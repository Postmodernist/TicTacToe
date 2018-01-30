package com.example.inok.tictactoe;

import com.example.inok.tictactoe.mcts.MonteCarloTreeSearch;
import com.example.inok.tictactoe.mcts.Player;

/**
 * Bot uses Monte-Carlo Tree Search to choose next move
 */

public class MctsAgent implements Bot {

  private MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();

  @Override
  public int nextMove(Board board, Player player) {
    return mcts.getNextMove(board, player);
  }
}
