package controller;

import apptemplate.AppTemplate;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import data.GameData;
import gamelogic.GameMode;
import gamelogic.LetterNode;
import gamelogic.UserProfile;
import gui.Workspace;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import propertymanager.PropertyManager;
import ui.AppGUI;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
	private LinkedList<StackPane>	wordInProgress;	//Nodes tha have been visited
	private StackPane				preNode;
	private ArrayList<String>		typedLetters;
	private Set[] 					visitedKNodes;
	boolean endOfWord;
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
		timeline.setOnFinished(event -> handleEndGame());
		timeline.setCycleCount(counter);
		//setup variables needed
		wordInProgress = new LinkedList<>();
		typedLetters = new ArrayList<>();
		visitedKNodes = new HashSet[5];
		for(int i = 0; i < 5; i++)
			visitedKNodes[i] = new HashSet<>();
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
			counter 		= gameData.getTimeAllowed();
			isPlaying 		= false;
			startedPlaying 	= false;
			//RESET TIMER
			timeline.stop();
			//RESET GAME DATA
			gameData.reset();
			//RESET FOUND WORDS
			((TextArea)((VBox)app.getGUI().getAppPane().getRight()).getChildren().get(4)).clear();
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
		timeline.setOnFinished(event -> handleEndGame());
		timeline.setCycleCount(counter);
		//RESET GAME DATA
		gameData.reset();
		//RESET FOUND WORDS
		((TextArea)((VBox)app.getGUI().getAppPane().getRight()).getChildren().get(4)).clear();
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
			((Workspace)app.getWorkspaceComponent()).clrWrdSlctDsp(false);
			return;
		}
		//check for cycles
		if(wordInProgress.contains(gridPiece))
			return;
		//check that node is adjacent
		int indexOfPreNode = ((GridPane)((VBox)app.getGUI().getAppPane().getCenter()).getChildren().get(1)).getChildrenUnmodifiable().indexOf(wordInProgress.peekLast());
		int indexOfCurNode = ((GridPane)((VBox)app.getGUI().getAppPane().getCenter()).getChildren().get(1)).getChildrenUnmodifiable().indexOf(gridPiece);
		boolean nodeIsAdjacent = false;
		try
		{
			LetterNode preLetterNode = gameData.getPlayingGrid().get(indexOfPreNode);
			LetterNode curLetterNode = gameData.getPlayingGrid().get(indexOfCurNode);
			if(preLetterNode.isAdjacent(curLetterNode))
				nodeIsAdjacent = true;
		}catch(Exception e)
		{
		}
		//if all test are passed
		if(nodeIsAdjacent || wordInProgress.isEmpty())
		{
			//all test passe
			preNode = wordInProgress.peekLast();
			wordInProgress.add(gridPiece);
			((Workspace) app.getWorkspaceComponent()).updateWrdSlctDsp(((Label) gridPiece.getChildren().get(1)).getText());
			((Workspace) app.getWorkspaceComponent()).updateWrdSlctOnGui(gridPiece);
		}
	}
	public void handleKeyTyped(KeyEvent event)
	{
		//check that the player is actually playing
		if(!isPlaying)
			return;
		//get the letter that was typed
		char guess = event.getCode().toString().charAt(0);
		//if its lower case turn it to upper case
		if(guess > 96 && guess < 123)
			guess -= 32;
			//if key typed is enter
		if(event.getCode().equals(KeyCode.ENTER))
		{
			System.out.println("Entered Key Pressed");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < typedLetters.size(); i++)
				sb.append(typedLetters.toArray()[i]);
			dragEnd(sb.toString());
			typedLetters = new ArrayList<>();
			for(int i = 0; i < visitedKNodes.length; i++)
				visitedKNodes[i] = new HashSet<>();
		}
		//if its and upper case letter then lets update grid
		else if(guess > 64 && guess < 91)
		{
			//added newly pressed key to array of letters pressed
			typedLetters.add(String.valueOf(guess));
			//look for solutions
			ArrayList<LetterNode> playingGrid = gameData.getPlayingGrid();
			LetterNode currentNode;
			for(int i = 0; i < 5; i++)
				visitedKNodes[i] = new HashSet<>();
			//END OF WORD RESET; WORD HAS NOT REACHED THE END
			endOfWord = false;
			boolean wordPossible = true;
			boolean endOfWordNeverReached = true;
			//IF ANY OF THE LETTERS IN A WORD ARE NOT IN THE GRID THEN, NEXT WORD
			for(int j = 0; j < typedLetters.size(); j++)
				if(!playingGrid.contains(new LetterNode((typedLetters.get(j).charAt(0) ))) )
				{
					wordPossible = false; ///WORD CAN NOT BE IN GRID
					break;
				}
			if(wordPossible)
			{
				//initialize starting point
				int index = 0;
				int i = 0;
				while(playingGrid.get(index).getLetter() != typedLetters.get(0).charAt(0))
					index++;
				while(index < 16)
				{
					currentNode = playingGrid.get(index);
					//RESET VISITED NODES FOR WORD
					nextNode(typedLetters.subList(1, typedLetters.size()), currentNode, i);
					if(endOfWord)
					{
						endOfWordNeverReached = false;
						Iterator iter = visitedKNodes[i].iterator();
						//highlight the word path found
						for(int j = 0; j < visitedKNodes[i].size(); j++)
							((Workspace)app.getWorkspaceComponent()).updateWrdSlctOnGui(((LetterNode)iter.next()).getIndexOfNode());
					}
					do
					{
						index++;
						if(index > 15)
							break;
					}while(playingGrid.get(index).getLetter() != typedLetters.get(0).charAt(0));
					i++;
				}
				if(endOfWordNeverReached)
					typedLetters.remove(typedLetters.size()-1);
			}
			else
				typedLetters.remove(typedLetters.size()-1);
		}
		else
			System.out.println(event.getCode());
	}
	public void dragEnd(String wordFound)
	{
		if(gameData.addFoundWord(wordFound))
		{
			((Workspace) app.getWorkspaceComponent()).updateWrdFndDsp(wordFound + "\t\t| " + wordFound.length()*2);
			((Workspace) app.getWorkspaceComponent()).updateScore(gameData.getCurrentScore());
		}
		((Workspace)app.getWorkspaceComponent()).clrWrdSlctDsp(true);
		((Workspace)app.getWorkspaceComponent()).rstWrdSlctOnGui(null);
		//clear variables used to keep track of word
		wordInProgress.clear();
		preNode = null;
	}
	public void counterMethod()
	{
		counter--;
		((Workspace)app.getWorkspaceComponent()).updateTimerDisplay(counter);
	}
	/*/**********************
	*****HANDLE END GAME*****
	************************/
	public void handleEndGame()
	{
		//get grid pane
		GridPane temp = (GridPane)((VBox)app.getGUI().getAppPane().getCenter()).getChildren().get(1);
		//disable grid pane so that no more drags are allowed
		for(int i = 0; i<16; i++)
			temp.getChildren().get(i).setDisable(true);
		//get end game pop up
		YesNoCancelDialogSingleton endMessage = YesNoCancelDialogSingleton.getSingleton();
		//set up text to go into pop up
		StringBuilder sb = new StringBuilder();
		Iterator<String> wf = gameData.getWordsFound().iterator();
		Iterator<String> gw = gameData.getGoodWords().iterator();
		//list found words
		sb.append("Words Found:\n");
		while(wf.hasNext())
			sb.append(wf.next() + "\n");
		//list all possible words in grid
		sb.append("All Possible Words in Grid:\n");
		while(gw.hasNext())
			sb.append(gw.next() + "\n");
		sb.append("Total Score: " + gameData.getCurrentScore());
		//CHECK TO SAVE
		boolean hasWon = gameData.getCurrentScore() >= gameData.getTargetScore();
		if(hasWon)
		{
			System.out.println("Winner winner chicken din din");
			if(gameData.getCurrentLevel() >= gameData.getUser().getModeProgress(gameData.getCurrentMode()))
				gameData.getUser().updateModeProgress(gameData.getCurrentMode(), gameData.getCurrentLevel()+1);
			File userFile = new File("Buzzword/saved/" + gameData.getUser().getUsername() + ".json");
			try
			{
				BufferedWriter bf = new BufferedWriter(new FileWriter(userFile));
				app.getFileComponent().saveData(app.getDataComponent(), userFile.toPath());
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		Platform.runLater(()->
		{
			if(hasWon)
				endMessage.setButtonText("Replay Level", "Next Level", "Quit");
			else
				endMessage.setButtonText("Replay Level", "Home", "Quit");
			endMessage.show("End of Game", sb.toString());
			endMessage.setButtonText("Yes", "No", "Cancel");
			String tempSelection = endMessage.getSelection();
			if(tempSelection.equals("Replay Level"))
			{
				counter					= gameData.getTimeAllowed();
				isPlaying				= false;
				startedPlaying			= false;
				//RESET TIMER
				timeline.stop();
				timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> counterMethod()));
				timeline.setOnFinished(event -> handleEndGame());
				timeline.setCycleCount(counter);
				//clear word select area
				((TextArea)((VBox)app.getGUI().getAppPane().getRight()).getChildren().get(2)).clear();
				//cleat words found area
				((TextArea)((VBox)app.getGUI().getAppPane().getRight()).getChildren().get(4)).clear();
				((Workspace) app.getWorkspaceComponent()).loadGameLevelGUI(
						gameData.getCurrentLevel(), gameData.getCurrentMode());
			}
			else if(tempSelection.equals("Next Level"))
			{
				counter					= gameData.getTimeAllowed();
				isPlaying				= false;
				startedPlaying			= false;
				//RESET TIMER
				timeline.stop();
				timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> counterMethod()));
				timeline.setOnFinished(event -> handleEndGame());
				timeline.setCycleCount(counter);
				//clear word select area
				((TextArea)((VBox)app.getGUI().getAppPane().getRight()).getChildren().get(2)).clear();
				//cleat words found area
				((TextArea)((VBox)app.getGUI().getAppPane().getRight()).getChildren().get(4)).clear();
				((Workspace) app.getWorkspaceComponent()).loadGameLevelGUI(
						gameData.getCurrentLevel() + 1, gameData.getCurrentMode());
			}
			else
			{
				handleHomeRequest();
			}
			System.out.println(endMessage.getSelection());
		});
		gameData.reset();
	}
	/*/**********************
	 *****PROMPT METHODS*****
	 ************************/
	private boolean promptToSave() throws IOException
	{
		YesNoCancelDialogSingleton wannaClose = YesNoCancelDialogSingleton.getSingleton();
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
		wannaClose.show
				("Currently Playing", "Would you like to close the application even though\nyou are currently playing a game?");
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
	/*/**********************
	 ****PRIVATE METHODS*****
	 ************************/
	private void nextNode(List<String> word, LetterNode currentNode, int i)
	{
		visitedKNodes[i].add(currentNode);
		//CHECK IF WE REACHED THE END
		if(word.isEmpty() || word == null)
			endOfWord = true;
		//ON THE WAY OUT
		if(endOfWord)
			return;
		//CHECK ALL ADJACENT NODES TO CURRENT NODE
		for(int k = 0; k < 8; k++)
		{
			//check for null adjacent nodes
			while(currentNode.getAdjacentNode(k) == null)
			{
				k++;
				if(k>7)
					break;
			}
			if(k>7)
				continue;
			//ADJACENT NODE FOUND
			if(currentNode.getAdjacentNode(k).getLetter() == word.get(0).charAt(0) && !visitedKNodes[i].contains(currentNode.getAdjacentNode(k)))
			{
				nextNode(word.subList(1, word.size()), currentNode.getAdjacentNode(k), i);
				//DID WE REACH THE END?
				if(endOfWord)
					return;
			}
		}
		visitedKNodes[i].remove(currentNode);
	}
}
