package ui;

import apptemplate.AppTemplate;
import components.AppStyleArbiter;
import controller.FileController;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import propertymanager.PropertyManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

/**
 * This class provides the basic user interface for this application, including all the file controls, but it does not
 * include the workspace, which should be customizable and application dependent.
 *
 * @author Richard McKenna, Ritwik Banerjee
 */
public class AppGUI implements AppStyleArbiter {

    protected FileController	fileController;   // to react to file-related controls
    protected Stage          	primaryStage;     // the application window
    protected Scene         	primaryScene;     // the scene graph
    protected BorderPane		appPane;          // the root node in the scene graph, to organize the containers
    protected VBox  			sidebarPane;      // the side bar that holds buttons
    protected Button 			newButton;        // button to create a new profile
    protected Button         	loginButton;       // button to log into an already existing account
    protected Button         	closeButton;		//button to exit application
    protected String         	applicationTitle;	//the application title
	protected StackPane			backgroundPane;		//background pane

    private int appSpecificWindowWidth;  // optional parameter for window width that can be set by the application
    private int appSpecificWindowHeight; // optional parameter for window height that can be set by the application
    
    /**
     * This constructor initializes the file toolbar for use.
     *
     * @param initPrimaryStage The window for this application.
     * @param initAppTitle     The title of this application, which
     *                         will appear in the window bar.
     * @param app              The app within this gui is used.
     */
    public AppGUI(Stage initPrimaryStage, String initAppTitle, AppTemplate app) throws IOException, InstantiationException
    {
        this(initPrimaryStage, initAppTitle, app, -1, -1);
    }
    public AppGUI(Stage primaryStage, String applicationTitle, AppTemplate appTemplate, int appSpecificWindowWidth, int appSpecificWindowHeight)
            throws IOException, InstantiationException
    {
        this.appSpecificWindowWidth = appSpecificWindowWidth;
        this.appSpecificWindowHeight = appSpecificWindowHeight;
        this.primaryStage = primaryStage;
        this.applicationTitle = applicationTitle;
        initializeSidebar();                    // initialize the buttons on the side bar
        initializeToolbarHandlers(appTemplate); // set the toolbar button handlers
        initializeWindow();                     // start the app window (without the application-specific workspace)
    }
	public void updateWorkspaceToolbar(boolean savable)
	{
		// saveButton.setDisable(!savable);
		newButton.setDisable(false);
		closeButton.setDisable(false);
	}
    /*/**************************************************************************
    ***********PRIVATE HELPER METHODS USED FOR INITIALIZING AppGUI***************
    *****************************************************************************/
    /**
     * This function initializes all the buttons in the sidebar
     * These are related to file management.
     */
    private void initializeSidebar() throws IOException
    {
        sidebarPane = new VBox();
        loginButton = initializeChildButton(sidebarPane, LOGIN_BUTTON_LABEL.toString(), LOGIN_TOOLTIP.toString(), false);
        newButton = initializeChildButton(sidebarPane, NEW_BUTTON_LABEL.toString(), NEW_TOOLTIP.toString(), false);
        closeButton = initializeChildButton(sidebarPane, EXIT_BUTTON_LABEL.toString(), EXIT_TOOLTIP.toString(), false);
		sidebarPane.setPadding(new Insets(70,10,10,50));
		sidebarPane.setSpacing(10);
    }
    private void initializeToolbarHandlers(AppTemplate app) throws InstantiationException
    {
        try
        {
            Method         getFileControllerClassMethod = app.getClass().getMethod("getFileControllerClass");
            String         fileControllerClassName      = (String) getFileControllerClassMethod.invoke(app);
            Class<?>       klass                        = Class.forName("controller." + fileControllerClassName);
            Constructor<?> constructor                  = klass.getConstructor(AppTemplate.class);
            fileController = (FileController) constructor.newInstance(app);
        }
        catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        newButton.setOnAction(e -> fileController.handleNewRequest());
        loginButton.setOnAction(e -> fileController.handleLoginRequest());
//        saveButton.setOnAction(e -> {
//            try {
//                fileController.handleSaveRequest();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//                System.exit(1);
//            }
//        });
        closeButton.setOnAction(e -> fileController.handleExitRequest());
    }
    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Page IS CREATED OR LOADED
    private void initializeWindow() throws IOException
    {
        PropertyManager propertyManager = PropertyManager.getManager();
		//INITIALIZE BACKGROUND PANE
		backgroundPane = new StackPane();
		//INITIALIZE APP PANE
		appPane = new BorderPane();
		//ADD SIDEBAR
		appPane.setLeft(sidebarPane);
		//ADD TO BACKGROUND
		backgroundPane.getChildren().addAll(appPane);
        // SET THE WINDOW TITLE
        primaryStage.setTitle(applicationTitle);
        // GET THE SIZE OF THE SCREEN
        Screen      screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        // AND USE IT TO SIZE THE WINDOW
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
		//INITIALIZE THE PRIMARY SCENE
        primaryScene = appSpecificWindowWidth < 1 || appSpecificWindowHeight < 1
                ?new Scene(backgroundPane)
                :new Scene(backgroundPane, appSpecificWindowWidth, appSpecificWindowHeight);
		//SETUP THE APP IMAGE
        URL imgDirURL = AppTemplate.class.getClassLoader().getResource(APP_IMAGEDIR_PATH.getParameter());
        if (imgDirURL == null)
            throw new FileNotFoundException("Image resources folder does not exist.");
        try (InputStream appLogoStream = Files.newInputStream(Paths.get(imgDirURL.toURI()).resolve(propertyManager.getPropertyValue(APP_LOGO))))
        {
            primaryStage.getIcons().add(new Image(appLogoStream));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(primaryScene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     *
     * @param toolbarPane Toolbar pane into which to place this button.
     * @param label       property name for the label of the Button
     * @param tooltip     Tooltip to appear when the user mouses over the button.
     * @param disabled    true if the button is to start off disabled, false otherwise.
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    private Button initializeChildButton(Pane toolbarPane, String label, String tooltip, boolean disabled) throws IOException
    {
        //ADD LABEL
        PropertyManager propertyManager = PropertyManager.getManager();
        Button button = new Button(propertyManager.getPropertyValue(label));
        //SET UP DISABLE
        button.setDisable(disabled);
        //ADD TOOLTIP
        Tooltip buttonTooltip = new Tooltip(propertyManager.getPropertyValue(tooltip));
        button.setTooltip(buttonTooltip);
        //ADD BUTTON TO THE PANE
        toolbarPane.getChildren().add(button);
        //AND FINALLY RETURN
        return button;
    }
	@Override
	public void initStyle()
	{
		// currently, we do not provide any stylization at the framework-level
	}
	/*/********************************************************
	 ***************** GETTER METHODS *************************
	 **********************************************************/
	public VBox getSidebarPane()
	{
		return sidebarPane;
	}
	public BorderPane getAppPane()
	{
		return appPane;
	}
	/**
	 * Accessor method for getting this application's primary stage's,
	 * scene.
	 *
	 * @return This application's window's scene.
	 */
	public Scene getPrimaryScene()
	{
		return primaryScene;
	}
	/**
	 * Accessor method for getting this application's window,
	 * which is the primary stage within which the full GUI will be placed.
	 *
	 * @return This application's primary stage (i.e. window).
	 */
	public Stage getWindow()
	{
		return primaryStage;
	}
	/**
	 * This function specifies the CSS style classes for the controls managed
	 * by this framework.
	 */
	public FileController getFileController()
	{
		return fileController;
	}
	public Button getNewButton()
	{
		return newButton;
	}
	public Button getLoginButton()
	{
		return loginButton;
	}
	public Button getCloseButton()
	{
		return closeButton;
	}
}
