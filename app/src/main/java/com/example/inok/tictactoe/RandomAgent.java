package com.example.inok.tictactoe;

import com.example.inok.tictactoe.Board.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Bot that makes random moves
 */

public class RandomAgent implements Bot {

  @Override
  public int nextMove(Board board) {
    List<Integer> emptyPositions = getEmptyPositions(board.getState());
    int index = (int) (Math.random() * emptyPositions.size());
    return emptyPositions.get(index);
  }

  private List<Integer> getEmptyPositions(Cell[] cells) {
    List<Integer> emptyPositions = new ArrayList<>();
    for (int i = 0; i < cells.length; i++) {
      if (cells[i] == Cell.EMPTY) {
        emptyPositions.add(i);
      }
    }
    return emptyPositions;
  }
}
