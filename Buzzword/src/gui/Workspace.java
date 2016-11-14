package gui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzwordController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import propertymanager.PropertyManager;
import ui.AppGUI;

import static gui.BuzzwordProperties.*;

/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class Workspace extends AppWorkspaceComponent
{
    AppTemplate appTemplate;    	//reference to the actual app
    AppGUI      gui;            	//access to the app gui
    //Workspace GUI Objects
    Label       gameHeader;     	//the header of the game
	Button 		newAccountBttn;		//Button for users to create a new account
	Button		logoutButton;		//Button that will log user out
	Button		startPlayingButton;	//Button to start playing a level
	Button		homeButton;			//Sends the user home
	ChoiceBox<String>	gameModeMenu;		//Holds all the modes of the game
	//Property Manager
	PropertyManager propertyManager = PropertyManager.getManager();

	public Workspace(AppTemplate app)
	{
		appTemplate = app;
		gui = app.getGUI();
		setupGUI();
		setupHandlers();
	}
	public void setupGUI()
	{
		//LOGIN AREA
		Rectangle loginBackground = new Rectangle(350, 200, Paint.valueOf("black"));
		loginBackground.setOpacity(.5);
		loginBackground.setVisible(false);		//only set visible when login button is pressed
		StackPane gridLoginLayover = new StackPane();
		//SETUP HEADER
		gameHeader = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADER_LABEL));
		gui.getAppPane().setTop(gameHeader);
		gui.getAppPane().setAlignment(gameHeader, Pos.CENTER);
		//SETUP LOGO GRID
		GridPane logo = setupLogoGrid();
		gridLoginLayover.getChildren().addAll(logo, loginBackground);
		gridLoginLayover.setAlignment(loginBackground,Pos.TOP_LEFT);
		gridLoginLayover.setAlignment(logo,Pos.TOP_LEFT);
		gridLoginLayover.setPadding(new Insets(30, 0, 0, 50));
		gui.getAppPane().setCenter(gridLoginLayover);
		//INITIALIZE BUTTONS AND OTHER THINGS - EVEN IF NOT ON GUI YET
		newAccountBttn	= new Button("Create Account");
		logoutButton	= new Button("Log Out");
		gameModeMenu	= new ChoiceBox();
		gameModeMenu.getItems().addAll("Item 1", "Item 2", "Item 3");
	}
	public void setupHandlers()
	{
		BuzzwordController controller = (BuzzwordController)gui.getFileController();
		newAccountBttn.setOnMouseClicked(e -> controller.makeNewAccount());
	}
    @Override
    public void initStyle()
    {
        ObservableList<Node> sidebarChildren = gui.getSidebarPane().getChildren();
        sidebarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));
        sidebarChildren.get(1).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));
		sidebarChildren.get(2).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));

        gameHeader.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
    }
    @Override
    public void reloadWorkspace()
    {

    }
    private GridPane setupLogoGrid()
	{
		GridPane homeGrid = new GridPane();
		StackPane gridPiece;
		Circle container;
		Text[][] letter = {
				{new Text("B"), new Text("U"), new Text(""), new Text()},
				{new Text("Z"), new Text("Z"), new Text(""), new Text()},
				{new Text(""), new Text(""), new Text("W"), new Text("O")},
				{new Text(""), new Text(""), new Text("R"), new Text("D")}};
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				container = new Circle(30, Paint.valueOf("white"));
				letter[j][i].getStyleClass().setAll(propertyManager.getPropertyValue(LETTER_STYLE));
				gridPiece = new StackPane(container, letter[j][i]);
				homeGrid.add(gridPiece, i, j);
				homeGrid.setMargin(gridPiece, new Insets(10));
			}
		}
		return homeGrid;
	}
	public void setupNewProfileGUI()
	{
		//GET PANE TO WORK IN
		Pane workspace = (Pane) gui.getAppPane().getCenter();
		GridPane layout = new GridPane();
		workspace.getChildren().setAll(layout);
		//SET UP TEXT FIELDS
		TextField nameInput = new TextField();
		TextField passwordInput = new TextField();
		//SET UP PROMPT TEXT
		nameInput.setPromptText("Username");
		passwordInput.setPromptText("Password");
		//SET UP SPACING AND PADDING
		layout.setPadding(new Insets(50));
		layout.setVgap(20);
		//SETUP BUTTON STYLE AND SIZE
		newAccountBttn.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));
		newAccountBttn.setAlignment(Pos.CENTER);
		newAccountBttn.setMinWidth(150);
		newAccountBttn.setMaxWidth(155);
		//ADD EVERYTHING INTO THE LAYOUT
		layout.add(nameInput, 0, 0);
		layout.add(passwordInput, 0, 1);
		layout.add(newAccountBttn, 0, 2);
		layout.setAlignment(Pos.CENTER);
	}
	public void setupLoggedInHomeGUI()
	{
		//SETUP SIDEBAR
		VBox sidbarPane = gui.getSidebarPane();
		//SETUP USER LABEL
		Rectangle userLabel = new Rectangle(150, 30);
		userLabel.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));
		//STYLE LOG OUT BUTTON
		logoutButton.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));
		//ADD IT ALL TO THE SIDEBAR
		sidbarPane.getChildren().setAll(userLabel, gameModeMenu, logoutButton);
	}
	public void activateLoginScreen()
	{
		StackPane gridlayover = (StackPane)gui.getAppPane().getCenter();
		gridlayover.getChildren().get(1).setVisible(true);
		VBox loginFields = new VBox();
		//SETUP TEXT FIELDS
		TextField username = new TextField();
		TextField password = new TextField();
		username.setMaxSize(150, 30);
		password.setMaxSize(150, 30);
		username.setPromptText("Username");
		password.setPromptText("Password");
		//ADD FIELDS TO VBOX
		loginFields.getChildren().addAll(username, password);
		//SET PADDING FOR VBOX
		loginFields.setPadding(new Insets(30));
		gridlayover.getChildren().add(loginFields);
	}
}
