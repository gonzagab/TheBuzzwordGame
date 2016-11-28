package gamelogic;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public enum GameMode
{
	ENGLISH_DICTIONARY	("English Dictionary", 	0, "englishdictionary", 10),
	FAMOUS_PEOPLE		("Famous People", 		1, "famouspeople", 		10),
	SCIENCE				("Science",		 		2, "science", 			10),
	PLACES				("Places", 				3, "places", 			10);
	//FIELDS FOR EACH MODE
	String 	literal;
	String 	folder;
	int 	intVal;
	int		levels;

	GameMode(String literal, int intVal, String folder, int levels)
	{
		this.literal = literal;
		this.intVal = intVal;
		this.folder = folder;
		this.levels = levels;
	}
	/*/**********************************
	 ***********GETTER METHODS***********
	 ************************************/
	public String 	getLiteral()
	{
		return literal;
	}
	public String 	getFolder()
	{
		return folder;
	}
	public int 		getIntVal()
	{
		return intVal;
	}
	public int 		totalLevels()
	{
		return levels;
	}
}
