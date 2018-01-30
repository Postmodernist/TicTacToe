package com.example.inok.tictactoe;

import com.example.inok.tictactoe.mcts.Player;

import java.util.List;
import java.util.Random;

/**
 * Bot that makes random moves
 */

public class RandomAgent implements Bot {

  private Random rnd = new Random();

  @Override
  public int nextMove(Board board, Player player) {
    List<Integer> emptyPositions = board.getEmptyPositions();
    int index = rnd.nextInt(emptyPositions.size());
    return emptyPositions.get(index);
  }
}
