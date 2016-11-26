package data;

import components.AppDataComponent;
import gamelogic.GameMode;
import gamelogic.LetterNode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

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
		initPlayingGrid();

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
		//VARIABLES TO HELP PLACE WORD
		boolean letterPlaced;
		String word = "HOWDY";
		boolean visited;
		int toSkip;
		int offset;
		int nodePos;
		int[] visitedNode = new int[currentLevel + 4];
		int[] offsetVal = {-4, -3, 1, 5, 4, 3, -1, -5};

		for(int manyWords = 0; manyWords < 3; manyWords++)
		{
			for(int i = 0; i < visitedNode.length; i++)
				visitedNode[i] = -1;
			toSkip = new Random().nextInt(TOTAL_NUMBER_OF_STORED_WORDS);
			try(Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI())))
			{
				word = lines.skip(toSkip).findFirst().get();
				word = word.toUpperCase();
			}catch(IOException | URISyntaxException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			//RANDOMLY START AT A NODE IN GRID
			nodePos = ((int) (Math.random() * 16));        //range from 0 - 15
			//PLACE WORD
			for(int i = 0; i < word.length(); i++)
			{
				letterPlaced = false;
				//GET NEXT NODE POSITION
				/* FIRST CREATE OFFSET */
				offset = (int) (Math.random() * 8); //random offset to begin at 0 - 8 adjacent nodes
				/* THEN CHECK OFFSET NODE */
				for(int j = 0; j < 7; j++)    //7 because there are 8 adjacent nodes
				{
					//CHECK FOR OVERFLOW AND THAT OFFSET IS IN RANGE
					while(playingGrid.get(nodePos).getAdjecentNode(offset) == null)
					{
						offset++;
						if(offset > 7)
							offset = 0;
					}
					//CHECK THAT NODE HASN'T BEEN VISITED BEFORE
					visited = false;
					for(int k = 0; k < i; k++)
					{
						if(visitedNode[k] == nodePos + offsetVal[offset])
						{
							visited = true;
							//NEW OFFSET - increment to next node
							offset++;
							if(offset > 7)
								offset = 0;
							break;            //NO NEED TO CONTINUE LOOKING
						}
					}
					if(!visited)
					{
						if(playingGrid.get(nodePos).getAdjecentNode(offset).getLetter() == '-')    //IF EMPTY
						{
							j = 10;        //NO NEED TO LOOK INTO OTHER NODES
							nodePos += offsetVal[offset];
							letterPlaced = true;
							visitedNode[i] = nodePos;
						}
						else if(playingGrid.get(nodePos).getAdjecentNode(offset).getLetter() == word.charAt(i)) //IF SAME LETTER IS THERE
						{
							j = 10;        //NO NEED TO LOOK INTO OTHER NODES
							nodePos += offsetVal[offset];
							letterPlaced = true;
							visitedNode[i] = nodePos;
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
				playingGrid.get(nodePos).setLetter(word.charAt(i));
				visitedNode[i] = nodePos;    //save where letter was placed
			}
			System.out.println(word);
		}
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
}
