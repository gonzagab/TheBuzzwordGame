package gamelogic;

/**
 * Created by Bryant Gonzaga on 11/24/2016.
 */
public class UserProfile
{
	/*/*************************
	*****PROFILE LOGIN INFO*****
	****************************/
	private String username;
	private String password;
	/*/*************************
	*****USER PROGRESS INFO*****
	****************************/
	private int[] progress;
	/*/*************************
	********CONSTRUCTORS********
	 ***************************/
	public UserProfile()
	{
		username = null;
		password = null;
		progress = new int[GameMode.values().length];
	}
	public UserProfile(String username)
	{
		this.username = username;
		password = null;
		progress = new int[GameMode.values().length];
	}
	public UserProfile(String username, String password)
	{
		this.username = username;
		this.password = password;
		progress = new int[GameMode.values().length];
	}
	public void updateModeProgress(GameMode mode, int levelsComplete)
	{
		progress[mode.getIntVal()] = levelsComplete;
	}
	/*/************************
	*******GETTER METHODS******
	***************************/
	public int[] 	getProgress()
	{
		return progress;
	}
	public String 	getUsername()
	{
		return username;
	}
	public String 	getPassword()
	{
		return password;
	}
	public int 		getModeProgress(GameMode mode)
	{
		return progress[mode.getIntVal()];
	}
}
