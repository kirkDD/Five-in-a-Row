
# How to play the game

## Talking to server via HTTP requests

### /
The default URL
- Request (none)
- Response (text)
	> response.text()
	
		1. "hello world" if the server is running
		2. Error if it is not
---------------
### /getGameBoard
Obtain the game's current state
 - Request (none)
- Response (JSON) 

	> response.json() 
	
	a 2D array with:
		 
		1. 0 for open positions
		2. -1 for player A
		3. 1 for player B 
			
---------------------------------------

### /makeMove
Make a move in the game
- Request 
	1. POST (JSON)
		>JSON.stringify([ position x, position y, player id ])	
		body should be a JSON string with this structure

			the player id is either 1 or -1 
	
	2. GET (EncodeURI)
		Supply three arguments x, y, player
			
			var uri = "?x=12&y=12&player=-1"
			encodeURI(uri)

- Response (text)
	> response.text()
		
		1. "good" - if the move is successful, the move is made
		2. "bad" - if it's not the player's turn or the move is illegal
		4. "wrong" - if the winner is decided, should use /getResult
 
-----------------

### /getResult
See the current game state
 - Request (none)
 - Response (JSON)
	>response.json()
		
		the game state:
		0 -> game is still going
		1 -> player 1 won
		-1 -> player -1 won
		2 -> draw

-----------------

### /reset
Reset the game
 - Request (none)
			
 - Response (text)
	>response.text()
		
		1. "good", the game is reset
		2. "bad", the game is not reset somehow
