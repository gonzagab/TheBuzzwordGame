package data;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import components.AppDataComponent;
import components.AppFileComponent;
import sun.net.ProgressSource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class GameDataFile implements AppFileComponent
{
	public final static String USERNAME	= "USERNAME";
	public final static String PASSWORD	= "PASSWORD";
	public final static String PROGRESS	= "PROGRESS";

	@Override
	public void saveData(AppDataComponent data, Path to) throws IOException
	{
		OutputStream file = Files.newOutputStream(to);
		JsonGenerator jsonGen = (new JsonFactory()).createGenerator(file);
		jsonGen.setPrettyPrinter(new DefaultPrettyPrinter());

		jsonGen.writeStartObject();
		jsonGen.writeStringField(USERNAME, ((GameData) data).getUser().getUsername());
		jsonGen.writeStringField(PASSWORD, ((GameData) data).getUser().getPassword());

		jsonGen.writeFieldName(PROGRESS);
		jsonGen.writeStartArray();
		for(Object temp : ((GameData) data).getUser().getProgress())
			jsonGen.writeString(temp.toString());
		jsonGen.writeEndArray();
		jsonGen.close();
	}
	@Override
	public void loadData(AppDataComponent data, Path filePath) throws IOException
	{

	}
	@Override
	public void exportData(AppDataComponent data, Path filePath) throws IOException
	{

	}
}
