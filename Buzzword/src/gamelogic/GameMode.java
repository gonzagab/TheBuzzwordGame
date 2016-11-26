package gamelogic;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public enum GameMode
{
	ENGLISH_DICTIONARY("English Dictionary", 1, "englishdictionary"),
	FAMOUS_PEOPLE("Famous People", 2, "famouspeople"),
	SCIENCE("Science", 3, "science"),
	PLACES("Places", 4, "places");

	String 	literal;
	String folder;
	int 	intVal;

	GameMode(String literal, int intVal, String folder)
	{
		this.literal = literal;
		this.intVal = intVal;
		this.folder = folder;
	}
	public String getLiteral()
	{
		return literal;
	}
	public int getIntVal()
	{
		return intVal;
	}
	public String getFolder()
	{
		return folder;
	}
}
