import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.Filter;

import java.util.HashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.*;


//⚔
//I am on phone and I will try to nudge you in the rght direction. 
//You need to follow the following 3 steps mentioned in Bulletproof SSL and TLS, pages 436-439:
// Creating a key and a self signed certificate
// Creating a certificate signing request
// Importing certificates



// all response are JSON
public class SparkServer {
	public static void main(String[] args) {
        // HTTPS instead of HTTP, not working
        // secure("KeyStore.jks", "123456", null, null);


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
        FiveInARowGame game = new FiveInARowGame(20);


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
        // so removed, to hard

        // second make move support get
        get("/makeMove", (req, res) -> {
            String xStr = req.queryParams("x");
            String yStr = req.queryParams("y");
            String playerStr = req.queryParams("player");
            if (xStr == null || yStr == null || playerStr == null) {
                res.body(gson.toJson("Missing params, need x, y, player"));
                res.type("application/json");
                return "";
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
                res.body(gson.toJson(e.toString()));
                res.type("application/json");
                return "";
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

        // return whose turn it is 
        get("/whoseTurn", (req, res) -> {
            res.body(gson.toJson(game.nextPlayer()));
            res.type("test/json");
            return "";
        });

	}
}