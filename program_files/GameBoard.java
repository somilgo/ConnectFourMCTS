import java.util.InputMismatchException;
import java.util.Scanner;

/*
 * Author: Somil Govani
 * Somil Govani
 * 02/09/15
 * v2.3
 * 
 * Implementation of Monte Carlo Tree Search Algorithm (UCB) for the Turn-Based Strategy Game Connect Four.
 * 
 * This class holds the framework of the game play and the main method.
 */


public class GameBoard {
	//Gameboard size dimensions
	public static int rows = 6;
	public static int columns = 7;
	//Player that goes first
	public static char turn = 'O';
	public static int playOutsX = 3000; //adjust iterations for computer player 1
	public static int playOutsO = 3000; //adjust iterations for computer player 2
	public static int bestAI = 0;
	public static boolean humanPlayer1 = true;
	public static boolean humanPlayer2 = false;
	static Scanner play = new Scanner(System.in);
	
	public static void main(String[] args) {
		
		long startTime = System.nanoTime();
		boolean gameLoop = true;
		char[][] gameBoard = new char [rows][columns];
		while(gameLoop){
			buildBoard(gameBoard, turn);
			System.out.println("\n" + "Player " + turn + "'s Turn");
			if (turn == 'O' && humanPlayer1) //Human player 1
			{
				playTurn(turn, gameBoard);
			}
			else if (turn == 'X' && humanPlayer2) //Human player 2
			{
				playTurn(turn, gameBoard);
			}
			else if (turn == 'X' && !humanPlayer2) //MCTS AI initialization for player 1
			{
				playAI(gameBoard);
			}
			else if (turn == 'O' && !humanPlayer1) //MCTS AI initialization for player 2
			{
				playAI(gameBoard);
			}
			if (checkWin(gameBoard, turn))
			{
				buildBoard(gameBoard, turn);
				System.out.println("\nPlayer "+ turn +" wins!");
				break;
			}
			else if (checkTie(gameBoard))
			{
				buildBoard(gameBoard, turn);
				System.out.println("It's a tie!");
				break;
			}
			if (turn == 'O'){turn = 'X';} //changes turn
			else{turn='O';}
			
		}
			buildBoard(gameBoard, turn);
			long elapsedTime = System.nanoTime() - startTime;
			//System.out.println(elapsedTime);
		
	}
	//sees if board is full if there is no winner
	public static boolean checkTie(char[][] gameBoard) {
		if (!checkWin(gameBoard, turn))
		{
			for (int i = 0; i < rows; i++)
			{
				for (int j = 0; j < columns; j++)
				{
					if (gameBoard[i][j] == '-')
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void buildBoard(char[][] boardState, char player)
	{
		
		System.out.println("");
		
		for(int i = 0; i < rows; i++){
			
			for(int j = 0; j < columns; j++){
				if (j == 0){System.out.print("| ");}
				if (boardState[i][j] == '\0'){boardState[i][j] = '-';} //fills empty spaces with '-'
				
				
				System.out.print(boardState[i][j] + " | ");		
			}	
		System.out.println("\n-----------------------------");	
		}	
	}
	
	//asks human player to make move
	public static void playTurn (char color, char[][] boardState){
		
		System.out.println("In which column would you like to drop your piece?");
		int piece = 0;
		while(true)	
		{
			
			try {
				piece = play.nextInt(); //asks user to input a column to drop it in
				//if input is within range of columns and there is an open space in that column, continue to drop piece
				if ((piece > 0 && piece <= columns) && (boardState[0][piece-1]=='-')){break;}
				
				//check for full columns
				else if ((piece > 0 && piece <= columns) && (boardState[0][piece-1]=='X' || boardState[0][piece-1]=='O')){
					
					System.out.println();
					System.out.println("Sorry, this column is full!");
					System.out.println();
					System.out.print("In which column would you like to drop your piece?");
					
					
				}
				//make sure input is within valid range of columns
				else if(piece < 1 || piece > columns){
					System.out.println();
					System.out.println("Please input a number from 1 to " + columns + "!");
					System.out.println();
					System.out.print("In which column would you like to drop your piece?");
					
					}
				
				
			}
			//makes sure input is a valid int
			catch (InputMismatchException e){
				System.out.println();
				System.out.println("Please input a valid integer!");
				System.out.println();
				System.out.print("In which column would you like to drop your piece?");
				play.next();
			}
		}
		piece--; //adjust for index starting at 0
		//drop piece in first available row within column
		for(int i = (rows-1); i >= 0; i--)
		{
			
			if (boardState[i][piece]=='-')
			{
				
				boardState[i][piece]= color;
				break;
				
			}
			
		}
		
		
			
		
	}
	
	public static boolean checkWin (char[][] boardState, char winner){
		
		//check horizontal win
		for (int i = 0; i < rows; i++){
			int horizontalCounter = 0;
			for (int j = 0; j < columns; j++){
				
				if (boardState[i][j] == winner){
					horizontalCounter++; //add 1 for every time the color in question comes up
				}
				
				else{horizontalCounter = 0;} //resets counter if same two pieces are not in a row
				
				if (horizontalCounter == 4){
					
					return true;
				}
				
			}
			
		}
		
		//check vertical win
		for (int i = 0; i < columns; i++){
			int vertCounter = 0;
			for (int j = 0; j < rows; j++){
				
				if (boardState[j][i] == winner){
					vertCounter++; //add 1 for every time the color in question comes up
				}
				
				else{vertCounter = 0;} //resets counter if same two pieces are not in a row
				
				if (vertCounter == 4){
					
					return true;
				}
				
			}
			
		}
		
		for (int i = 0; i < columns; i++){
			
			for (int j = 0; j < rows; j++){
				//check diagonals down and to the right (negative slope)
				if (j <= rows - 4 && i <= columns - 4) //starts 4 up from the bottom and 4 away from the right to prevent OutOfIndex
				{
					if (boardState[j][i]==winner 
							&& boardState[j+1][i+1]==winner 
							&& boardState[j+2][i+2]==winner 
							&& boardState[j+3][i+3]==winner){
						
						return true;
					}
				}
				//check diagonals down and to the left (positive slope)
				if (j >= 3 && i <= 3) //starts 4 down from the top and 4 away from the left to prevent OutOfIndex
				{
					if (boardState[j][i]==winner 
							&& boardState[j-1][i+1]==winner 
							&& boardState[j-2][i+2]==winner 
							&& boardState[j-3][i+3]==winner){
						
						return true;
					}
				}
			}
			
		}
		
		 return false;
		
	}
	
	public static void playAI (char[][] gameBoard)
	{
		Node root = new Node(gameBoard);
		int playOuts; //number of iterations to go through (level of MCTS)
		if (turn == 'O'){playOuts = playOutsO;}
		else {playOuts = playOutsX;}
		for (int i = 0; i < playOuts; i++) //runs through MCTS algorithm sequence for number of iterations
		{
			root.monteCarloTreeSearch();			
		}
		double bestNodeValue = 0;
		double nodeValue = 0;
		for (int j = 0; j < root.children.size(); j++) //iterate through root's children to find node with greatest average reward (estimated value)
		{
			if (root.children.get(j) == null){continue;} //accounts for place-holders in root node's children that occurs when columns fill up
			nodeValue = root.children.get(j).nodeWins / root.children.get(j).nodeVisits; //calculate average reward
			System.out.println(j + ": " + root.children.get(j).nodeWins +" " + root.children.get(j).nodeVisits); //prints each child's number of wins and number of visits
			if (nodeValue >= bestNodeValue)
			{
				bestAI = j; //stores index of best child to drop piece, as index correlates with column drop
				bestNodeValue = nodeValue;
				
			}
		}
		
		System.out.println(bestAI);
		for(int i = (rows-1); i >= 0; i--)
		{
			if (gameBoard[i][bestAI]=='-')
			{
				
				gameBoard[i][bestAI]= turn; //drops piece based on child with highest value
				break;
				
			}
		}
	}
	
	

}

