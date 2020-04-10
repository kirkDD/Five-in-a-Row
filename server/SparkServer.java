import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.Filter;

import java.util.HashMap;

// compile with -encoding UTF8 -classpath .;C:/Users/Leo/BunchOfJars/* -sourcepath .
// run with
// java -classpath .;C:/Users/Leo/BunchOfJars/*;C:/Users/Leo/BunchOfJars/jetty/*;  SparkServer


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
            }
        };
        Spark.afterAfter(filter); // Applies this filter even if there's a halt() or exception.

        ////////////////////////
        // End of CORSFILTER  //
        ////////////////////////


        // define the routes 
        get("/", (req, res) -> {
        	return "Hello World"; 
        });


	}
}