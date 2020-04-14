# How to play the game

## Talking to server via HTTP requests

### /
The default URL
- Response (text)
	> response.text()
	
		1. "hello world" if the server is running
		2. Error if it is not
---------------
### /getGameBoard
Obtain the game's current state
- Response (JSON) 

	> response.json() 
	
	a 2D array with:
		 
		1. 0 for open positions
		2. -1 for player A
		3. 1 for player B 
			
---------------------------------------

### /makeMove
Make a move in the game
- Request (JSON)
  body should be a JSON string with this structure
	>JSON.stringify([ position x, position y, player id ])	

		the player id is either 1 or -1 

- Response (text)
	> response.text()
		
		1. "good" - if the move is successful, the move is made
		2. "bad" - if it's not the player's turn
		3. "no" - if the move is illegal, should resend move
		4. "wrong" - if the winner is decided, should use /getResult
 
 -----------------
 ### /getResult
See the winner
 - Request (JSON)
	 body should be player id 
	>JSON.stringify(1)
			
		the player id is either 1 or -1 
			
 - Response (text)
	>response.text()
		
		1. "won"
		2. "lost"
