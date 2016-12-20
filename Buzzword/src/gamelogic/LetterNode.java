package gamelogic;

/**
 * Created by Bryant Gonzaga on 11/26/2016.
 */
public class LetterNode
{
	public static final int TOTAL_ADJACENT_NODES = 8;
	private LetterNode[]	adjacentNode;
	private char			letter;
	private int				indexOfNode;
	/*/*************************
	 ****CONSTRUCTOR METHODS****
	 ***************************/
	public LetterNode()
	{
		this('-');
	}
	public LetterNode(char letter)
	{
		adjacentNode = new LetterNode[TOTAL_ADJACENT_NODES];
		this.letter = letter;
	}
	public LetterNode(char letter, int indexOfNode)
	{
		adjacentNode = new LetterNode[TOTAL_ADJACENT_NODES];
		this.letter = letter;
		this.indexOfNode = indexOfNode;
	}
	public LetterNode(LetterNode[] adjacentNode, char letter)
	{
		this.adjacentNode = adjacentNode;
		this.letter = letter;
	}
	/*/****************************
	*********GETTER METHODS********
	******************************/
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
	public int getIndexOfNode()
	{
		return indexOfNode;
	}
	/*/****************************
		********SETTER METHODS*********
		******************************/
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
	public boolean equals(Object o)
	{
		if(((LetterNode)o).getLetter() == this.getLetter())
			return true;
		else
			return false;
	}
	public void setIndexOfNode(int indexOfNode)
	{
		this.indexOfNode = indexOfNode;
	}
	/*/****************************
		*******BOOLEAN METHODS*********
		******************************/
	public boolean isAdjacent(LetterNode node)
	{
		//if the node in question is null or the same node
		if(node == null || node == this)
			return false;
		//check if the node is any of the adjacent nodes
		for(int i = 0; i < TOTAL_ADJACENT_NODES; i++)
			if(adjacentNode[i] == node)
				return true;
		return false;
	}
}
