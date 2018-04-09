package com.example.inok.tictactoe.agents;

import com.example.inok.tictactoe.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Agent makes random moves
 */
public class RandomAgent implements Agent {

  private Random random = new Random();

  @Override
  public int getAction() {
    List<Integer> validActions = new ArrayList<>(Game.state.getValidActions());
    return validActions.get(random.nextInt(validActions.size()));
  }
}
