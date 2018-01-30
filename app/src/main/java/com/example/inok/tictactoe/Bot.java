package com.example.inok.tictactoe;

import com.example.inok.tictactoe.mcts.Player;

public interface Bot {
  int nextMove(Board board, Player player);
}
