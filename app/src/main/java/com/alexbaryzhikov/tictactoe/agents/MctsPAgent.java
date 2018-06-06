package com.alexbaryzhikov.tictactoe.agents;

import android.content.res.AssetManager;

import com.alexbaryzhikov.tictactoe.game.Game;
import com.alexbaryzhikov.tictactoe.mcts.MctsP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Agent uses Probabilistic Monte-Carlo Tree Search to choose next move
 */
public class MctsPAgent implements Agent {

  private MctsP mcts;
  private Random random = new Random();

  public MctsPAgent(AssetManager assetManager) {
    mcts = new MctsP(assetManager);
  }

  @Override
  public int getAction() {
    int[] visits = mcts.getDistribution(Game.state);
    if (visits == null) {
      return -1;
    }
    int max = Integer.MIN_VALUE;
    List<Integer> candidates = new ArrayList<>();
    for (int i = 0; i < visits.length; i++) {
      if (visits[i] > max) {
        max = visits[i];
        candidates.clear();
        candidates.add(i);
      } else if (visits[i] == max) {
        candidates.add(i);
      }
    }
    return candidates.get(random.nextInt(candidates.size()));
  }
}
