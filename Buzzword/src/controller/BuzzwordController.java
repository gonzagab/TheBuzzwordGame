package controller;

import apptemplate.AppTemplate;
import gui.Workspace;

import java.io.IOException;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public class BuzzwordController implements FileController
{
	private AppTemplate app;			//reference to the app
	//private Workspace	guiWorkspace;	//Workspace reference

	public BuzzwordController(AppTemplate app)
	{
		this.app = app;
	}
	@Override
	public void handleNewRequest()
	{
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		guiWorkspace.setupNewProfileGUI();
	}
	@Override
	public void handleSaveRequest() throws IOException
	{

	}
	@Override
	public void handleLoadRequest() throws IOException
	{

	}
	@Override
	public void handleExitRequest()
	{

	}
	@Override
	public void handleLoginRequest()
	{
		Workspace guiWorkspace = (Workspace)app.getWorkspaceComponent();
		guiWorkspace.activateLoginScreen();
	}

	public void makeNewAccount()
	{

	}
}
