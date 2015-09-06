import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * Author: Somil Govani
 * Somil Govani
 * 02/09/15
 * v2.3
 * 
 * Implementation of Monte Carlo Tree Search Algorithm (UCB) for the Turn-Based Strategy Game Connect Four.
 * 
 * This class holds the framework for building the nodes of the MCTS trees and simulating through them.
 */

public class Node 
{
	static Random randomNumb = new Random();
	static double buffer = 1e-6;
	static char opp;
	static int counter = 0;
	
	List<Node> children = new ArrayList<Node>();
	char[][] boardState;
	double nodeVisits;
	double nodeWins;
	boolean oppMove = false;
	
	public Node(char[][] gameBoard) {
		this.boardState = new char[GameBoard.rows][GameBoard.columns];
		this.boardState = gameBoard;
	}
	
	public Node(char[][] gameBoard, boolean oppMoveIn)
	{
		this.boardState = new char[GameBoard.rows][GameBoard.columns];
		this.boardState = gameBoard;
		this.oppMove = oppMoveIn;
	}

	public void monteCarloTreeSearch()
	{
		List<Node> visitedNodes = new ArrayList<Node>();
		//set opponents character based on current player character
		if (GameBoard.turn == 'X'){opp = 'O';}
		else if (GameBoard.turn == 'O'){opp = 'X';}
		Node currentNode = this;
		visitedNodes.add(this);
		boolean firstIteration = true;
		//Selection: find next optimal node to explore, given that the current node has children to select from
		//first iteration has no children, thus this loop will be skipped for the first iteration each time
		while (currentNode.hasChildren() 
				&& !GameBoard.checkWin(currentNode.boardState, 'O') 
				&& !GameBoard.checkWin(currentNode.boardState, 'X') 
				&& !GameBoard.checkTie(currentNode.boardState))
		{
			firstIteration = false; //first iteration cannot have children (as it has not reached expansion yet) thus this must become false in this loop
			if (opp == 'X'){currentNode = currentNode.select();} //selects optimal node based on UCB and follows it
			//else {currentNode = currentNode.egreedy();} for testing against egreedy
			else {currentNode = currentNode.select();}
			if (!currentNode.oppMove) {visitedNodes.add(currentNode);}
		}
		//Simulation/Expansion: simulate (random) game until there is a winner or there is a tie.
		while (!GameBoard.checkWin(currentNode.boardState, 'O') 
				&& !GameBoard.checkWin(currentNode.boardState, 'X') 
				&& !GameBoard.checkTie(currentNode.boardState))
		{
			currentNode.expansion(currentNode.boardState, currentNode.children, firstIteration); //Expands a given node to create child nodes for the current players turn
			firstIteration = false; //after any node has expanded, it is impossible for it to be the root node
			currentNode.oppExpand(currentNode.boardState, currentNode.children); //Simulates all possible random opponent moves as children of players possible moves
			currentNode = currentNode.select(); //selects a player child node to continue simulation (randomly since none of these nodes have been visited yet as they are newly made)
			if (!currentNode.oppMove) {visitedNodes.add(currentNode);}
			if (!GameBoard.checkWin(currentNode.boardState, 'O') 
					&& !GameBoard.checkWin(currentNode.boardState, 'X') 
					&& !GameBoard.checkTie(currentNode.boardState))
			{
				currentNode = currentNode.select(); //selects opponent child of player children randomly
			}
		}
		
		
		if (opp == 'X')
		{
		
			if (GameBoard.checkWin(currentNode.boardState, GameBoard.turn))
			{
				//backpropogate and update stats for positive result (win game)
				for (Node visitedNode : visitedNodes)
				{
					//GameBoard.buildBoard(visitedNode.boardState, GameBoard.turn);
					visitedNode.nodeVisits++;
					visitedNode.nodeWins+=1; //adds value for a win
				}
			}
			
			else if (GameBoard.checkWin(currentNode.boardState, opp))
			{
				//backpropogate and update stats for negative result (lose game)
				for (Node visitedNode : visitedNodes)
				{
					visitedNode.nodeVisits++;
					//visitedNode.nodeWins-=0; //adds nothing for a loss
				}
			}
			
			else
			{
				for (Node visitedNode : visitedNodes)
				{				
					//backpropogate and update stats for a neutral result (tie game)
					visitedNode.nodeVisits++;
					visitedNode.nodeWins+=15; //adds value for a tie
				}
			}
		}
		
		else 
		{
			if (GameBoard.checkWin(currentNode.boardState, GameBoard.turn))
			{
				//backpropogate and update stats for positive result (win game)
				for (Node visitedNode : visitedNodes)
				{
					//GameBoard.buildBoard(visitedNode.boardState, GameBoard.turn);
					visitedNode.nodeVisits++;
					visitedNode.nodeWins+=1; //adds value for a win
				}
			}
			
			else if (GameBoard.checkWin(currentNode.boardState, opp))
			{
				//backpropogate and update stats for negative result (lose game)
				for (Node visitedNode : visitedNodes)
				{
					visitedNode.nodeVisits++;
					//visitedNode.nodeWins-=0; //adds nothing for a loss
				}
			}
			
			else
			{
				for (Node visitedNode : visitedNodes)
				{				
					//backpropogate and update stats for a neutral result (tie game)
					visitedNode.nodeVisits++;
					visitedNode.nodeWins+=15; //adds value for a tie
				}
			}
		}
		
	}

	private Node oppExpand(char[][] boardState, List<Node> children) {
		for (Node c : children)
		{
			if (c == null) {continue;}
			for (int i = 0; i < GameBoard.columns; i++)
			{
				for (int j = GameBoard.rows - 1; j >= 0; j--)
				{
					
					if (c.boardState[j][i] == '-') //finds lower-most (if any) blanks spaces in each column to simulate opponent move
					{
						char[][] tempBoard = new char [GameBoard.rows][GameBoard.columns];
						
						boardClone(tempBoard, c.boardState);
						if (GameBoard.turn == 'X')
						{
							tempBoard[j][i] = 'O';
							c.children.add(new Node(tempBoard, true));
							break;
						}
						else if (GameBoard.turn == 'O')
						{
							tempBoard[j][i] = 'X';
							c.children.add(new Node(tempBoard, true));
							break;
							
						}
						
					}
				}
			}
		}
		
		return children.get(randomNumb.nextInt(children.size()));
	}

	//clones an array in order to make multiple children from one parent
	public void boardClone (char[][] emptyArray, char[][] arrayToBeCloned)
	{
		
		for (int x = 0; x < GameBoard.rows; x++)
		{
			for (int y = 0; y < GameBoard.columns; y++)
			{
				emptyArray[x][y] = arrayToBeCloned[x][y];
			}
		}
		
		
	}

	public void expansion(char[][] boardState, List<Node> children, boolean firstIteration) 
	{
		
		for (int i = 0; i < GameBoard.columns; i++)
		{
			for (int j = GameBoard.rows - 1; j >= 0; j--)
			{
				if (boardState[j][i] == '-') //finds lower-most (if any) blanks spaces in each column to simulate opponent move
				{
					char[][] tempBoard = new char [GameBoard.rows][GameBoard.columns];
					
					boardClone(tempBoard, boardState);
					
					tempBoard[j][i] = GameBoard.turn;
					children.add(new Node(tempBoard));
					//if (counter < 100) {GameBoard.buildBoard(tempBoard, GameBoard.turn);}
					counter++;
					break;
				}
				else if (boardState[j][i] != '-' && j == 0 && firstIteration)
				{
					children.add(null); //preserves indexes of children (in case one column fills up and is skipped)
										//only necessary for firstIteration because the index is used to play move
				}
			}
		}			
	}

	private boolean boardFull(char[][] board) {
		for (int r = 0; r < GameBoard.rows; r++)
		{
			for (int c = 0; c < GameBoard.columns; c++)
			{
				if (board[r][c] == '-')
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean hasChildren() 
	{
		return children.size() > 0;
	}

	private Node select() //Upper Confidence Bound (Bandit Policy) implemented to select most optimal node based on exploration-exploitation balance
	{
		Node selected = null;
		double bestUCB = 0;
		for (Node childNode : children)
		{
			if (childNode == null){continue;} //for the special case of firstIteration children
			// UCB = estimated value(average reward) + C * sqrt(ln(number of visits to parent) / (number of times (child) node has been visited))
			// Where C is some constant -- sqrt(2) in this case
			// buffer prevents divide by 0 errors in the case of unvisited nodes
			double ucbValue = childNode.nodeWins / (childNode.nodeVisits + buffer) //estimated value
					+ (2/Math.sqrt(2)) * Math.sqrt(2 * Math.log(nodeVisits+1) / (childNode.nodeVisits + buffer)) +
                    randomNumb.nextDouble() * buffer; //generates a very small random number to break ties between similar/unvisited nodes
			if (ucbValue > bestUCB)
			{
				selected = childNode;
				bestUCB = ucbValue;
			}
					
		}
				
		return selected;
	}
	
	private Node egreedy()
	{
		Node selected = null;
		double bestGreed = 0;
		if (Math.random() >= 0.2)
		{
			for (Node childNode : children)
			{
				if (childNode == null){continue;}
				double tempGreed = childNode.nodeWins / (childNode.nodeVisits + buffer) + buffer;
				if (tempGreed > bestGreed)
				{
					selected = childNode;
					bestGreed = tempGreed;
				}
			}
			return selected;
		}
		else
		{
			while (selected == null)
			{
				selected = children.get(randomNumb.nextInt(children.size()));
			}
			
			return selected;
		}
	}

}
