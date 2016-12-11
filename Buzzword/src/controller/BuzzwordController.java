package controller;

import apptemplate.AppTemplate;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import data.GameData;
import data.GameDataFile;
import gamelogic.GameMode;
import gamelogic.UserProfile;
import gui.Workspace;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import propertymanager.PropertyManager;
import ui.AppGUI;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;

import java.io.*;
import java.util.LinkedList;
import java.util.Stack;

import static gamelogic.GameMode.*;
import static settings.AppPropertyType.*;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public class BuzzwordController implements FileController
{
	private AppTemplate	app;		//reference to the app
	private GameData 	gameData;
	//Flags indicating wats going on through out the game
	private boolean 	isPlaying; 		//Indicates if a game is currently in play mode
	private boolean 	startedPlaying;	//indicate if a game has started
	private boolean 	savable; 		//indicate whether savable or not
	private Timeline 	timeline;
	private int 		counter;
	//used to verify word input
	LinkedList<StackPane>	wordInProgress;	//Nodes tha have been visited
	StackPane				preNode;
	/*/**************************
	 ********CONSTRUCTOR*********
	 ****************************/
	public BuzzwordController(AppTemplate app)
	{
		this.app = app;
		gameData = (GameData) app.getDataComponent();
		isPlaying = false;
		startedPlaying = false;
		counter = gameData.getTimeAllowed();
		//SETUP TIME COUNT
		timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> counterMethod()));
		timeline.setCycleCount(counter);
		//setup variables needed
		wordInProgress = new LinkedList<>();
	}
	@Override
	public void handleSaveRequest() throws IOException
	{

	}
	@Override
	public void handleExitRequest()
	{
		System.out.println("Exist request");
		if(isPlaying)
		{
			timeline.pause();
			isPlaying = false;
			((Workspace)app.getWorkspaceComponent()).isPlayingSetup(false);
		}
		try
		{
			boolean exit = true;
			if(savable)
				exit = promptToSave();
			if(startedPlaying)
				exit = promptForClosing();
			if(exit)
				System.exit(0);
		}
		catch(IOException ioe)
		{
			AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
			PropertyManager props = PropertyManager.getManager();
			dialog.show(props.getPropertyValue(SAVE_ERROR_TITLE), props.getPropertyValue(SAVE_ERROR_MESSAGE));
		}
	}
	/*/************************
	 **LOGIN WITH NEW ACCOUNT**
	 **************************/
	@Override
	public void handleNewRequest()
	{
		Workspace guiWorkspace = (Workspace) app.getWorkspaceComponent();
		guiWorkspace.loadNewProfileGUI();
	}
	public void makeNewAccount()
	{
		//CREATE APP MESSAGE BOX
		AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
		//PROPERTY MANAGER THAT HAS MESSAGE
		PropertyManager propertyManager = PropertyManager.getManager();
		//GET REFERENCE TO USER FIELD AND PASSWORD FIELD
		AppGUI appGUI = app.getGUI();                                //reference to gui
		Pane workspacePane = (Pane) appGUI.getAppPane().getCenter();    //reference to workspace pane
		TextField usernameField = (TextField) workspacePane.getChildren().get(0);    //reference to fields
		TextField passwordField = (TextField) workspacePane.getChildren().get(1);
		//CHECK THAT USERNAME AND PASSWORD ARE GOOD ENTRIES
		String username = usernameField.getText();
		if(username.equals("") || username == null)
		{
			messageDialog.show(propertyManager.getPropertyValue(EMPTY_FIELD_TITLE),
					propertyManager.getPropertyValue(EMPTY_FIELD_ERROR_MESSAGE));
			return;
		}
		String password = passwordField.getText();
		if(password.equals("") || password == null || password.length() < 5)
		{
			messageDialog.show(propertyManager.getPropertyValue(PSSWORD_FIELD_TITLE),
					propertyManager.getPropertyValue(PSSWRD_FIELD_ERROR_MESSAGE));
			return;
		}
		//USER USERNAME AND PASSWORD TO CREATE NEW USER PROFILE AND SET NEW USER
		((GameData) app.getDataComponent()).setUser(new UserProfile(username, password));
		//LOGIN AND LOAD LOGGED IN GUI
		((Workspace) app.getWorkspaceComponent()).loadLoggedInHomeGUI();
		//SAVE NEW USER
		File userFile = new File("Buzzword/saved/" + username + ".json");
		try
		{
			BufferedWriter bf = new BufferedWriter(new FileWriter(userFile));
			app.getFileComponent().saveData(app.getDataComponent(), userFile.toPath());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/*/*****************************
	 **LOGIN WITH EXISTING ACCOUNT**
	 *******************************/
	@Override
	public void handleLoginRequest()
	{
		//SHOW LOG IN SCREEN
		Workspace guiWorkspace = (Workspace) app.getWorkspaceComponent();
		guiWorkspace.ensureHomeScreen();            //ensure to be at the home screen
		guiWorkspace.activateLoginScreen(true);
		//CHANGE THE BUTTONS HANDLER; SO THAT WHE PRESSED AGAIN IT WILL LOGIN THE USER
		Button loginButton = (Button) app.getGUI().getSidebarPane().getChildren().get(0);
		loginButton.setOnAction(e -> login());
	}
	public void login()
	{
		//CHECK THAT SOMETHING WAS INPUT TO FIELDS AND THAT FIELDS SCENE IS CURRENTLY SHOWING
		Workspace guiWorkspace = (Workspace) app.getWorkspaceComponent();
		AppGUI appGUI = app.getGUI();
		AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
		PropertyManager propertyManager = PropertyManager.getManager();
		try
		{
			guiWorkspace.activateLoginScreen(true);
			//IF EVERYTHING IS OK THEN GET INFO IN FIELDS
			StackPane gridLayover = (StackPane) appGUI.getAppPane().getCenter();
			StackPane loginLayover = (StackPane) gridLayover.getChildren().get(1);
			GridPane loginFields = (GridPane) loginLayover.getChildren().get(1);
			//REFERENCES TO FIELDS
			TextField usernameField = (TextField) loginFields.getChildrenUnmodifiable().get(2);    //reference to fields
			TextField passwordField = (TextField) loginFields.getChildrenUnmodifiable().get(3);
			//CHECK THAT USERNAME AND PASSWORD ARE GOOD ENTRIES
			String username = usernameField.getText();
			if(username.equals("") || username == null)
			{
				messageDialog.show(propertyManager.getPropertyValue(EMPTY_FIELD_TITLE),
						propertyManager.getPropertyValue(EMPTY_FIELD_ERROR_MESSAGE));
				throw new IllegalArgumentException();
			}
			String password = passwordField.getText();
			if(password.equals("") || password == null || password.length() < 5)
			{
				messageDialog.show(propertyManager.getPropertyValue(PSSWORD_FIELD_TITLE),
						propertyManager.getPropertyValue(PSSWRD_FIELD_ERROR_MESSAGE));
				throw new IllegalArgumentException();
			}
			//CHECK THAT USER EXISTS
			File userFile = new File("Buzzword/saved/" + username + ".json");
			app.getFileComponent().loadData(app.getDataComponent(), userFile.toPath());
			//CHECK THAT PASSWORD IS CORRECT FOR USER
			if(!password.equals(((GameData) app.getDataComponent()).getUser().getPassword()))
			{
				System.out.println("Password fucked");
				passwordField.clear();
				throw new IOException();
			}
			else
			{
				Button loginButton = (Button) app.getGUI().getSidebarPane().getChildren().get(0);
				guiWorkspace.loadLoggedInHomeGUI();
				usernameField.clear();
				passwordField.clear();
				loginButton.setOnAction(e -> handleLoginRequest());
			}
		}
		//IF NOT THEN RETURN CONTROL TO HANDLE LOGIN REQUEST
		catch(IOException e)        //when a matching json file can't be found
		{
			messageDialog.show(propertyManager.getPropertyValue(PSSWRD_NAME_TITLE),
					propertyManager.getPropertyValue(PSSWRD_NAME_ERROR));
			guiWorkspace.activateLoginScreen(true);
		}catch(ClassCastException ex)    //for whe switching between login and new user
		{
			Button loginButton = (Button) app.getGUI().getSidebarPane().getChildren().get(0);
			loginButton.setOnAction(e -> handleLoginRequest());
			handleLoginRequest();
		}catch(IllegalArgumentException ill)
		{
			guiWorkspace.activateLoginScreen(true);
		}catch(Exception er)    //just in case for everything else
		{
			er.printStackTrace();
		}
	}
	/*/**************************
	 ***MODE SELECTOR LISTENER***
	 ****************************/
	public void handleLevelSelect(int selected)
	{
		UserProfile user = gameData.getUser();
		GameMode mode = GameMode.values()[selected];
		gameData.setCurrentMode(mode);
		((Workspace) app.getWorkspaceComponent()).loadModeSelectorGUI(mode, mode.totalLevels(), user.getModeProgress(mode));
	}
	/*/***************************
	 ****HOME / LOGOUT REQUESTS***
	 *****************************/
	public void handleLogoutRequest()
	{
		if(startedPlaying)
		{
			//VARIABLES NEEDED TO RESET LEVEL
			counter = gameData.getTimeAllowed();
			isPlaying = false;
			startedPlaying = false;
			//RESET TIMER
			timeline.stop();
		}
		app.getWorkspaceComponent().reloadWorkspace();
	}
	public void handleHomeRequest()
	{
		//VARIABLES NEEDED TO RESET LEVEL
		GameMode currentMode 	= gameData.getCurrentMode();
		UserProfile user 		= gameData.getUser();
		counter					= gameData.getTimeAllowed();
		isPlaying				= false;
		startedPlaying			= false;
		//RESET TIMER
		timeline.stop();
		timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> counterMethod()));
		timeline.setCycleCount(counter);
		//RESET HOME GUI - SIDEBAR
		((Workspace) app.getWorkspaceComponent()).reloadHomeGuiSidebar();
		//RESET HOME GUI - LEVEL SELECTOR
		((Workspace) app.getWorkspaceComponent()).loadModeSelectorGUI(
				currentMode, currentMode.totalLevels(), user.getModeProgress(currentMode));
	}
	/*/********************
	 ***PLAYING HANDLERS***
	 **********************/
	public void play()
	{
		Workspace guiWorkspace = (Workspace) app.getWorkspaceComponent();
		isPlaying = !isPlaying;
		guiWorkspace.isPlayingSetup(isPlaying);
		if(!isPlaying)
			timeline.pause();
		else
			timeline.play();
	}
	public void nodeSelected(StackPane gridPiece)
	{
		//check that the player is actually playing
		if(!isPlaying)
			return;
		//check if user is trying to go back
		if(gridPiece == preNode )
		{
			((Workspace)app.getWorkspaceComponent()).rstWrdSlctOnGui(wordInProgress.removeLast());
			StackPane temp = wordInProgress.removeLast();
			preNode = wordInProgress.peekLast();
			wordInProgress.add(temp);
			System.out.println("Handle reverse movement");
			((Workspace)app.getWorkspaceComponent()).clrWrdSlctDsp(false);
			return;
		}
		//check for cycles
		if(wordInProgress.contains(gridPiece))
			return;
		//check that node is adjacent.
		//all test passed
		System.out.println("Letter: " + gridPiece.getChildren().get(1).toString() +  " Activated");
		preNode = wordInProgress.peekLast();
		wordInProgress.add(gridPiece);
		((Workspace)app.getWorkspaceComponent()).updateWrdSlctDsp(((Label)gridPiece.getChildren().get(1)).getText());
		((Workspace)app.getWorkspaceComponent()).updateWrdSlctOnGui(gridPiece);
	}
	public void dragEnd(String wordFound)
	{
		if(gameData.addFoundWord(wordFound))
			((Workspace)app.getWorkspaceComponent()).updateWrdFndDsp(wordFound);
		((Workspace)app.getWorkspaceComponent()).clrWrdSlctDsp(true);
		((Workspace)app.getWorkspaceComponent()).rstWrdSlctOnGui(null);
		wordInProgress.clear();
		preNode = null;
		System.out.println("Drag done");
	}
	public void counterMethod()
	{
		counter--;
		((Workspace)app.getWorkspaceComponent()).updateTimerDisplay(counter);
	}
	/*/**********************
	 *****PROMPT METHODS*****
	 ************************/
	private boolean promptToSave() throws IOException
	{
		YesNoCancelDialogSingleton wannaClose = YesNoCancelDialogSingleton.getSingleton();

		wannaClose.init(app.getGUI().getWindow());
		wannaClose.show("Leave?", "Would you like to save before continuing?");

		String tmp = wannaClose.getSelection();
		if(tmp.equals("Yes"))
			handleSaveRequest();
		else
			return false;
		return true;
	}
	public boolean promptForClosing()
	{
		YesNoCancelDialogSingleton wannaClose = YesNoCancelDialogSingleton.getSingleton();

		wannaClose.init(app.getGUI().getWindow());
		wannaClose.show
				("Currently Playing", "Would you like to close the application even though you are currently playing a game?");

		String tmp = wannaClose.getSelection();
		if(tmp.equals("Yes"))
			return true;
		return false;
	}
	/*/**********************
	 *****SETTER METHODS*****
	 ************************/
	public void isPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}
	public void startedPlaying(boolean startedPlaying)
	{
		this.startedPlaying = startedPlaying;
	}
}
