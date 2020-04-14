import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.Filter;

import java.util.HashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.*;

// all response are JSON
public class SparkServer {
	public static void main(String[] args) {

		///////////////////////
		// CORSFILTER stuff  //
		///////////////////////
        
    	final HashMap<String, String> corsHeaders = new HashMap<>();
		corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers",
                "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
		Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) {
                corsHeaders.forEach(response::header);
                System.out.print("___Client IP: ");
                System.out.print(request.ip());
                System.out.print(" -> ");
                System.out.println(request.url());
            }
        };
        Spark.afterAfter(filter); // Applies this filter even if there's a halt() or exception.

        ////////////////////////
        // End of CORSFILTER  //
        ////////////////////////


        /////////////////
        // initialize  //
        /////////////////
        Gson gson = new Gson();
        FiveInARowGame game = new FiveInARowGame(40);


        // refault 
        get("/", (req, res) -> {
        	return "hello world"; 
        });


        // get board 
        get("/getGameBoard", (req, res) -> {
            res.body(gson.toJson(game.getBoard()));
            res.type("application/json");
            return "";
        });

        // get board as list
        get("/getGameBoardAsList", (req, res) -> {
            res.body(gson.toJson(game.getBoardAsList()));
            res.type("application/json");
            return "";
        });

        // make a move 
        // POST method is not working locally
        post("/makeMove", (req, res) -> {
            // Type type = new TypeToken<List<Integer>>(){}.getType();
            String result = "";
            try {
                System.out.println("In makeMove");
                System.out.println(req.headers());
                System.out.println(req.body());
                List<Integer> aList = gson.fromJson(req.body(), new ArrayList<Integer>().getClass());
                System.out.println(aList.size());
                result = game.makeMove(aList.get(0), aList.get(1), aList.get(2));
            } catch (Exception e) {
                System.out.println(e);
            }
            res.body(gson.toJson(result));
            res.type("application/json");
            return "";
        });

        // second make move support get
        get("/makeMove", (req, res) -> {
            String xStr = req.queryParams("x");
            String yStr = req.queryParams("y");
            String playerStr = req.queryParams("player");
            if (xStr == null || yStr == null || playerStr == null) {
                return "Missing params, need x, y, player";
            }
            int x;
            int y;
            int player;
            try {
                x = Integer.parseInt(xStr);
                y = Integer.parseInt(yStr);
                player = Integer.parseInt(playerStr);
            } catch (Exception e) {
                System.out.println("Error parsing int");
                System.out.println(e);
                return e.toString();
            }
            res.body(gson.toJson(game.makeMove(x, y, player)));
            res.type("application/json");
            return "";
        });


        // get winner
        // return the player id
        get("/getResult", (req, res) -> {
            // an int 
            res.body(gson.toJson(game.getResult()));
            res.type("application/json");
            return "";
        });


        // reset game 
        get("/reset", (req, res) -> {
            res.body(gson.toJson(game.reset()));
            res.type("test/json");
            return "";
        });

	}
}