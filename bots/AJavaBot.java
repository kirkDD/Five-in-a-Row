
// NET stuff
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Decode stuff
import com.google.gson.Gson;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

// map
import java.util.*;

public class AJavaBot {
	
	/////////////////
	// KNOWN stuff //
	/////////////////

	private static final String BASE_LINK = "http://localhost:4567/";
	private static final HttpClient CLIENT = HttpClient.newHttpClient();
	private static final int ID = -1;
	private static final char ME = '+';
	private static final char THEM = '-';
	private static final char EMPTYCHAR = '.';

	private static Gson GSON = new Gson();

	// test feature
	private static int sizeOffSetX = 0;
	private static int sizeOffSetY = 0;

	// debugging
	private static int COUNT = 0;

	//////////
	// Main //
	//////////
	
	public static void main(String[] args) throws IOException, InterruptedException {

		if (!serverRunning()) {
			System.out.println("Server is not running, exit!");
			return;
		}

		// debug 
		// while (true) {
		// 	Map<Integer, List<int[]>> boardMap = getBoardAsMap();
		// 	System.out.println(staticEval(mapToBoard(boardMap, 1)));
			
		// 	System.out.println("Pause");
		// 	Thread.sleep(2000);

		// }

		// get the board
		while (true) {
			if (isMyTurn()) {
				Map<Integer, List<int[]>> boardMap = getBoardAsMap();
				// assert boardListMap.size() == 2;
				// System.out.println(staticEval());
				COUNT = 0;
				int[] move = makeAMove(mapToBoard(boardMap, 1));
				System.out.println(move[0] + " " + move[1]);
				int x = move[0] + sizeOffSetX;
				int y = move[1] + sizeOffSetY;
				// make the move
				httpGET("makeMove" + "?x=" + x + "&y=" + y + "&player=" + ID);
			} else {
				System.out.println("Wait1");
				Thread.sleep(1234);
				System.out.println("Wait2");
				Thread.sleep(1234);
			}
			System.out.println("Pause");
			Thread.sleep(1234);
		}
		
	}


	////////////////////////
	// supporting methods //
	////////////////////////


	// use GET and return a String response
	private static String httpGET(String appendURL) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(BASE_LINK + appendURL))
			    .build();

			HttpResponse<String> response = CLIENT.send(request,
				HttpResponse.BodyHandlers.ofString());
			System.out.println("GET: " + response.body());
			return response.body();
		} catch (Exception e) {
			System.out.println(e);
			return "";
		}
	}
	// check server
	private static boolean serverRunning()  throws IOException, InterruptedException {
		try {
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(BASE_LINK))
			    .build();

			HttpResponse<String> response = CLIENT.send(request,
				HttpResponse.BodyHandlers.ofString());
			
			System.out.println(response.body());
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	// get the board as list
	private static Map<Integer, List<int[]>> getBoardAsMap() {
		return GSON.fromJson(httpGET("getGameBoardAsList"), 
			new TypeToken<Map<Integer, List<int[]>>>(){}.getType());
	}
	// make the listMap to a small board
	// possible fail ⚠ out of bound!!!!
	private static char[][] mapToBoard(Map<Integer, List<int[]>> map, int borderSize) {
		// calculate a small size
		int maxW = 0;
		int minW = 999;
		int maxH = 0;
		int minH = 999;

		List<int[]> myL = map.get(ID);
		List<int[]> otherL = map.get(-ID);

		if (myL.size() == 0 && otherL.size() == 0) {
			System.out.println("empty board, first move");
			sizeOffSetX = 5;
			sizeOffSetY = 5;
			char[][] result = new char[1][1];
			result[0][0] = EMPTYCHAR;
			return result;
		}

		for (int i = 0; i < myL.size(); i++) {
			maxW = Math.max(maxW, myL.get(i)[0]);
			minW = Math.min(minW, myL.get(i)[0]);

			maxH = Math.max(maxH, myL.get(i)[1]);
			minH = Math.min(minH, myL.get(i)[1]);
		}
		for (int i = 0; i < otherL.size(); i++) {
			maxW = Math.max(maxW, otherL.get(i)[0]);
			minW = Math.min(minW, otherL.get(i)[0]);

			maxH = Math.max(maxH, otherL.get(i)[1]);
			minH = Math.min(minH, otherL.get(i)[1]);
		}

		// System.out.println(maxW + " " + maxH);
		// System.out.println(minW + " " + minH);

		maxW += borderSize;
		maxH += borderSize;
		minW = (minW - borderSize < 0) ? 0 : minW - borderSize;
		minH = (minH - borderSize < 0) ? 0 : minH - borderSize;

		int newSize = Math.max(maxW - minW, maxH - minH) + 1;

		// record offset
		sizeOffSetX = minW;
		sizeOffSetY = minH;

		// build board ⚠ A square
		char[][] result = new char[newSize][newSize];
		// fill with empty char
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = EMPTYCHAR;
			}
		}
		for (int i = 0; i < myL.size(); i++) {
			result[myL.get(i)[0] - sizeOffSetX][myL.get(i)[1] - sizeOffSetY] = ME;
		}
		for (int i = 0; i < otherL.size(); i++) {
			result[otherL.get(i)[0] - sizeOffSetX][otherL.get(i)[1] - sizeOffSetY] = THEM;
		} 

		// debug
		// System.out.println("_____________");
		// printCharArr(result);
		// System.out.println("_____________");

		return result;
	}

	// helper: print char[][]
	private static void printCharArr(char[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j] == ME || a[i][j] == THEM) {
					System.out.print(a[i][j]);
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}

	// return true iff its my turn
	private static boolean isMyTurn() {
		int turn = GSON.fromJson(httpGET("whoseTurn"), int.class);
		return turn == ID;
	}



	private static int[] makeAMove(char[][] currBoard) {
		System.out.println("Board size " + currBoard.length + " " + currBoard[0].length);
		
		// run a depth 5 MiniMax?
		int[] moveScorePair = miniMax(currBoard, true, 3, -9999999, true);

		System.out.println("Best score is " + moveScorePair[2]);
		return new int[]{moveScorePair[0], moveScorePair[1]};
	}

	// miniMax helper
	// returns int[3], [x, y, score]
	private static int[] miniMax(char[][] board, boolean maximize, int remainLevel, int otherCurrBest, boolean isRoot) {
		// System.out.println(remainLevel);
		int[] moveScorePair;
		if (maximize) {
			moveScorePair = new int[]{0, 0, -9999999};
		} else {	
			moveScorePair = new int[]{0, 0, 9999999};
		}
		// explore all possibilities
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == EMPTYCHAR) {

					// can place here
					if (maximize) {
						board[i][j] = ME;
					} else {
						board[i][j] = THEM;
					}
					int[] newScorePair;
					if (remainLevel == 0) {
						newScorePair = new int[]{i, j, staticEval(board)};
					} else {
						// recurse to get the best move
						newScorePair = miniMax(board, !maximize, remainLevel - 1, moveScorePair[2], false);
					}
					if (maximize) {
						if (newScorePair[2] > otherCurrBest && ! isRoot) {
							moveScorePair = new int[]{0, 0, 99999999};
							// undo the move
							board[i][j] = EMPTYCHAR;
							return moveScorePair;
						} 
						if (newScorePair[2] > moveScorePair[2]) {
							moveScorePair = newScorePair;
						}
					} else {
						// min time
						// pruning
						if (newScorePair[2] < otherCurrBest) {
							moveScorePair = new int[]{0, 0, -99999999};
							// undo the move
							board[i][j] = EMPTYCHAR;
							return moveScorePair;
						}
						if (newScorePair[2] < moveScorePair[2]) {
							moveScorePair = newScorePair;
						}
					}
					// undo the move
					board[i][j] = EMPTYCHAR;
					if (isRoot) {
						System.out.println(moveScorePair[0] + " " + moveScorePair[1] + " " + moveScorePair[2]);
						printCharArr(board);
					}
				}
			}
		}
		
		// System.out.println(moveScorePair[2]);
		return moveScorePair;
	}


	// return the goodness of board 
	// use ID as self
	private static int staticEval(char[][] b) {
		COUNT ++;
		if (COUNT % 32768 == 0) {
			// System.out.println(b.length);
			// System.out.println(b[0].length);
			System.out.println(COUNT);
		}
		// printCharArr(b);
		// if win return 100, lost -100
		if (checkGridFor(ME, b)) {
			return 10000;
		}
		if (checkGridFor(THEM, b)) {
			return -10000;
		}
		// no winner what to do ?
		// count 4, 3, 2, in a row
		int[] points = new int[6];
		points[0] = 0;
		points[1] = 1;
		points[2] = 10;
		points[3] = 100;
		points[4] = 1000;
		points[5] = 10000;
		int score = 0;
		// count rows
		for (int i = 0; i < b.length; i++) {
			int countME = 0;
			int countTHEM = 0;
			for (int j = 0; j < b[0].length; j++) {
				if (b[i][j] == ME) {
					score -= points[countTHEM];
					countTHEM = 0;
					countME ++;
				} else if (b[i][j] == THEM) {
					score += points[countME];
					countME = 0;
					countTHEM ++;
				} else {
					score += points[countME];
					score -= points[countTHEM];
					countME = 0;
					countTHEM = 0;
				}
			}
			score += points[countME];
			score -= points[countTHEM];
		}
		// System.out.println("Row Score " + score);

		// count columns
		for (int i = 0; i < b.length; i++) {
			int countME = 0;
			int countTHEM = 0;
			for (int j = 0; j < b[0].length; j++) {
				if (b[j][i] == ME) {
					score -= points[countTHEM];
					countTHEM = 0;
					countME ++;
				} else if (b[j][i] == THEM) {
					score += points[countME];
					countME = 0;
					countTHEM ++;
				} else {
					score += points[countME];
					score -= points[countTHEM];
					countME = 0;
					countTHEM = 0;
				}
			}
			score += points[countME];
			score -= points[countTHEM];
		}
		// System.out.println("Col Row Score " + score);


		// diagonal
		for (int index = b.length - 1; index > - b.length; index--) {
			int j = Math.max(0, -index);
			int i = (j == 0) ? index : 0;
			// going rows
			int alpah = 0;
			int beta = 0;
			for (; i < b.length && j < b[0].length; i++, j++) {
				if (b[i][j] == ME) {
					score -= points[beta];
					beta = 0;
					alpah++;
				} else if (b[i][j] == THEM) {
					score += points[alpah];
					alpah = 0;
					beta++;
				} else {
					score += points[alpah];
					score -= points[beta];
					alpah = 0;
					beta = 0;
				}				
				if (i == b.length - 1 || j == b[0].length - 1) {
					// end of row
					score += points[alpah];
					score -= points[beta];
				}
			}
			// going other direction
			j = Math.max(0, -index);
			i = (j == 0) ? index : 0;
			alpah = 0;
			beta = 0;
			for (; i < b.length && j < b[0].length; i++, j++) {
				if (b[i][b[0].length - 1 - j] == ME) {
					score -= points[beta];
					beta = 0;
					alpah++;
				} else if (b[i][b[0].length - 1 - j] == THEM) {
					score += points[alpah];
					alpah = 0;
					beta++;
				} else {
					score += points[alpah];
					score -= points[beta];
					alpah = 0;
					beta = 0;
				}				
				if (i == b.length - 1 || j == b[0].length - 1) {
					// end of row
					score += points[alpah];
					score -= points[beta];
				}
			}
		} 

		// System.out.println(score);
		// System.out.println("Combined Score " + score);
		return score;
	}

	// helper for staticEval
	// true if player wins 
	// take char and char[][]
	private static boolean checkGridFor(char player, char[][] board) {
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
			for (; i < board.length && j < board[0].length; i++, j++) {
				countA = (board[i][j] == player)? countA + 1 : 0; 
				countB = (board[board.length - 1 - i][j] == player)? countB + 1 : 0; 
				if (countA == 5 || countB == 5) {
					return true;
				}
			}
		} 
		return false;
	}




}