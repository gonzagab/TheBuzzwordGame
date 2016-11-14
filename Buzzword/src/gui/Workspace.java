package gui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import propertymanager.PropertyManager;
import ui.AppGUI;

import static gui.BuzzwordProperties.*;

/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class Workspace extends AppWorkspaceComponent
{
    AppTemplate appTemplate;    //reference to the actual app
    AppGUI      gui;            //access to the app gui
    //Workspace GUI Objects
    Label       gameHeader;     //the header of the game
	//Property Manager
	PropertyManager propertyManager = PropertyManager.getManager();

	public Workspace(AppTemplate app)
	{
		appTemplate = app;
		gui = app.getGUI();
		setupGUI();
	}
	public void setupGUI()
	{
		//SETUP HEADER
		gameHeader = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADER_LABEL));
		gui.getAppPane().setTop(gameHeader);
		gui.getAppPane().setAlignment(gameHeader, Pos.CENTER);
		//SETUP LOGO GRID
		GridPane logo = setUpHomeGrid();
		gui.getAppPane().setCenter(logo);
		gui.getAppPane().setAlignment(logo, Pos.BOTTOM_CENTER);

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
    private GridPane setUpHomeGrid()
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
			for(int j = 0; j<4; j++)
			{
				container = new Circle(30, Paint.valueOf("white"));
				letter[j][i].getStyleClass().setAll(propertyManager.getPropertyValue(LETTER_STYLE));
				gridPiece = new StackPane(container, letter[j][i]);
				homeGrid.add(gridPiece, i, j);
				homeGrid.setMargin(gridPiece, new Insets(10));
			}
		}
		homeGrid.setPadding(new Insets(50));

		return homeGrid;
	}
}
