# How to play the game

## Talking to server via HTTP request options:

# /getGameBoard
Response should be ok with status 200
Use .json() to decode to data structure 
Structure is a 2D array with 0 indicating open places and -1 for player A, 1 for player B

# /makeMove
Request body should be a JSON string containing:
- position, and player how?
Response should be good,
if invalid player or move is made, or its not your turn, response will be "bad" and no move will be made. 
 
