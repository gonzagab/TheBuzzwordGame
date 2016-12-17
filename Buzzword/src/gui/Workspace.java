package gui;

import apptemplate.AppTemplate;
import com.sun.javafx.scene.layout.region.BorderImageSliceConverter;
import components.AppWorkspaceComponent;
import controller.BuzzwordController;
import data.GameData;
import gamelogic.GameMode;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.WindowEvent;
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
	//FRAMEWORK REFERENCES
    AppTemplate 	appTemplate;    	//reference to the actual app
    AppGUI      	gui;            	//access to the app gui
    //Workspace GUI Objects
			/*BUTTONS*/
	Button 			newAccountBttn;	//Button for users to create a new account
	Button			logoutButton;	//Button that will log user out
	Button			playButton;		//Button to start playing a level
	Button			homeButton;		//Sends the user home
	Button			userLabel;		//Temporary space holder for username
			/*LABELS*/
	Label       	gameHeader;     //the header of the game
	Label 			levelLabel;		//Label that displays the level
	Label 			subTitle;		//Sub header for mode
	Label			slctWordLabel;	//label for the selected word
	Label			fndAreaLabel;	//label for the found word area
	Label			timeLabel;		//label for time remaining
	Label			scoreLabel;		//label for current score
	Label			trgtScoreLabel;	//holds the target score
			/*TEXT  AREAS*/
	TextArea 			selectedWordArea;//area will hold the word the user has guessd
	TextArea 			foundWordArea;	//area will hold all the words the user has found so far
	//SETUP TEXT FIELDS
	TextField username = new TextField();
	TextField password = new TextField();
			/*DROOP DOWN MENU*/
	ChoiceBox<String>	gameModeMenu;	//Holds all the modes of the game
	//Property Manager
	PropertyManager 	propertyManager = PropertyManager.getManager();
	/*/***********************************
	 *************CONSTRUCTOR*************
	 *************************************/
	public Workspace(AppTemplate app)
	{
		appTemplate = app;
		gui = app.getGUI();
		setupGUI();
		setupHandlers();
	}
	/*/***********************************
	 ************GUI MODIFIERS************
	 *************************************/
	@Override
	public void reloadWorkspace()
	{
		BuzzwordController controller = (BuzzwordController)gui.getFileController();
		gui.getSidebarPane().getChildren().setAll(gui.getLoginButton(), gui.getNewButton(), gui.getCloseButton());
		ensureHomeScreen();
		gameModeMenu	= new ChoiceBox();
		gameModeMenu.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> controller.handleLevelSelect(newValue.intValue()));
	}
	public void activateLoginScreen(boolean visible)
	{
		StackPane gridLayover = (StackPane)gui.getAppPane().getCenter();
		gridLayover.getChildren().get(1).setVisible(visible);
	}
	public void ensureHomeScreen()
	{
		StackPane loginLayoverPane = setupLoginLayoverPane();
		//SETUP LOGO GRID
		GridPane logo = setupLogoGrid();
		//CREATE A HOME PANE THAT HAS THE LOGO AND AN INVISIBLE LOGIN AREA
		StackPane homePane = new StackPane(logo, loginLayoverPane);
		homePane.setPadding(new Insets(30, 0, 0, 0));
		homePane.setAlignment(Pos.TOP_CENTER);
		//SET THE HOME PANE IN THE CENTER
		gui.getAppPane().setCenter(homePane);
		//CLEAR RIGHT PANE JUST IN CASE
		gui.getAppPane().setRight(null);
	}
	/*GAME PLAY MODIFIERS*/
	public void updateTimerDisplay(int count)
	{
		Label countText = (Label) ((VBox)gui.getAppPane().getRight()).getChildren().get(0);
		countText.setText("Time Remaining: " +count+" seconds");
	}
	public void isPlayingSetup(boolean isPlaying)
	{
		//SETUP UP PLAY BUTTON STYLE
		playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PLAY_BUTTON));
		if(isPlaying)
			playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PAUSE_BUTTON));
		//MAKE LETTERS VISIBLE OR INVISIBLE
		VBox centerPane 	= (VBox)gui.getAppPane().getCenter();
		GridPane playGrid 	= (GridPane) centerPane.getChildren().get(1);
		for(int i = 0; i < 16; i++)
			((StackPane)playGrid.getChildren().get(i)).getChildren().get(1).setVisible(isPlaying);
	}
	public void updateWrdSlctDsp(String append)
	{
		selectedWordArea.appendText(append);
	}
	public void clrWrdSlctDsp(boolean all)
	{
		if(all)
			selectedWordArea.clear();
		else
			selectedWordArea.deleteText(selectedWordArea.getText().length()-1, selectedWordArea.getText().length());
	}
	public void updateWrdFndDsp(String append)
	{
		foundWordArea.appendText(append);
		foundWordArea.appendText("\n");
	}
	public void updateWrdSlctOnGui(StackPane node)
	{
		((Circle)node.getChildren().get(0)).setFill(Paint.valueOf("blue"));
	}
	public void updateScore(int score)
	{
		scoreLabel.setText("Current Score: " + score + " points");
	}
	public void rstWrdSlctOnGui(StackPane node)
	{
		if(node != null)
			((Circle)node.getChildren().get(0)).setFill(Paint.valueOf("white"));
		else
		{
			GridPane grid = (GridPane)((VBox)gui.getAppPane().getCenter()).getChildren().get(1);
			for(int i= 0; i < 16; i++)
				((Circle)((StackPane)grid.getChildren().get(i)).getChildren().get(0)).setFill(Paint.valueOf("White"));
		}
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
		userLabel.setText(((GameData)appTemplate.getDataComponent()).getUser().getUsername());
		//ADD IT ALL TO THE SIDEBAR
		sidebarPane.getChildren().setAll(userLabel, gameModeMenu, logoutButton);
		//SETUP DROP DOWN MENU FOR GAME MODE
		gameModeMenu.getItems().setAll(ENGLISH_DICTIONARY.getLiteral(), FAMOUS_PEOPLE.getLiteral(),
				SCIENCE.getLiteral(), PLACES.getLiteral());
		gameModeMenu.setValue(ENGLISH_DICTIONARY.getLiteral());
		gameModeMenu.setTooltip(new Tooltip(propertyManager.getPropertyValue(GAME_MODE_MENU_TOOLTIP)));
	}
	public void reloadHomeGuiSidebar()
	{
		VBox sidebarPane = gui.getSidebarPane();
		sidebarPane.getChildren().setAll(userLabel, gameModeMenu, logoutButton);
		gui.getAppPane().setRight(null);
		((BuzzwordController)gui.getFileController()).startedPlaying(false);
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
		((BuzzwordController)gui.getFileController()).startedPlaying(true);
		//SETUP SIDEBAR
		gui.getSidebarPane().getChildren().setAll(userLabel, homeButton, logoutButton);
		//SETUP HEADER
		subTitle.setText(mode.getLiteral());
		//SETUP PLAY GRID GUI
		GameData dataComponent = (GameData) appTemplate.getDataComponent();
		dataComponent.setCurrentLevel(level);
		GridPane playGrid = setupGridLevel();
		playGrid.setPadding(new Insets(20, 0, 20, 0));
		playGrid.setAlignment(Pos.CENTER);
		//SETUP LEVEL LABEL
		levelLabel.setText("Level " + level);
		//MAKE SURE PLAY BUTTON IS SET AS PLAY BUTTON
		playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PLAY_BUTTON));
		//ADD IT ALL TO THE V-BOX
		VBox centerPane = new VBox(subTitle, playGrid, levelLabel, playButton);
		centerPane.setAlignment(Pos.CENTER);
		//SETUP CENTER PANE
		gui.getAppPane().setCenter(centerPane);
		//SETUP WORD SELECT AREA
		selectedWordArea.setEditable(false);
		//SETUP FOUND WORDS AREA
		foundWordArea.setEditable(false);
		scoreLabel.setText("Current Score: " +
				((GameData) appTemplate.getDataComponent()).getCurrentScore()+ " points");
		//SETUP TARGET SCORE
		trgtScoreLabel.setText("Target: " +
				((GameData) appTemplate.getDataComponent()).getTargetScore()+ " points");
		//VBOX FOR RIGHT PANE
		VBox rightPane = new VBox(timeLabel,slctWordLabel, selectedWordArea, fndAreaLabel,
				foundWordArea, scoreLabel, trgtScoreLabel);
		gui.getAppPane().setRight(rightPane);
		//DISPLAY TIMER DISPLAY
		updateTimerDisplay(((GameData) appTemplate.getDataComponent()).getTimeAllowed());

		//KEY PRESSED FOR INPUTTING GUESS WORD
		GridPane tempGrid = (GridPane)((VBox)gui.getAppPane().getCenter()).getChildren().get(1);
		gui.getPrimaryScene().setOnKeyTyped(event ->
		{
			char guess = event.getCharacter().charAt(0);
			if(guess > 96 && guess < 123)
				guess -= 32;
			if(guess > 64 && guess < 91)
			{
				for(int i = 0; i < 16; i++)
				{
					StackPane  node = (StackPane)tempGrid.getChildren().get(i);
					String letter = ((Label)node.getChildren().get(1)).getText();
					if(letter.charAt(0) == guess)
						((BuzzwordController)gui.getFileController()).nodeSelected(node);
				}
			}
		});
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
		homePane.setPadding(new Insets(30, 0, 0, 0));
		homePane.setAlignment(Pos.TOP_CENTER);
		//SET THE HOME PANE IN THE CENTER
		gui.getAppPane().setCenter(homePane);
		//RIGHT PANE SPACE HOLDER
		Region space = new Region();
		HBox.setHgrow(space, Priority.ALWAYS);
		gui.getAppPane().setRight(space);
		//INITIALIZE BUTTONS AND OTHER THINGS - EVEN IF NOT ON GUI YET
		/*BUTTONS*/
		newAccountBttn	= new Button("Create Account");
		logoutButton	= new Button("Log Out");
		homeButton		= new Button("Home");
		playButton		= new Button();
		userLabel 		= new Button();
		/*CHOICE BOX*/
		gameModeMenu	= new ChoiceBox();
		/*LABELS*/
		slctWordLabel	= new Label("Selected Word");
		fndAreaLabel	= new Label("Words Found");
		timeLabel		= new Label();
		scoreLabel		= new Label();
		trgtScoreLabel	= new Label();
		levelLabel 		= new Label();
		subTitle		= new Label();
		/*TEXT AREA*/
		selectedWordArea= new TextArea();
		foundWordArea	= new TextArea();
	}
	public void setupHandlers()
	{
		BuzzwordController controller = (BuzzwordController)gui.getFileController();
		//BUTTON CONTROLLERS
		newAccountBttn.setOnAction(e -> controller.makeNewAccount());
		logoutButton.setOnAction(e -> controller.handleLogoutRequest());
		playButton.setOnAction(e -> controller.play());
		homeButton.setOnAction(e -> controller.handleHomeRequest());
		//ADD A LISTENER TO THE DROP DOWN MENU
		gameModeMenu.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> controller.handleLevelSelect(newValue.intValue()));
		//STAGE CLOSE REQUEST
		gui.getWindow().setOnCloseRequest(event ->
		{
			event.consume();
			controller.handleExitRequest();
		});
		//TEXT FIELD KEY PRESSED HANDLERS
		username.setOnKeyPressed(e ->
		{
			if(e.getCode().equals(KeyCode.ENTER))
				controller.login();
		});
		password.setOnKeyPressed(e ->
		{
			if(e.getCode().equals(KeyCode.ENTER))
				controller.login();
		});
	}
	@Override
	public void initStyle()
	{
		//BUTTON STYLE SETUP
		ObservableList<Node> sidebarChildren = gui.getSidebarPane().getChildren();
		sidebarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));	//Login Button
		sidebarChildren.get(1).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));	//New Account Btn
		sidebarChildren.get(2).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));	//Close Button
		logoutButton.getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));		//Log Out Button
		newAccountBttn.getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));		//Create Account btn
		homeButton.getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));			//Home Button
		playButton.getStyleClass().setAll(propertyManager.getPropertyValue(PLAY_BUTTON));		//play button
		userLabel.getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));		//user label button
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
		//SETUP RIGHT PANE LABEL STYLE
		slctWordLabel.getStyleClass().setAll(propertyManager.getPropertyValue(RIGHT_PANE_TEXT_STYLE));
		fndAreaLabel.getStyleClass().setAll(propertyManager.getPropertyValue(RIGHT_PANE_TEXT_STYLE));
		timeLabel.getStyleClass().setAll(propertyManager.getPropertyValue(RIGHT_PANE_TEXT_STYLE));
		scoreLabel.getStyleClass().setAll(propertyManager.getPropertyValue(RIGHT_PANE_TEXT_STYLE));
		trgtScoreLabel.getStyleClass().setAll(propertyManager.getPropertyValue(RIGHT_PANE_TEXT_STYLE));
		//SETUP TEXT AREA STYLE
		selectedWordArea.getStyleClass().addAll(propertyManager.getPropertyValue(SELECTED_WORD_AREA));
		foundWordArea.getStyleClass().addAll(propertyManager.getPropertyValue(FOUND_WORD_AREA));
		//STYLE TEXT FIELDS
		username.getStyleClass().setAll(propertyManager.getPropertyValue(TEXTFIELD_STYLE));
		password.getStyleClass().setAll(propertyManager.getPropertyValue(TEXTFIELD_STYLE));
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
		homeGrid.setAlignment(Pos.TOP_CENTER);
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
		loginFields.setAlignment(Pos.TOP_CENTER);
		//SETUP TEXT FIELD LABELS AND STYLE THEM
		Label name = new Label("Username");
		Label passwrd = new Label("Password");
		name.getStyleClass().setAll(propertyManager.getPropertyValue(LABEL_STYLE));
		//STYLE LABELS
		passwrd.getStyleClass().setAll(propertyManager.getPropertyValue(LABEL_STYLE));
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
		loginLayoverPane.setMaxHeight(160);
		loginLayoverPane.setVisible(false);
		loginLayoverPane.setAlignment(Pos.CENTER);
		return loginLayoverPane;		//return finished invisible pane
	}
	public GridPane setupGridLevel()
	{
		BuzzwordController controller = (BuzzwordController)gui.getFileController();
		//grid playing grid
		GridPane gridGame = new GridPane();
		//each individual grid pieces
		StackPane gridPiece;
		//background of letters
		Circle container;
		//set up the letters
		GameData dataComponent = (GameData) appTemplate.getDataComponent();
		Label[][] letter = new Label[4][4];
		ArrayList gridLetters = dataComponent.initPlayGrid();
		//create and put all 16 gid pieces into grid
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				int finalI = i;
				int finalJ = j;
				//white circle for background
				container = new Circle(30, Paint.valueOf("white"));
				//letter set up
				letter[i][j] = new Label(gridLetters.get(j+i*4).toString());
				letter[i][j].getStyleClass().setAll(propertyManager.getPropertyValue(LETTER_STYLE));
				letter[i][j].setVisible(false);
				//grid piece setup
				gridPiece = new StackPane(container, letter[i][j]);
				StackPane gridPieceF = gridPiece;
				//get up handlers for drag events
				gridPiece.setOnMouseDragEntered(e -> controller.nodeSelected(gridPieceF));
				gridPiece.setOnMouseClicked(e -> controller.nodeSelected(gridPieceF));
				gridPiece.setOnDragDetected(e -> gridPieceF.startFullDrag());
				gridPiece.setOnMouseDragReleased(e -> controller.dragEnd(selectedWordArea.getText()));
				//add grid pieces to grid
				gridGame.add(gridPiece, j, i);
				gridGame.setMargin(gridPiece, new Insets(10));
			}
		}
		return gridGame;
	}
//	private StackPane setupGridLevel(int h)
//	{
//		BuzzwordController controller = (BuzzwordController)gui.getFileController();
//		StackPane playGrid = new StackPane();
//		playGrid.setAlignment(Pos.CENTER);
//		GridPane letterGrid = new GridPane();
//		letterGrid.setAlignment(Pos.CENTER);
//		GridPane circleGrid = new GridPane();
//		circleGrid.setAlignment(Pos.CENTER);
//		Circle container;
//
//		GameData dataComponent = (GameData) appTemplate.getDataComponent();
//		Label[][] letter = new Label[4][4];
//		ArrayList gridLetters = dataComponent.initPlayGrid();
//
//		for(int i = 0; i<4; i++)
//		{
//			for(int j = 0; j < 4; j++)
//			{
//				letter[i][j] = new Label(gridLetters.get(j+i*4).toString());
//				int finalI = i;
//				int finalJ = j;
//				//circle grid
//				container = new Circle(30, Paint.valueOf("white"));
//				Circle containerF = container;
//				letter[i][j].setOnMousePressed(e -> controller.nodeSelected(letter[finalI][finalJ].getText()));
//				letter[i][j].setOnMouseDragEntered(e -> controller.nodeSelected(letter[finalI][finalJ].getText()));
//				letter[i][j].setOnDragDetected(e ->
//				{
//					containerF.startFullDrag();
//					controller.nodeSelected(letter[finalI][finalJ].getText());
//				});
//
//				circleGrid.add(container, i, j);
//				circleGrid.setMargin(container, new Insets(10));
//				//letter grid
//				letter[i][j].getStyleClass().setAll(propertyManager.getPropertyValue(LETTER_STYLE));
//				letterGrid.add(letter[i][j], j, i);
//				letterGrid.setMargin(letter[i][j], new Insets(30, 27, 25, 32));
//			}
//		}
//		letterGrid.setVisible(false);
//		playGrid.getChildren().addAll(circleGrid, letterGrid);
//		return playGrid;
//	}
}
