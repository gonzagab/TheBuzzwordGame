package gamelogic;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public enum GameMode
{
	ENGLISH_DICTIONARY	("English Dictionary", 	0, "englishdictionary"),
	FAMOUS_PEOPLE		("Famous People", 		1, "famouspeople"),
	SCIENCE				("Science",		 		2, "science"),
	PLACES				("Places", 				3, "places");
	//FIELDS FOR EACH MODE
	String 	literal;
	String 	folder;
	int 	intVal;
	int		levels;

	GameMode(String literal, int intVal, String folder)
	{
		this.literal = literal;
		this.intVal = intVal;
		this.folder = folder;
	}
	/*/**********************************
	 ***********GETTER METHODS***********
	 ************************************/
	public String 	getLiteral()
	{
		return literal;
	}
	public int 		getIntVal()
	{
		return intVal;
	}
	public String 	getFolder()
	{
		return folder;
	}
}
