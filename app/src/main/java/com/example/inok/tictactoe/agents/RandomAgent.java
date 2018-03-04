package com.example.inok.tictactoe.agents;

import com.example.inok.tictactoe.game.Game;

import java.util.Random;

/**
 * Agent that makes random moves
 */

public class RandomAgent implements Agent {

  private Random rnd = new Random();

  @Override
  public int getAction() {
    int[] validActions = Game.state.getValidActions();
    int i = rnd.nextInt(validActions.length);
    return validActions[i];
  }
}
