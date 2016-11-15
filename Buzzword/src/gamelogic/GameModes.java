package gamelogic;

import data.GameData;

/**
 * Created by Bryant Gonzaga on 11/14/2016.
 */
public enum GameModes
{
	ENGLISH_DICTIONARY("English Dictionary"),
	PLACES("Places"),
	SCIENCE("Science"),
	FAMOUS_PEOPLE("Famous People");

	String value;
	GameModes(String val)
	{
		value = val;
	}
	public String getVal()
	{
		return value;
	}
}
