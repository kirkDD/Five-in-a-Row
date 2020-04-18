
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
	private static final String BASE_LINK = "http://localhost:4567/";
	private static final HttpClient CLIENT = HttpClient.newHttpClient();
	private static final int ID = -1;
	private static final char ME = '+';
	private static final char THEM = '-';

	private static Gson GSON = new Gson();

	// test feature
	private static int sizeOffSetX = 0;
	private static int sizeOffSetY = 0;
	
	public static void main(String[] args) throws IOException, InterruptedException {

		if (!serverRunning()) {
			System.out.println("Server is not running, exit!");
			return;
		}

		// get the board
		while (true) {
			if (isMyTurn()) {
				Map<Integer, List<int[]>> boardMap = getBoardAsMap();
				// assert boardListMap.size() == 2;
				System.out.println(staticEval(mapToBoard(boardMap, 3)));
				System.out.println("Wait");
				Thread.sleep(1234);
			} else {
				System.out.println("Sleep1");
				Thread.sleep(1234);
				System.out.println("Sleep2");
				Thread.sleep(1234);
				System.out.println("Sleep3");
				Thread.sleep(1234);
			}
		}
		
	}

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

		maxW += 3;
		maxH += 3;
		minW = (minW - borderSize < 0) ? 0 : minW - borderSize;
		minH = (minH - borderSize < 0) ? 0 : minH - borderSize;

		// System.out.println(maxW);
		// System.out.println(maxH);
		// System.out.println(minW);
		// System.out.println(minH);

		int newSize = Math.max(maxW - minW, maxH - minH);

		// record offset
		sizeOffSetX = minW;
		sizeOffSetY = minH;

		// build board ⚠ A square
		char[][] result = new char[newSize][newSize];
		for (int i = 0; i < myL.size(); i++) {
			result[myL.get(i)[0] - sizeOffSetX][myL.get(i)[1] - sizeOffSetY] = ME;
		}
		for (int i = 0; i < otherL.size(); i++) {
			result[otherL.get(i)[0] - sizeOffSetX][otherL.get(i)[1] - sizeOffSetY] = THEM;
		} 

		// debug
		System.out.println("_____________");
		// printCharArr(result);
		System.out.println("_____________");

		return result;
	}

	// helper: print char[][]
	private static void printCharArr(char[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				System.out.print(a[i][j]);
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
		return null;
	}


	// return the goodness of board 
	// use ID as self
	private static int staticEval(char[][] b) {
		printCharArr(b);
		// if win return 100, lost -100
		if (checkGridFor(ME, b)) {
			return 10000;
		}
		if (checkGridFor(THEM, b)) {
			return -10000;
		}
		// no winner what to do ?
		// count 4, 3, 2, in a row
		Map<Integer, Integer> points = new HashMap<>();
		points.put(0, 0);
		points.put(1, 1);
		points.put(2, 10);
		points.put(3, 100);
		points.put(4, 1000);
		int score = 0;
		for (int i = 0; i < b.length; i++) {
			// going rows
			int alpahRow = 0;
			int betaRow = 0;

			// going column
			int alpahCol = 0;
			int betaCol = 0;
			for (int j = 0; j < b[0].length; j++) {
				// going rows
				if (b[i][j] == ME) {
					score -= points.get(betaRow);
					betaRow = 0;
					alpahRow++;
				} else if (b[i][j] == THEM) {
					score += points.get(alpahRow);
					alpahRow = 0;
					betaRow++;
				} else {
					score += points.get(alpahRow);
					score -= points.get(betaRow);
					alpahRow = 0;
					betaRow = 0;
				}
				if (j == b[0].length - 1) {
					// end of row
					score += points.get(alpahRow);
					score -= points.get(betaRow);
				}
				// going column
				if (b[j][i] == ME) {
					score -= points.get(betaCol);
					betaCol = 0;
					alpahCol++;
				} else if (b[j][i] == THEM) {
					score += points.get(alpahCol);
					alpahCol = 0;
					betaCol++;
				} else {
					score += points.get(alpahCol);
					score -= points.get(betaCol);
					alpahCol = 0;
					betaCol = 0;
				}
				if (i == b.length - 1) {
					// end of column
					score += points.get(alpahCol);
					score -= points.get(betaCol);
				}
			}
		}
		// diagonal
		for (int index = b.length - 1; index > - b.length; index--) {
			int j = Math.max(0, -index);
			int i = (j == 0) ? index : 0;
			int myDir1 = 0;
			int countB = 0;
			// going rows
			int alpahRow = 0;
			int betaRow = 0;
			for (; i < b.length && j < b[0].length; i++, j++) {
				if (b[i][j] == ME) {
					score -= points.get(betaRow);
					betaRow = 0;
					alpahRow++;
				} else if (b[i][j] == THEM) {
					score += points.get(alpahRow);
					alpahRow = 0;
					betaRow++;
				} else {
					score += points.get(alpahRow);
					score -= points.get(betaRow);
					alpahRow = 0;
					betaRow = 0;
				}
				
				// going column
				int alpahCol = 0;
				int betaCol = 0;
				if (b[j][i] == ME) {
					score -= points.get(betaCol);
					betaCol = 0;
					alpahCol++;
				} else if (b[j][i] == THEM) {
					score += points.get(alpahCol);
					alpahCol = 0;
					betaCol++;
				} else {
					score += points.get(alpahCol);
					score -= points.get(betaCol);
					alpahCol = 0;
					betaCol = 0;
				}
				if (j == b[0].length - 1 || i == b.length) {
					// end of row
					score += points.get(alpahRow);
					score -= points.get(betaRow);
					// reset
					alpahRow = 0;
					betaRow = 0;
				}
			}
		} 

		// System.out.println(score);
		return score;
	}


	// true if player wins take char abd char[][]
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