package gui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import controller.BuzzwordController;
import data.GameData;
import gamelogic.GameMode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import propertymanager.PropertyManager;
import ui.AppGUI;

import java.util.ArrayList;

import static gamelogic.GameMode.*;
import static gui.BuzzwordProperties.*;

/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class Workspace extends AppWorkspaceComponent
{
    AppTemplate 	appTemplate;    	//reference to the actual app
    AppGUI      	gui;            	//access to the app gui
    //Workspace GUI Objects
	Label       		gameHeader;     //the header of the game
	Button 				newAccountBttn;	//Button for users to create a new account
	Button				logoutButton;	//Button that will log user out
	Button				playButton;		//Button to start playing a level
	Button				homeButton;		//Sends the user home
	Rectangle			userLabel;		//Temporary space holder for username
	Label 				levelLabel;		//Label that displays the level
	Label 				subTitle;		//Sub header for mode
	ChoiceBox<String>	gameModeMenu;	//Holds all the modes of the game
	int count;
	//Property Manager
	PropertyManager propertyManager = PropertyManager.getManager();
	/*/***********************************
	 *************CONSTRUCTOR*************
	 *************************************/
	public Workspace(AppTemplate app)
	{
		appTemplate = app;
		gui = app.getGUI();
		count = 60;
		setupGUI();
		setupHandlers();
	}
    @Override
    public void reloadWorkspace()
    {

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
	public void doSomething(Text countText)
	{
		countText.setText("Time Remaining: " +count+" seconds");
		count--;
	}
	/*/***********************************************
	 *******************GUI LOADERS*******************
	 *************************************************/
	public void loadNewProfileGUI()
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
		newAccountBttn.setAlignment(Pos.CENTER);
		newAccountBttn.setMinWidth(150);
		newAccountBttn.setMaxWidth(155);
		//ADD EVERYTHING INTO THE LAYOUT
		layout.add(nameInput, 0, 0);
		layout.add(passwordInput, 0, 1);
		layout.add(newAccountBttn, 0, 2);
	}
	public void loadLoggedInHomeGUI()
	{
		//SETUP SIDEBAR
		VBox sidebarPane = gui.getSidebarPane();
		//SETUP USER LABEL
		userLabel.getStyleClass().setAll(propertyManager.getPropertyValue(USERNAME_HOLDER_STYLE));
		//STYLE LOG OUT BUTTON; SETUP BUTTON STYLE AND SIZE
		logoutButton.setAlignment(Pos.CENTER);
		//ADD IT ALL TO THE SIDEBAR
		sidebarPane.getChildren().setAll(userLabel, gameModeMenu, logoutButton);
		//SETUP DROP DOWN MENU FOR GAME MODE
		gameModeMenu.getItems().setAll(FAMOUS_PEOPLE.getLiteral(),
				ENGLISH_DICTIONARY.getLiteral(), PLACES.getLiteral(), SCIENCE.getLiteral());
		gameModeMenu.setValue(SCIENCE.getLiteral());
		gameModeMenu.setTooltip(new Tooltip(propertyManager.getPropertyValue(GAME_MODE_MENU_TOOLTIP)));
	}
	public void loadModeSelectorGUI(GameMode mode, int totalLevels, int levelsCompleted)
	{
		//SETUP SUB HEADER
		subTitle.setText(mode.getLiteral());
		//CREATE LEVEL FLOW PANE
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
			if(j <= levelsCompleted)
			{
				int finalJ = j;
				gridPiece.setOnMouseClicked(event -> loadGameLevelGUI(finalJ, mode));
			}
			levelDisplayPane.getChildren().add(gridPiece);
			levelDisplayPane.setMargin(gridPiece, new Insets(10));
		}
		VBox levelSelectPane = new VBox(subTitle, levelDisplayPane);
		gui.getAppPane().setCenter(levelSelectPane);
	}
	public void loadGameLevelGUI(int level, GameMode mode)
	{
		//SETUP SIDEBAR
		gui.getSidebarPane().getChildren().setAll(userLabel, homeButton);
		//SETUP HEADER
		subTitle.setText(mode.getLiteral());
		//SETUP PLAY GRID GUI
		GameData dataComponent = (GameData) appTemplate.getDataComponent();
		dataComponent.setCurrentLevel(level);
		dataComponent.setCurrentMode(mode);
		StackPane playGrid = setupGridLevel();
		playGrid.setPadding(new Insets(30, 0, 0, 50));
		//SETUP LEVEL LABEL
		levelLabel.setText("Level " + level);
		//ADD IT ALL TO THE V-BOX
		VBox centerPane = new VBox(subTitle, playGrid, levelLabel, playButton);
		//SETUP CENTER PANE
		gui.getAppPane().setCenter(centerPane);
		//SETUP TEXT FOR THE COUNT
		Text countText = new Text();
		countText.setText("Time Remaining: 60 seconds");
		//SETUP TIME COUNT
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> doSomething(countText)));
		timeline.setCycleCount(60);
		timeline.play();
		//SETUP WORD SELECT
		//SETUP FOUND WORDS AREA
		//SETUP TARGET DISPLAY
		Text targetText = new Text("Target\n75points");
		//VBOX FOR RIGHT PANE
		VBox rightPane = new VBox(countText, targetText);
		gui.getAppPane().setRight(rightPane);
	}
	public void isPlayingSetup(boolean isPlaying)
	{
		//SETUP UP PLAY BUTTON STYLE
		playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PLAY_BUTTON));
		if(isPlaying)
			playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PAUSE_BUTTON));
		//MAKE LETTERS VISIBLE
		VBox centerPane 	= (VBox)gui.getAppPane().getCenter();
		StackPane playGrid 	= (StackPane) centerPane.getChildren().get(1);
		playGrid.getChildren().get(1).setVisible(isPlaying);
	}
	/*/***************************************************
	 ******************SETUP METHODS**********************
	 *****************************************************/
	public void setupGUI()
	{
		//SETUP HEADER
		gameHeader = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADER_LABEL));
		gui.getAppPane().setTop(gameHeader);
		gui.getAppPane().setAlignment(gameHeader, Pos.CENTER);
		//SET UP THE LOGIN AREA AND FIELDS
		StackPane loginLayoverPane = setupLoginLayoverPane();
		//SETUP LOGO GRID
		GridPane logo = setupLogoGrid();
		//CREATE A HOME PANE THAT HAS THE LOGO AND AN INVISIBLE LOGIN AREA
		StackPane homePane = new StackPane(logo, loginLayoverPane);
		//SET THE HOME PANE IN THE CENTER
		gui.getAppPane().setCenter(homePane);
		//INITIALIZE BUTTONS AND OTHER THINGS - EVEN IF NOT ON GUI YET
		newAccountBttn	= new Button("Create Account");
		logoutButton	= new Button("Log Out");
		homeButton		= new Button("Home");
		playButton		= new Button();
		gameModeMenu	= new ChoiceBox();
		levelLabel = new Label();
		userLabel = new Rectangle(150, 30);
		subTitle = new Label();
	}
	public void setupHandlers()
	{
		BuzzwordController controller = (BuzzwordController)gui.getFileController();
		//BUTTON CONTROLLERS
		newAccountBttn.setOnAction(e -> controller.makeNewAccount());
		logoutButton.setOnAction(e -> controller.handleLogoutRequest());
		playButton.setOnAction(e -> controller.play());
		//ADD A LISTENER TO THE DROP DOWN MENU
		gameModeMenu.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> controller.handleLevelSelect(newValue.intValue()));
	}
	@Override
	public void initStyle()
	{
		//BUTTON STYLE SETUP
		ObservableList<Node> sidebarChildren = gui.getSidebarPane().getChildren();
		sidebarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));	//Login Button
		sidebarChildren.get(1).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));	//New Account Btn
		sidebarChildren.get(2).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));	//Close Button
		logoutButton.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));		//Log Out Button
		newAccountBttn.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));		//Create Account btn
		homeButton.getStyleClass().setAll(propertyManager.getPropertyValue(BUTTON_STYLE));			//Home Button
		playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PLAY_BUTTON));
		//BUTTON TOOLTIP SETUP
		logoutButton.setTooltip(new Tooltip(propertyManager.getPropertyValue(LOGOUT_TOOLTIP)));
		newAccountBttn.setTooltip(new Tooltip(propertyManager.getPropertyValue(NEW_ACCOUNT_TOOLTIP)));
		homeButton.setTooltip(new Tooltip(propertyManager.getPropertyValue(HOME_TOOLTIP)));
		//SETUP SIZE
		homeButton.setMinWidth(150);
		homeButton.setAlignment(Pos.CENTER);
		//HEADER STYLE SETUP
		gameHeader.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
		//BACKGROUND STYLE SETUP
		gui.getAppPane().getStyleClass().setAll(propertyManager.getPropertyValue(BACKGROUND_STYLE));
		//SETUP SUB HEADERS STYLE
		subTitle.getStyleClass().setAll(propertyManager.getPropertyValue(SUB_HEADER_STYLE));
		levelLabel.getStyleClass().setAll(propertyManager.getPropertyValue(SUB_HEADER_STYLE));
	}
	/*/***************************************************
	 **************PRIVATE HELPER METHODS*****************
	 *****************************************************/
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
		homeGrid.setPadding(new Insets(30, 0, 0, 50));
		return homeGrid;
	}
	private StackPane setupLoginLayoverPane()
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
		Label passwrd = new Label("Password");		name.getStyleClass().setAll(propertyManager.getPropertyValue(LABEL_STYLE));
		//STYLE LABELS
		passwrd.getStyleClass().setAll(propertyManager.getPropertyValue(LABEL_STYLE));
		//SETUP TEXT FIELDS
		TextField username = new TextField();
		TextField password = new TextField();
		//STYLE TEXT FIELDS
		username.getStyleClass().setAll(propertyManager.getPropertyValue(TEXTFIELD_STYLE));
		password.getStyleClass().setAll(propertyManager.getPropertyValue(TEXTFIELD_STYLE));
		//SIZE TEXT FIELDS
		username.setMaxSize(150, 30);
		password.setMaxSize(150, 30);
		//ADD TOOLTIPS TO TEXT FIELDS
		username.setTooltip(new Tooltip(propertyManager.getPropertyValue(USERNAME_TOOLTIP)));
		password.setTooltip(new Tooltip(propertyManager.getPropertyValue(PASSWORD_TOOLTIP)));
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
		loginLayoverPane.setPadding(new Insets(30, 0, 0, 50));
		return loginLayoverPane;		//return finished invisible pane
	}
	private StackPane setupGridLevel()
	{
		StackPane playGrid = new StackPane();
		GridPane letterGrid = new GridPane();
		GridPane circleGrid = new GridPane();
		Circle container;

		GameData dataComponent = (GameData) appTemplate.getDataComponent();
		Text[][] letter = new Text[4][4];
		ArrayList gridLetters = dataComponent.initPlayGrid();

/*
		Text[][] letter = {
				{new Text("B"), new Text("U"), new Text("O"), new Text("E")},
				{new Text("Z"), new Text("Z"), new Text("G"), new Text("H")},
				{new Text("W"), new Text("C"), new Text("W"), new Text("O")},
				{new Text("A"), new Text("X"), new Text("R"), new Text("D")}};
*/
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				letter[i][j] = new Text(String.valueOf(gridLetters.get(j+i*4)));
				//circle grid
				container = new Circle(30, Paint.valueOf("white"));
				circleGrid.add(container, i, j);
				circleGrid.setMargin(container, new Insets(10));
				//letter grid
				letter[i][j].getStyleClass().setAll(propertyManager.getPropertyValue(LETTER_STYLE));
				letterGrid.add(letter[i][j], j, i);
				letterGrid.setMargin(letter[i][j], new Insets(30, 27, 25, 32));
			}
		}
		letterGrid.setVisible(false);
		playGrid.getChildren().addAll(circleGrid, letterGrid);
		return playGrid;
	}
}
