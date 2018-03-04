package com.example.inok.tictactoe.agents;

import com.example.inok.tictactoe.game.Game;
import com.example.inok.tictactoe.mcts.Mcts;

/**
 * Agent uses Monte-Carlo Tree Search to choose next move
 */

public class MctsAgent implements Agent {

  private Mcts mcts = new Mcts();

  /**
   * Return action with max MCTS score
   */
  @Override
  public int getAction() {
    int[] distribution = mcts.getDistribution(Game.state);
    int max = Integer.MIN_VALUE;
    int action = -1;
    for (int i = 0; i < distribution.length; i++) {
      if (distribution[i] > max) {
        max = distribution[i];
        action = i;
      }
    }
    return action;
  }
}
