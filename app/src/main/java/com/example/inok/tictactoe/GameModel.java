package com.example.inok.tictactoe;

import android.util.Log;

import com.example.inok.tictactoe.Board.Cell;

/**
 * Model contains game state and behavior
 */
public class GameModel {

  public static final String TAG = "TAG_" + GameModel.class.getSimpleName();
  public static final int DEFAULT_BOARD_SIZE = 3;
  private static GameModel instance = new GameModel();
  private Board board;
  private Status status;

  private GameModel() {
    status = Status.FINISHED;
  }

  public static GameModel getInstance() {
    return instance;
  }

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
    Log.d(TAG, "Game status: " + status);
  }

  /**
   * Attempt a move, if it is legal -- return true. Update game status according to the move
   * outcome
   */
  public boolean makeMove(int position) {
    boolean result = false;
    switch (status) {
      case FINISHED:
        Log.e(TAG, "Cannot make a move: game is finished");
        break;
      case PLAYER_A_MOVE: // fall through
      case PLAYER_B_MOVE:
        if (result = board.setPosition(toCell(status), position)) {
          if (testWinConditions()) {
            // Current player wins
            GameController.getInstance().displayWinner(status);
            setStatus(Status.FINISHED);
          } else if (board.hasEmptyCell()) {
            // Game continues
            setStatus(otherPlayer(status));
          } else {
            // No more free cells and no winner -- game draw
            setStatus(Status.FINISHED);
            GameController.getInstance().displayWinner(status);
          }
        } else {
          Log.e(TAG, "Invalid move: status " + status + ", position " + position);
        }
        break;
      default:
        Log.e(TAG, "Invalid game status");
        break;
    }
    return result;
  }

  /**
   * Convert status to corresponding cell value, only for 'PLAYER' statuses
   */
  private Cell toCell(Status status) {
    switch (status) {
      case PLAYER_A_MOVE:
        return Cell.PLAYER_A;
      case PLAYER_B_MOVE:
        return Cell.PLAYER_B;
      default:
        Log.e(TAG, "Status to cell value conversion failed: not a player turn");
        return Cell.INVALID;
    }
  }

  /**
   * Get status corresponding to other player's move
   */
  private Status otherPlayer(Status status) {
    switch (status) {
      case PLAYER_A_MOVE:
        return Status.PLAYER_B_MOVE;
      case PLAYER_B_MOVE:
        return Status.PLAYER_A_MOVE;
      default:
        Log.e(TAG, "Status to other player conversion failed: not a player turn");
        return status;
    }
  }

  /**
   * Test win conditions
   */
  private boolean testWinConditions() {
    int boardSize = board.getSize();
    int maxIndex = boardSize - 1;
    Cell[] row = new Cell[boardSize];
    Cell[] col = new Cell[boardSize];
    Cell[] diag1 = new Cell[boardSize];
    Cell[] diag2 = new Cell[boardSize];

    for (int i = 0; i < boardSize; i++) {
      // Get row and column
      for (int j = 0; j < boardSize; j++) {
        row[j] = board.getState()[i * boardSize + j];
        col[j] = board.getState()[j * boardSize + i];
      }
      // Get diagonals
      diag1[i] = board.getState()[i * boardSize + i];
      diag2[i] = board.getState()[(maxIndex - i) * boardSize + i];
      // Test row and column
      if (allEqual(row) || allEqual(col)) {
        return true;
      }
    }
    // Test diagonals
    return allEqual(diag1) || allEqual(diag2);
  }

  /**
   * Test array for equality of all elements
   */
  private boolean allEqual(Cell[] cells) {
    // Null array
    if (cells == null) {
      Log.e(TAG, "Cells array reference is null");
      return false;
    }
    // Empty array
    if (cells.length == 0) {
      return false;
    }
    // Array of 1 element
    if (cells.length == 1) {
      return cells[0] == Cell.PLAYER_A || cells[0] == Cell.PLAYER_B;
    }
    // Array of 2+ elements
    if (cells[0] == Cell.PLAYER_A || cells[0] == Cell.PLAYER_B) {
      Cell cell0 = cells[0];
      for (int i = 1; i < cells.length; i++) {
        if (cells[i] != cell0) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Game status values
   */
  public enum Status {
    FINISHED, PLAYER_A_MOVE, PLAYER_B_MOVE
  }
}
