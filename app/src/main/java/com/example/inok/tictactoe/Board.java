package com.example.inok.tictactoe;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inok.tictactoe.mcts.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Game board
 */

public class Board {

  private static final String TAG = "TAG_" + Board.class.getSimpleName();
  public static final int IN_A_ROW = 4;
  private int size;
  private Player[] cells;

  public Board(int size) {
    this.size = size;
    cells = new Player[size * size];
    for (int i = 0; i < cells.length; i++) {
      cells[i] = Player.NONE;
    }
  }

  public Board(Board board) {
    this.size = board.size();
    cells = new Player[size * size];
    System.arraycopy(board.getCells(), 0, cells, 0, cells.length);
  }

  public int size() {
    return size;
  }

  public Player[] getCells() {
    return cells;
  }

  @Nullable
  public Player get(int position) {
    if (position >= 0 && position < cells.length) {
      return cells[position];
    }
    return null;
  }

  public boolean set(Player player, int position) {
    if (isValidMove(position)) {
      cells[position] = player;
      return true;
    }
    return false;
  }

  /**
   * Check if board has at least one empty cell
   */
  public boolean hasValidMove() {
    for (int i = 0; i < cells.length; i++) {
      if (isValidMove(i)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return a list of indexes of empty cells
   */
  public List<Integer> getValidMoves() {
    List<Integer> validMoves = new ArrayList<>();
    for (int i = 0; i < cells.length; i++) {
      if (isValidMove(i)) {
        validMoves.add(i);
      }
    }
    return validMoves;
  }

  /**
   * Return true if given move is valid
   */
  public boolean isValidMove(int position) {
    // Out of range
    if (position < 0 || position >= cells.length) {
      return false;
    }
    // Cell is not empty
    if (cells[position] != Player.NONE) {
      return false;
    }
    // Cell is next to the border
    if (position < size || position >= cells.length - size || position % size == size - 1
        || position % size == 0) {
      return true;
    }
    // Has neighbor
    int[] neighbors = {position - 1, position + 1, position - size, position + size,
        position - size - 1, position - size + 1, position + size - 1, position + size + 1};
    for (int i : neighbors) {
      if (cells[i] != Player.NONE) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return a list of indexes of empty neighbors
   */
  public List<Integer> getEmptyNeighbors(int position) {
    List<Integer> neighbors = new ArrayList<>();
    int[] allNeighbors = {position - size - 1, position - size, position - size + 1,
        position - 1, position + 1, position + size - 1, position + size, position + size + 1};
    for (int i : allNeighbors) {
      if (i >= 0 && i < size && cells[i] == Player.NONE) {
        neighbors.add(i);
      }
    }
    return neighbors;
  }

  /**
   * Test boards equality
   */
  public boolean isEqual(Board board) {
    if (board == null) {
      return false;
    }
    if (size != board.size()) {
      return false;
    }
    for (int i = 0; i < cells.length; i++) {
      if (cells[i] != board.get(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * String representation of the board
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Player cell : cells) {
      switch (cell) {
        case NONE:
          sb.append(" . ");
          break;
        case PLAYER_A:
          sb.append(" A ");
          break;
        case PLAYER_B:
          sb.append(" B ");
          break;
      }
    }
    return sb.toString();
  }

  /**
   * Test if the board meets win condition
   */
  public boolean hasWinCondition() {
    int maxIndex = size - 1;
    Player[] row = new Player[size];
    Player[] col = new Player[size];
    Player[] diagonal1;
    Player[] diagonal2;
    Player[] diagonal3;
    Player[] diagonal4;

    for (int i = 0; i < size; i++) {
      // Test row and column
      for (int j = 0; j < size; j++) {
        row[j] = cells[i * size + j];
        col[j] = cells[j * size + i];
      }
      if (winCondition(row) || winCondition(col)) {
        return true;
      }
      // Test diagonals
      if (size - i >= IN_A_ROW) {
        diagonal1 = new Player[size - i];
        diagonal2 = new Player[size - i];
        diagonal3 = new Player[size - i];
        diagonal4 = new Player[size - i];
        for (int j = 0; j < size - i; j++) {
          diagonal1[j] = cells[i + j + j * size];
          diagonal2[j] = cells[j + (i + j) * size];
          diagonal3[j] = cells[j + (maxIndex - i - j) * size];
          diagonal4[j] = cells[i + j + (maxIndex - j) * size];
        }
        if (winCondition(diagonal1) || winCondition(diagonal2) || winCondition(diagonal3)
            || winCondition(diagonal4)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Test if the board meets win condition after last move (quick)
   */
  public boolean hasWinCondition(int lastMove) {
    int maxIndex = size - 1;
    Player[] row = new Player[size];
    Player[] col = new Player[size];
    // Test row and column
    int rowN = lastMove / size;
    int colN = lastMove % size;
    for (int i = 0; i < size; i++) {
      row[i] = cells[rowN * size + i];
      col[i] = cells[colN + size * i];
    }
    if (winCondition(row) || winCondition(col)) {
      return true;
    }
    // Test '\' diagonal
    int diagonalN = colN - rowN;
    int diagonalSize = size - Math.abs(diagonalN);
    if (diagonalSize >= IN_A_ROW) {
      Player[] diagonal = new Player[diagonalSize];
      int diagonalStart = diagonalN < 0 ? size * (-diagonalN) : diagonalN;
      for (int i = 0; i < diagonalSize; i++) {
        diagonal[i] = cells[diagonalStart + i * (size + 1)];
      }
      if (winCondition(diagonal)) {
        return true;
      }
    }
    // Test '/' diagonal
    diagonalN = colN + rowN - maxIndex;
    diagonalSize = size - Math.abs(diagonalN);
    if (diagonalSize >= IN_A_ROW) {
      Player[] diagonal = new Player[diagonalSize];
      int diagonalStart = diagonalN < 0 ? maxIndex + diagonalN : maxIndex + size * diagonalN;
      for (int i = 0; i < diagonalSize; i++) {
        diagonal[i] = cells[diagonalStart + i * (size - 1)];
      }
      if (winCondition(diagonal)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if array meets win condition
   */
  private boolean winCondition(Player[] row) {
    if (row == null) {
      Log.e(TAG, "Cells array reference is null");
      return false;
    }
    int matchCount = 0;
    Player lastCell = null;
    for (Player cell : row) {
      if (cell == Player.PLAYER_A || cell == Player.PLAYER_B) {
        if (cell == lastCell) {
          matchCount++;
          if (matchCount == IN_A_ROW) {
            return true;
          }
        } else {
          matchCount = 1;
          lastCell = cell;
        }
      } else {
        matchCount = 0;
        lastCell = cell;
      }
    }
    return false;
  }
}
