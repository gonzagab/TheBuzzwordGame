package controller;

import apptemplate.AppTemplate;
import gui.Workspace;
import javafx.scene.control.Button;
import ui.AppGUI;

import java.io.IOException;

import static gamelogic.GameMode.*;

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
		//IF NOT THEN RETURN CONTROL TO HANDLE LOGIN REQUEST
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		try
		{
			guiWorkspace.activateLoginScreen(false);
			guiWorkspace.loadLoggedInHomeGUI();
		}
		catch(Exception ex)
		{
			Button loginButton = (Button) app.getGUI().getSidebarPane().getChildren().get(0);
			loginButton.setOnAction(e -> handleLoginRequest());
			handleLoginRequest();
		}
	}
	public void makeNewAccount()
	{

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
