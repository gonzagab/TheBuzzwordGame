package gamelogic;

/**
 * Created by Bryant Gonzaga on 11/26/2016.
 */
public class LetterNode
{
	private LetterNode[]	adjacentNode;
	private char			letter;
	/*/*************************
	 ****CONSTRUCTOR METHODS****
	 ***************************/
	public LetterNode()
	{
		this('-');
	}
	public LetterNode(char letter)
	{
		adjacentNode = new LetterNode[8];
		this.letter = letter;
	}
	public LetterNode(LetterNode[] adjacentNode, char letter)
	{
		this.adjacentNode = adjacentNode;
		this.letter = letter;
	}
	/*/*****************************
	 *********GETTER METHODS********
	 *******************************/
	public char getLetter()
	{
		return letter;
	}
	public LetterNode getAdjacentNode(int side)
	{
		return adjacentNode[side];
	}
	@Override
	public String toString()
	{
		return String.valueOf(letter);
	}
	/*/*****************************
		 ********SETTER METHODS*********
		 *******************************/
	public void setAdjacentNode(LetterNode[] adjacentNode)
	{
		this.adjacentNode = adjacentNode;
	}
	public void setLetter(char letter)
	{
		this.letter = letter;
	}
	public void setAdjacentNode(LetterNode node, int side)
	{
		adjacentNode[side] = node;
	}
	public void setAdjacentLetter(char letter, int side)
	{
		adjacentNode[side] = new LetterNode(letter);
	}
}
