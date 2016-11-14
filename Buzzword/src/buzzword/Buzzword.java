package buzzword;

import apptemplate.AppTemplate;
import components.AppComponentsBuilder;
import components.AppDataComponent;
import components.AppFileComponent;
import components.AppWorkspaceComponent;
import data.GameData;
import data.GameDataFile;
import gui.Workspace;


/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class Buzzword extends AppTemplate
{
    public AppComponentsBuilder makeAppBuilderHook()
    {
        return new AppComponentsBuilder()
        {
            @Override
            public AppDataComponent buildDataComponent() throws Exception
            {
                return new GameData();
            }
            @Override
            public AppFileComponent buildFileComponent() throws Exception
			{
                return new GameDataFile();
            }
            @Override
            public AppWorkspaceComponent buildWorkspaceComponent() throws Exception
			{
                return new Workspace(Buzzword.this);
            }
        };
    }
	public String getFileControllerClass()
	{	return "BuzzwordController";	}
    public static void main(String[] args)
    {	launch(args);	}
}
