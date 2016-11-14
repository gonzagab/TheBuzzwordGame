package gui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

	public Workspace(AppTemplate app)
	{
		appTemplate = app;
		gui = app.getGUI();
	}
    @Override
    public void initStyle()
    {
        PropertyManager propertyManager = PropertyManager.getManager();

//        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_BORDERPANE_ID));
//        gui.getSidebarPane().getStyleClass().setAll(propertyManager.getPropertyValue(SEGMENTED_BUTTON_BAR));
//        gui.getSidebarPane().setId(propertyManager.getPropertyValue(TOP_TOOLBAR_ID));

        ObservableList<Node> sidbarChildren = gui.getSidebarPane().getChildren();
        sidbarChildren.get(0).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));
        sidbarChildren.get(sidbarChildren.size() - 1).getStyleClass().add(propertyManager.getPropertyValue(BUTTON_STYLE));

		//workspace.getStyleClass().add(CLASS_BORDERED_PANE);
        //gameHeader.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
    }
    @Override
    public void reloadWorkspace()
    {

    }
}
