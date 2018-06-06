package com.alexbaryzhikov.tictactoe.agents;

import com.alexbaryzhikov.tictactoe.game.Game;
import com.alexbaryzhikov.tictactoe.mcts.Mcts;

/**
 * Agent uses Monte-Carlo Tree Search to choose next move
 */
public class MctsAgent implements Agent {

  private Mcts mcts = new Mcts();

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
