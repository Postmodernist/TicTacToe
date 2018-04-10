# TicTacToe

It was started as a tic-tac-toe game played versus classic MCTS agent, <br>
but the game was trivial and I had to improve it. As a result I came up <br>
with 'Four' game, which has the same basic idea: the first player to get <br>
4 pieces in a line wins. The board size is 7x7 to give some space for <br>
different strategies.<br>
<br>
Classic MCTS did quite well, but not perfect and took a long time to <br>
simulate, so I replaced it with probabilistic version based on neural <br>
net, which is responsible for evaluating boards and predicting moves. <br>
<br>
The model was trained in another project and used here only for inference. <br>
https://github.com/Postmodernist/BoardAI<br>
<br><br>
![screenshot](screenshot.jpg?raw=true "Game")
