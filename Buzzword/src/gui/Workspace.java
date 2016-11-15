package gui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzwordController;
import gamelogic.GameModes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import propertymanager.PropertyManager;
import ui.AppGUI;

import static gamelogic.GameModes.*;
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
		//SETUP HEADER
		gameHeader = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADER_LABEL));
		gui.getAppPane().setTop(gameHeader);
		gui.getAppPane().setAlignment(gameHeader, Pos.CENTER);
		//SET UP THE LOGIN AREA AND FIELDS
		StackPane loginLayoverPane = setupLoginLayoverPane();
		loginLayoverPane.setPadding(new Insets(30, 0, 0, 50));
		//SETUP LOGO GRID
		GridPane logo = setupLogoGrid();
		logo.setPadding(new Insets(30, 0, 0, 50));
		//CREATE A HOME PANE THAT HAS THE LOGO AND AN INVISIBLE LOGIN AREA
		StackPane homePane = new StackPane(logo, loginLayoverPane);
		//SET THE HOME PANE IN THE CENTER
		gui.getAppPane().setCenter(homePane);
		//INITIALIZE BUTTONS AND OTHER THINGS - EVEN IF NOT ON GUI YET
		newAccountBttn	= new Button("Create Account");
		logoutButton	= new Button("Log Out");
		homeButton		= new Button("Home");
		gameModeMenu	= new ChoiceBox();
	}
	public void setupHandlers()
	{
		BuzzwordController controller = (BuzzwordController)gui.getFileController();
		newAccountBttn.setOnAction(e -> controller.makeNewAccount());
		logoutButton.setOnAction(e -> controller.handleLogoutRequest());
		//ADD A LISTENER TO THE DROP DOWN MENU
		gameModeMenu.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				controller.handleLevelSelect(newValue.intValue());
			}
		});
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
		GridPane layout = new GridPane();
		gui.getAppPane().setCenter(layout);
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
	}
	public void setupLoggedInHomeGUI()
	{
		//SETUP SIDEBAR
		VBox sidebarPane = gui.getSidebarPane();
		//SETUP USER LABEL
		Rectangle userLabel = new Rectangle(150, 30);
		userLabel.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));
		//STYLE LOG OUT BUTTON
		logoutButton.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));
		logoutButton.setMinSize(150, 30);
		//ADD IT ALL TO THE SIDEBAR
		sidebarPane.getChildren().setAll(userLabel, gameModeMenu, logoutButton);
		//SETUP DROP DOWN MENU FOR GAME MODE
		gameModeMenu.getItems().setAll(FAMOUS_PEOPLE.getVal(),
				ENGLISH_DICTIONARY.getVal(), PLACES.getVal(), SCIENCE.getVal());
		gameModeMenu.setValue(SCIENCE.getVal());
		gameModeMenu.setTooltip(new Tooltip(propertyManager.getPropertyValue(GAME_MODE_MENU_TOOLTIP)));
		//CLEAR GUI WORKSPACE
		//gui.getAppPane().getCenter().
	}
	public void activateLoginScreen(boolean visible)
	{
		StackPane gridlayover = (StackPane)gui.getAppPane().getCenter();
		gridlayover.getChildren().get(1).setVisible(visible);
	}
	public void ensureHomeScreen()
	{
		StackPane loginLayoverPane = setupLoginLayoverPane();
		loginLayoverPane.setPadding(new Insets(30, 0, 0, 50));
		//SETUP LOGO GRID
		GridPane logo = setupLogoGrid();
		logo.setPadding(new Insets(30, 0, 0, 50));
		//CREATE A HOME PANE THAT HAS THE LOGO AND AN INVISIBLE LOGIN AREA
		StackPane homePane = new StackPane(logo, loginLayoverPane);
		//SET THE HOME PANE IN THE CENTER
		gui.getAppPane().setCenter(homePane);
	}
	public StackPane setupLoginLayoverPane()
	{
		//STACK PANE FOR LOGIN AREA AND FIELDS
		StackPane loginLayoverPane = new StackPane();
		//LOGIN AREA
		Rectangle loginBackground = new Rectangle(325, 150, Paint.valueOf("black"));
		loginBackground.setOpacity(.8);
		//GRID PANE FOR FIELDS AND LABELS
		GridPane loginFields = new GridPane();
		//SETUP TEXT FIELD LABELS AND STYLE THEM
		Label name = new Label("Username");
		Label passwrd = new Label("Password");
		name.getStyleClass().setAll(propertyManager.getPropertyValue(LABEL_STYLE));
		passwrd.getStyleClass().setAll(propertyManager.getPropertyValue(LABEL_STYLE));
		//SETUP TEXT FIELDS
		TextField username = new TextField();
		TextField password = new TextField();
		//STYLE TEXT FIELDS
		username.getStyleClass().setAll(propertyManager.getPropertyValue(TEXTFIELD_STYLE));
		password.getStyleClass().setAll(propertyManager.getPropertyValue(TEXTFIELD_STYLE));
		username.setMaxSize(150, 30);
		password.setMaxSize(150, 30);
		//ADD FIELDS TO GRID PANE
		loginFields.add(name	, 0, 0);
		loginFields.add(passwrd	, 0, 1);
		loginFields.add(username, 1, 0);
		loginFields.add(password, 1, 1);
		//SET PADDING FOR GRID PANE
		loginFields.setPadding(new Insets(30));
		loginFields.setVgap(20);
		loginFields.setHgap(10);
		//LETS ADD THE FIELDS TO THE STACK PANE
		loginLayoverPane.getChildren().addAll(loginBackground, loginFields);
		loginLayoverPane.setAlignment(loginBackground, Pos.TOP_LEFT);
		loginLayoverPane.setVisible(false);
		return loginLayoverPane;		//return finished invisible pane
	}
	public void loadGUIforMode(GameModes mode, int totalLevels, int levelsCompleted)
	{
		//SETUP SUB HEADER
		Label subTitle = new Label(mode.getVal());
		//CREATE LEVEL FLOE PANE
		FlowPane levelDisplayPane = new FlowPane();
		StackPane gridPiece;
		Circle container;
		Text number;

		for(int j = 0; j < totalLevels; j++)
		{
			//LEVEL BUBBLE GRAPHIC
			if(j <= levelsCompleted)
				container = new Circle(30, Paint.valueOf("white"));
			else
				container = new Circle(30, Paint.valueOf("grey"));
			number = new Text(String.valueOf(j));
			number.getStyleClass().setAll(propertyManager.getPropertyValue(LETTER_STYLE));
			gridPiece = new StackPane(container, number);
			//ADD TO PANE
			levelDisplayPane.getChildren().add(gridPiece);
			levelDisplayPane.setMargin(gridPiece, new Insets(10));
		}
		VBox levelSelectPane = new VBox(subTitle, levelDisplayPane);
		gui.getAppPane().setCenter(levelSelectPane);
	}
}
