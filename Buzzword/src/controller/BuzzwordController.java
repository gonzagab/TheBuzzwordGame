package controller;

import apptemplate.AppTemplate;
import data.GameData;
import gamelogic.UserProfile;
import gui.Workspace;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import propertymanager.PropertyManager;
import ui.AppGUI;
import ui.AppMessageDialogSingleton;

import java.io.IOException;

import static gamelogic.GameMode.*;
import static settings.AppPropertyType.*;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public class BuzzwordController implements FileController
{
	private AppTemplate app;			//reference to the app
	private boolean		isPlaying;		//Indicates if a game is currently being played

	public BuzzwordController(AppTemplate app)
	{
		this.app = app;
		isPlaying = false;
	}
	@Override
	public void handleNewRequest()
	{
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		guiWorkspace.loadNewProfileGUI();
	}
	@Override
	public void handleSaveRequest() throws IOException
	{

	}
	@Override
	public void handleExitRequest()
	{

	}
	@Override
	public void handleLoginRequest()
	{
		//SHOW LOG IN SCREEN
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		guiWorkspace.ensureHomeScreen();			//ensure to be at the home screen
		guiWorkspace.activateLoginScreen(true);
		//CHANGE THE BUTTONS HANDLER; SO THAT WHE PRESSED AGAIN IT WILL LOGIN THE USER
		Button loginButton = (Button) app.getGUI().getSidebarPane().getChildren().get(0);
		loginButton.setOnAction(e -> login());
	}
	public void login()
	{
		//CHECK THAT SOMETHING WAS INPUT TO FIELDS AND THAT FIELDS SCENE IS CURRENTLY SHOWING
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		AppGUI appGUI = app.getGUI();
		AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
		PropertyManager propertyManager = PropertyManager.getManager();
		try
		{
			guiWorkspace.activateLoginScreen(false);
			//IF EVERYTHING IS OK THEN GET INFO IN FIELDS
			StackPane gridLayover	= (StackPane)appGUI.getAppPane().getCenter();
			StackPane loginLayover	= (StackPane)gridLayover.getChildren().get(1);
			GridPane  loginFields	= (GridPane)loginLayover.getChildren().get(1);
			//REFERENCES TO FIELDS
			TextField usernameField = (TextField)loginFields.getChildrenUnmodifiable().get(2);	//reference to fields
			TextField passwordField = (TextField)loginFields.getChildrenUnmodifiable().get(3);
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
			//CHECK THAT PASSWORD IS CORRECT FOR USER
			((GameData)app.getDataComponent()).setUser(new UserProfile(username, password));
			guiWorkspace.loadLoggedInHomeGUI();
		}
		//IF NOT THEN RETURN CONTROL TO HANDLE LOGIN REQUEST
		catch(Exception ex)
		{
			Button loginButton = (Button) app.getGUI().getSidebarPane().getChildren().get(0);
			loginButton.setOnAction(e -> handleLoginRequest());
			handleLoginRequest();
		}
	}
	public void makeNewAccount()
	{
		//CREATE APP MESSAGE BOX
		AppMessageDialogSingleton 	messageDialog 	= AppMessageDialogSingleton.getSingleton();
		//PROPERTY MANAGER THAT HAS MESSAGE
		PropertyManager				propertyManager	= PropertyManager.getManager();
		//GET REFERENCE TO USER FIELD AND PASSWORD FIELD
		AppGUI appGUI = app.getGUI(); 								//reference to gui
		Pane workspacePane = (Pane)appGUI.getAppPane().getCenter();	//reference to workspace pane
		TextField usernameField = (TextField)workspacePane.getChildren().get(0);	//reference to fields
		TextField passwordField = (TextField)workspacePane.getChildren().get(1);
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
		((GameData)app.getDataComponent()).setUser(new UserProfile(username, password));
		//LOGIN AND LOAD LOGGED IN GUI
		((Workspace)app.getWorkspaceComponent()).loadLoggedInHomeGUI();
	}
	public void handleLogoutRequest()
	{

	}
	public void handleLevelSelect(int selected)
	{
		AppGUI gui = app.getGUI();

		if(selected == 0)
			((Workspace)app.getWorkspaceComponent()).loadModeSelectorGUI(FAMOUS_PEOPLE, 10, 1);
		else if(selected == 1)
			((Workspace)app.getWorkspaceComponent()).loadModeSelectorGUI(ENGLISH_DICTIONARY, 10, 2);
		else if(selected == 2)
			((Workspace)app.getWorkspaceComponent()).loadModeSelectorGUI(PLACES, 10, 4);
		else if(selected == 3)
			((Workspace)app.getWorkspaceComponent()).loadModeSelectorGUI(SCIENCE, 10, 7);
	}
	public void play()
	{
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		isPlaying = !isPlaying;
		guiWorkspace.isPlayingSetup(isPlaying);
	}
}
