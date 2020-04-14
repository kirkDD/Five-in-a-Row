





public class FiveInARowGame {
  

  	/*
  	the game board
  	 */
	private int[][] board;

	/*
	1 - player A
	-1 - player B
	0 - cannot play anymore, maybe a win?
	 */
	private int turn;


	private int gameState;

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
	 */
	public void reset() {
		board = new int[board.length][board.length];
		turn = 1;
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

	}


	/**
	 * rep expose and return the board
	 */
	public int[][] getBoard() {
		return this.board;
	}


}
