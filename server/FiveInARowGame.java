

import java.lang.Math;


public class FiveInARowGame {
  
  	// the game board
	private int[][] board;

	// 1 - player A
	// -1 - player B
	// 0 - cannot play anymore, maybe a win?
	private int turn;

	// the game? 
	private int gameState;


	/**
	 * test main class
	 */
	public static void main(String[] args) {
		FiveInARowGame game = new FiveInARowGame(10);
		System.out.println(game.makeMove(0, 0, 1));	
		System.out.println(game.makeMove(5, 0, -1));
		System.out.println(game.makeMove(0, 1, 1));	
		System.out.println(game.makeMove(5, 1, -1));
		System.out.println(game.makeMove(0, 2, 1));	
		System.out.println(game.makeMove(5, 2, -1));
		System.out.println(game.makeMove(0, 3, 1));	
		System.out.println(game.makeMove(5, 3, -1));	
		System.out.println(game.makeMove(0, 4, 1));	
		System.out.println(game.makeMove(5, 4, -1));
	}

	/**
	 * initialize Five-in-a-row game board
	 * player with id 1 goes first
	 * @param  boardSize the size of board
	 * @return           [description]
	 */
	public FiveInARowGame(int boardSize) {
		board = new int[boardSize][boardSize];
		turn = 1;
	}

	/**
	 * reset the game
	 * only when game is 
	 */
	public String reset() {
		return hardReset();
	}
	private String hardReset() {
		// reset no matter what
		board = new int[board.length][board.length];
		turn = 1;
		return "good";
	}


	/**
	 * rep expose and return the board
	 */
	public int[][] getBoard() {
		return this.board;
	}

	/**
	 * make a move in game
	 * @param  player int either 1 or -1
	 * @param  x      [description]
	 * @param  y      [description]
	 * @return        a status code in string
	 */
	public String makeMove(int x, int y, int player) {
		if (turn == 0) {
			return "wrong";
		}
		if (board[x][y] == 0 && turn == player) {
			board[x][y] = player;
			turn = -turn;
			this.updateGameState();
			return "good";
		} else {
			return "bad";
		}
	}

	/**
	 * see if someone wins somehow
	 * @modifies 
	 */
	private void updateGameState() {
		// check if -1 wins O(4n)
		if (checkGridFor(-1)) {

		}
	}
	private boolean checkGridFor(int player) {
		// check vertical and horizontal
		for (int i = 0; i < board.length; i++) {	// rows?
			int row = 0;
			int col = 0;
			for (int j = 0; j < board[0].length; j++) {
				row = (board[i][j] == player) ? row + 1 : 0;
				col = (board[j][i] == player) ? col + 1 : 0;
				if (row == 5 || col == 5) {
					return true;
				}
			}
		}
		// check both diagonal
		for (int index = board.length - 1; index > - board.length; index--) {
			int j = Math.max(0, -index);
			int i = (j == 0) ? index : 0;
			int countA = 0;
			int countB = 0;
			for (; i < board.length && j < board.length; i++, j++) {
				countA = (board[i][j] == player)? countA + 1 : 0; 
				countB = (board[board.length - i][j] == player)? countB + 1 : 0; 
				if (countA == 5 || countB == 5) {
					return true;
				}
			}
		} 
		return false;
	}





}
