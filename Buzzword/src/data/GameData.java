package data;

import components.AppDataComponent;
import gamelogic.GameMode;
import gamelogic.LetterNode;
import gamelogic.UserProfile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class GameData implements AppDataComponent
{
	private GameMode 	currentMode;		//Indicates the current mode
	private int			currentLevel;		//Indicates the current level
	private int			timeAllowed;		//Indicates how much time is allowed for the level in seconds
	private ArrayList<LetterNode> playingGrid;	//The actual grid itself
	private int			targetScore;		//Score needed to reach to pass level
	private int			currentScore;		//Score currently at
	private UserProfile	user;				//information of the current player
	//WORD SETS FOR GAME PLAY
	private Set<String> goodWords;
	private Set<String> wordFounds;
	private Set<String>[] dictionary;

	public GameData()
	{
		goodWords = new HashSet<>();
		wordFounds = new HashSet<>();
		initPlayingGrid();
		targetScore = 0;
		timeAllowed = 60;
	}
	public ArrayList<LetterNode> initPlayGrid()
	{
		initPlayingGrid();
		generatePlayingGrid();

		return playingGrid;
	}
	@Override
	public void reset()
	{
	}
	/*/******************************
	 *********GETTER METHODS*********
	 ********************************/
	public ArrayList<LetterNode> getPlayingGrid()
	{
		return playingGrid;
	}
	public GameMode getCurrentMode()
	{
		return currentMode;
	}
	public int getTimeAllowed()
	{
		return timeAllowed;
	}
	public int getTargetScore()
	{
		return targetScore;
	}
	public UserProfile getUser()
	{
		if(user == null)
			user = new UserProfile();
		return user;
	}
	/*/******************************
	 *********SETTER METHODS*********
	 ********************************/
	public boolean addFoundWord(String wordUT)
	{
		System.out.println("FUCUCUCUCCUCUCCKCKKKK--- " + wordUT);
		if(goodWords.contains(wordUT))
		{
			wordFounds.add(wordUT);
			System.out.println("good word");
			return true;
		}
		else
			return false;
	}
	public void setCurrentLevel(int currentLevel)
	{
		this.currentLevel = currentLevel;
	}
	public void setUser(UserProfile user)
	{
		this.user = user;
	}
	public void setCurrentMode(GameMode currentMode)
	{
		this.currentMode = currentMode;
		dictionary = new HashSet[currentMode.totalLevels()];
		loadDictionary();
	}
	/*/******************************
	 *****PRIVATE HELPER METHODS*****
	 ********************************/
	private String integerToString(int num)
	{
		if(num == 0)
			return "zero";
		if(num == 1)
			return "one";
		if(num == 2)
			return "two";
		if(num == 3)
			return "three";
		if(num == 4)
			return "four";
		if(num == 5)
			return "five";
		if(num == 6)
			return "six";
		if(num == 7)
			return "seven";
		if(num == 8)
			return "eight";
		if(num == 9)
			return "nine";
		if(num == 10)
			return "ten";
		if(num == 11)
			return "eleven";
		if(num == 12)
			return "twelve";
		if(num == 13)
			return "thirteen";

		return "";
	}
	private void initPlayingGrid()
	{
		playingGrid = new ArrayList<>();
		for(int i = 0; i<16; i++)
			playingGrid.add(new LetterNode('-'));
		//NODE 0
		playingGrid.get(0).setAdjacentNode(playingGrid.get(1), 2);
		playingGrid.get(0).setAdjacentNode(playingGrid.get(5), 3);
		playingGrid.get(0).setAdjacentNode(playingGrid.get(4), 4);
		//NODE 1
		playingGrid.get(1).setAdjacentNode(playingGrid.get(2), 2);
		playingGrid.get(1).setAdjacentNode(playingGrid.get(6), 3);
		playingGrid.get(1).setAdjacentNode(playingGrid.get(5), 4);
		playingGrid.get(1).setAdjacentNode(playingGrid.get(4), 5);
		playingGrid.get(1).setAdjacentNode(playingGrid.get(0), 6);
		//NODE 2
		playingGrid.get(2).setAdjacentNode(playingGrid.get(3), 2);
		playingGrid.get(2).setAdjacentNode(playingGrid.get(7), 3);
		playingGrid.get(2).setAdjacentNode(playingGrid.get(6), 4);
		playingGrid.get(2).setAdjacentNode(playingGrid.get(5), 5);
		playingGrid.get(2).setAdjacentNode(playingGrid.get(1), 6);
		//NODE 3
		playingGrid.get(3).setAdjacentNode(playingGrid.get(7), 4);
		playingGrid.get(3).setAdjacentNode(playingGrid.get(6), 5);
		playingGrid.get(3).setAdjacentNode(playingGrid.get(2), 6);
		//NODE 4
		playingGrid.get(4).setAdjacentNode(playingGrid.get(0), 0);
		playingGrid.get(4).setAdjacentNode(playingGrid.get(1), 1);
		playingGrid.get(4).setAdjacentNode(playingGrid.get(5), 2);
		playingGrid.get(4).setAdjacentNode(playingGrid.get(9), 3);
		playingGrid.get(4).setAdjacentNode(playingGrid.get(8), 4);
		//NODE 5
		playingGrid.get(5).setAdjacentNode(playingGrid.get(1), 0);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(2), 1);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(6), 2);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(10), 3);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(9), 4);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(8), 5);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(4), 6);
		playingGrid.get(5).setAdjacentNode(playingGrid.get(0), 7);
		//NODE 6
		playingGrid.get(6).setAdjacentNode(playingGrid.get(2), 0);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(3), 1);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(7), 2);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(11), 3);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(10), 4);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(9), 5);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(5), 6);
		playingGrid.get(6).setAdjacentNode(playingGrid.get(1), 7);
		//NODE 7
		playingGrid.get(7).setAdjacentNode(playingGrid.get(3), 0);
		playingGrid.get(7).setAdjacentNode(playingGrid.get(11), 4);
		playingGrid.get(7).setAdjacentNode(playingGrid.get(10), 5);
		playingGrid.get(7).setAdjacentNode(playingGrid.get(6), 6);
		playingGrid.get(7).setAdjacentNode(playingGrid.get(2), 7);
		//NODE 8
		playingGrid.get(8).setAdjacentNode(playingGrid.get(4), 0);
		playingGrid.get(8).setAdjacentNode(playingGrid.get(5), 1);
		playingGrid.get(8).setAdjacentNode(playingGrid.get(9), 2);
		playingGrid.get(8).setAdjacentNode(playingGrid.get(13), 3);
		playingGrid.get(8).setAdjacentNode(playingGrid.get(12), 4);
		//NODE 9
		playingGrid.get(9).setAdjacentNode(playingGrid.get(5), 0);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(6), 1);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(10), 2);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(14), 3);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(13), 4);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(12), 5);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(8), 6);
		playingGrid.get(9).setAdjacentNode(playingGrid.get(4), 7);
		//NODE 10
		playingGrid.get(10).setAdjacentNode(playingGrid.get(6), 0);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(7), 1);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(11), 2);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(15), 3);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(14), 4);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(13), 5);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(9), 6);
		playingGrid.get(10).setAdjacentNode(playingGrid.get(5), 7);
		//NODE 11
		playingGrid.get(11).setAdjacentNode(playingGrid.get(7), 0);
		playingGrid.get(11).setAdjacentNode(playingGrid.get(15), 4);
		playingGrid.get(11).setAdjacentNode(playingGrid.get(14), 5);
		playingGrid.get(11).setAdjacentNode(playingGrid.get(10), 6);
		playingGrid.get(11).setAdjacentNode(playingGrid.get(6), 7);
		//NODE 12
		playingGrid.get(12).setAdjacentNode(playingGrid.get(8), 0);
		playingGrid.get(12).setAdjacentNode(playingGrid.get(9), 1);
		playingGrid.get(12).setAdjacentNode(playingGrid.get(13), 2);
		//NODE 13
		playingGrid.get(13).setAdjacentNode(playingGrid.get(9), 0);
		playingGrid.get(13).setAdjacentNode(playingGrid.get(10), 1);
		playingGrid.get(13).setAdjacentNode(playingGrid.get(14), 2);
		playingGrid.get(13).setAdjacentNode(playingGrid.get(12), 6);
		playingGrid.get(13).setAdjacentNode(playingGrid.get(8), 7);
		//NODE 14
		playingGrid.get(14).setAdjacentNode(playingGrid.get(10), 0);
		playingGrid.get(14).setAdjacentNode(playingGrid.get(11), 1);
		playingGrid.get(14).setAdjacentNode(playingGrid.get(15), 2);
		playingGrid.get(14).setAdjacentNode(playingGrid.get(13), 6);
		playingGrid.get(14).setAdjacentNode(playingGrid.get(9), 7);
		//NODE 15
		playingGrid.get(15).setAdjacentNode(playingGrid.get(11), 0);
		playingGrid.get(15).setAdjacentNode(playingGrid.get(14), 6);
		playingGrid.get(15).setAdjacentNode(playingGrid.get(10), 7);
	}
	private void generatePlayingGrid()
	{
		//VARIABLES TO HELP PLACE WORD
		boolean letterPlaced;		//indicates if letter was successfully placed
		boolean wordPlaced;			//indicates if word was successfully placed
		boolean visited;			//indicates of node was visited by a word
		String word;				//the actual word to be placed
		int toSkip;					//random number to get word from dictionary
		int offset;		//one through 8. corresponds to the linked array nodes
		int nodePos;	//the current pos. 1 - 16. 16 nodes in array list
		int[] visitedNode = new int[currentLevel + 4];
		int[] offsetVal = {-4, -3, 1, 5, 4, 3, -1, -5};
		//FILL THE GRID
		for(int manyWords = 0; manyWords < 100; manyWords++)
		{
			//RESET NODES VISITED
			for(int i = 0; i < visitedNode.length; i++)
				visitedNode[i] = -1;
			//GET A RANDOM WORD
			toSkip = new Random().nextInt(dictionary[currentLevel].size());
			word = (String) dictionary[currentLevel].toArray()[toSkip];
			word = word.toUpperCase();
			//RANDOMLY START AT A NODE IN GRID
			nodePos = ((int) (Math.random() * 16));        //range from 0 - 15
			//REST WORD PLACE BOOLEAN
			wordPlaced = true;
			//PLACE WORD
			for(int i = 0; i < word.length(); i++)
			{
				letterPlaced = false;
				//GET NEXT NODE POSITION
				/* FIRST CREATE OFFSET */
				offset = (int) (Math.random() * 8); //random offset to begin at 0 - 8 adjacent nodes
				/* THEN CHECK OFFSET NODE */
				for(int j = 0; j < 8; j++)    // because there are 8 adjacent nodes
				{
					//CHECK FOR OVERFLOW AND THAT OFFSET IS IN RANGE
					int count = 0;	//count loops
					while(playingGrid.get(nodePos).getAdjacentNode(offset) == null)
					{
						j += count++;
						offset++;
						if(offset > 7)
							offset = 0;
					}
					//CHECK THAT NODE HASN'T BEEN VISITED BEFORE
					visited = false;
					for(int k = 0; k < i; k++)
						if(visitedNode[k] == (nodePos + offsetVal[offset]))
						{
							visited = true;
							//NEW OFFSET - increment to next node
							offset++;
							if(offset > 7)
								offset = 0;
							break;            //NO NEED TO CONTINUE LOOKING
						}
					if(!visited)
					{
						if(playingGrid.get(nodePos).getAdjacentNode(offset).getLetter() == '-')    //IF EMPTY
						{
							j = 10;        //NO NEED TO LOOK INTO OTHER NODES
							nodePos += offsetVal[offset];
							//visitedNode[i] = nodePos;
							letterPlaced = true;

						}
						else if(playingGrid.get(nodePos).getAdjacentNode(offset).getLetter() == word.charAt(i)) //IF SAME LETTER IS THERE
						{
							j = 10;        //NO NEED TO LOOK INTO OTHER NODES
							nodePos += offsetVal[offset];
							//visitedNode[i] = nodePos;
							letterPlaced = true;
						}
						else            //IF A DIFFERENT LETTER IS THERE
						{
							//NEW OFFSET
							offset++;
							if(offset > 7)
								offset = 0;
						}
					}
				}
				//PLACE LETTER INTO NODE - random beginner node or adjacent node
				if(letterPlaced)
				{
					playingGrid.get(nodePos).setLetter(word.charAt(i));
					visitedNode[i] = nodePos;    //save where letter was placed
				}
				else
					wordPlaced = false;
			}
			System.out.println(word +" - "+ wordPlaced);
			//CALCULATE SCORE
			if(wordPlaced)
			{
				goodWords.add(word);
				targetScore += word.length() * 2;
			}
		}
		targetScore /= 2;
	}
	private void loadDictionary()
	{
		String folder = currentMode.getFolder();
		for(int i = 0; i < currentMode.totalLevels(); i++)
		{
			dictionary[i] = new HashSet<>();
			//URL TO WORDS
			URL wordsResource = getClass().getClassLoader()
					.getResource("words/" + folder + "/" + integerToString(i) + ".txt");
			assert wordsResource != null;
			try(Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI())))
			{
				Iterator<String> words = lines.iterator();
				while(words.hasNext())
				{
					dictionary[i].add(words.next());
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
