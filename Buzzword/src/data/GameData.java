package data;

import components.AppDataComponent;
import gamelogic.GameMode;
import gamelogic.LetterNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bryant Gonzaga on 11/13/2016.
 */
public class GameData implements AppDataComponent
{
	public final int TOTAL_NUMBER_OF_STORED_WORDS = 100;
	private GameMode 	currentMode;		//Indicates the current mode
	private int			currentLevel;		//Indicates the current level
	private int			timeAllowed;		//Indicates how much time is allowed for the level
	//private char[]		playingGrid;		//The actual grid itself
	private ArrayList<LetterNode> playingGrid;	//The actual grid itself
	private int			targetScore;		//Score needed to reach to pass level
	private int			currentScore;		//Score currently at
	private Set<String> goodWords;

	public GameData()
	{
		goodWords = new HashSet<>();
/*
		playingGrid = new char[]
		{
				'-', '-', '-', '-',
				'-', '-', '-', '-',
				'-', '-', '-', '-',
				'-', '-', '-', '-'
		};
*/
	}
	public ArrayList<LetterNode> initPlayGrid()
	{
		generatePlayingGrid();

		return playingGrid;
	}
	@Override
	public void reset()
	{
	}
	private void generatePlayingGrid()
	{
		//URL TO WORDS
		URL wordsResource = getClass().getClassLoader()
				.getResource("words/" + currentMode.getFolder() + "/" + integerToString(currentLevel) + ".txt");
		assert wordsResource != null;
	}
/*
	private void generatePlayingGrid()
	{
		//URL TO WORDS
		URL wordsResource = getClass().getClassLoader()
				.getResource("words/" + currentMode.getFolder() + "/" + integerToString(currentLevel) + ".txt");
		assert wordsResource != null;
		//VARIABLES TO HELP PLACE WORD
		boolean		letterPlaced = false;
		String 		word = "HOWDY";
		boolean 	visited;
		int			toSkip;
		int			offset = 0;
		int			nodePos;
		boolean 	overflow;
		int[]		visitedNode = new int[currentLevel+4];
		for(int i = 0; i < visitedNode.length; i++)
			visitedNode[i] = -1;
		toSkip = new Random().nextInt(TOTAL_NUMBER_OF_STORED_WORDS);
		try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI())))
		{
			word = lines.skip(toSkip).findFirst().get();
			word = word.toUpperCase();
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		//RANDOMLY START AT A NODE IN GRID
		nodePos = ((int)(Math.random()*16));		//range from 0 - 15
		/*row = nodePos/4;*/
		/*col = nodePos%4;*/
		//PLACE WORD
/*
		for(int i = 0; i < word.length(); i++)
		{
			letterPlaced = false;
			//PLACE LETTER INTO NODE
			playingGrid[nodePos] = word.charAt(i);
			if(i == word.length()-1)
				break;
			System.out.println(playingGrid);
			//GET NEXT NODE POSITION
			/* FIRST CREATE OFFSET */
/*
			offset = (int)(Math.random() * 4) + 2; //random offset to begin at
			if(offset == 2)
				offset = 1;
			offset *= Math.pow(-1, (int)(Math.random()*2)+1);	//neg or pos
			overflow = (nodePos%4 == 3 &&(offset == 1||offset == 5||offset == -3))
					|| (nodePos%4 == 0 &&(offset == -1||offset == -5||offset == 3));	//check for overflow
			/* THEN CHECK OFFSET NODE */
/*
			for(int j = 0; j < 7; j++)	//7 because there are 8 adjacent nodes
			{
				//CHECK FOR OVERFLOW AND THAT OFFSET IS IN RANGE
				while((nodePos+offset > 15 || nodePos+offset < 0 || overflow) && j<7)
				{
					offset++;			//check next node
					if(offset == 2)
						offset++;
					else if(offset == 6)
						offset = 1;
					else if(offset == 0)
						offset++;
					overflow = (nodePos%4 == 3 &&(offset == 1||offset == 5||offset == -3))
							|| (nodePos%4 == 0 &&(offset == -1||offset == -5||offset == 3));
					j++;		//one less node to check
					System.out.println(overflow);
				}
				//CHECK THAT NODE HASN'T BEEN VISITED BEFORE
				visited = false;
				for(int k = 0; k < i; k++)
				{
					if(visitedNode[k] == nodePos+offset)
					{
						visited = true;
						//NEW OFFSET - increment to next node
						offset++;
						if(offset == 2)
							offset++;
						else if(offset == 6)
							offset = 1;
						else if(offset == 0)
							offset++;
						overflow = (nodePos%4 == 3 &&(offset == 1||offset == 5||offset == -3))
								|| (nodePos%4 == 0 &&(offset == -1||offset == -5||offset == 3)); //overflow flag
						break;            //NO NEED TO CONTINUE LOOKING
					}
				}
				if(!visited)
				{
					if(playingGrid[nodePos + offset] == '-')    //IF EMPTY
					{
						j = 10;        //NO NEED TO LOOK INTO OTHER NODES
						nodePos += offset;
						letterPlaced = true;
						visitedNode[i] = nodePos;
						System.out.println(letterPlaced + " -");
					}
					else if(playingGrid[nodePos + offset] == word.charAt(i)) //IF SAME LETTER IS THERE
					{
						j = 10;        //NO NEED TO LOOK INTO OTHER NODES
						nodePos += offset;
						letterPlaced = true;
						visitedNode[i] = nodePos;
						System.out.println(letterPlaced + " same");

					}
					else            //IF A DIFFERENT LETTER IS THERE
					{
						//NEW OFFSET
						offset++;
						if(offset == 2)
							offset++;
						else if(offset == 6)
							offset = 1;
						else if(offset == 0)
							offset++;
						overflow = (nodePos%4 == 3 &&(offset == 1||offset == 5||offset == -3))
								|| (nodePos%4 == 0 &&(offset == -1||offset == -5||offset == 3));
					}
				}
			}
			if(!letterPlaced)
			{
				System.out.println("Letter couldn't be placed");
				break;
			}
		}
		if(letterPlaced)
			goodWords.add(word);
		System.out.println(word);
	}
*/
	/*/******************************
	 *********GETTER METHODS*********
	 ********************************/
	public ArrayList<LetterNode> getPlayingGrid()
	{
		return playingGrid;
	}
	/*/******************************
		 *********SETTER METHODS*********
		 ********************************/
	public void setCurrentLevel(int currentLevel)
	{
		this.currentLevel = currentLevel;
	}
	public void setCurrentMode(GameMode currentMode)
	{
		this.currentMode = currentMode;
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
}
