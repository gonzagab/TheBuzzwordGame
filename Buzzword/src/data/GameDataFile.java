package data;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import components.AppDataComponent;
import components.AppFileComponent;
import gamelogic.GameMode;
import gamelogic.UserProfile;
import sun.net.ProgressSource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	public void loadData(AppDataComponent data, Path userFile) throws IOException
	{
		GameData gamedata = (GameData) data;
		gamedata.reset();

		JsonNode rootNode;
		JsonNode jNode;

		Iterator<JsonNode> progress;
		UserProfile user = gamedata.getUser();
		int i;

		byte[] jData = Files.readAllBytes(userFile);
		rootNode = (new ObjectMapper()).readTree(jData);
		//GET USERNAME AND SET USERNAME
		jNode = rootNode.path(USERNAME);
		user.setUsername(jNode.asText());
		//GET PASSWORD AND SET PASSWORD
		jNode = rootNode.path(PASSWORD);
		user.setPassword(jNode.asText());
		//GET PROGRESS
		jNode = rootNode.path(PROGRESS);
		//ITERATE THROUGH PROGRESS
		progress = jNode.elements();
		i = 0;
		while(progress.hasNext())
		{
			user.updateModeProgress(GameMode.values()[i], progress.next().asInt());
			i++;
		}
	}
	@Override
	public void exportData(AppDataComponent data, Path filePath) throws IOException
	{

	}
}
