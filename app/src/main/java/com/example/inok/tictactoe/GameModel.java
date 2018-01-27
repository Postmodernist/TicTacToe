package com.example.inok.tictactoe;

import android.util.Log;

import com.example.inok.tictactoe.Board.Cell;

public class GameModel {

  public static final String TAG = "TAG_" + GameModel.class.getSimpleName();
  public static final int DEFAULT_BOARD_SIZE = 3;
  public static final int IN_A_ROW = 3;
  private static GameModel instance = new GameModel();
  private int boardSize;
  private Board board;
  private Status status;

  private GameModel() {
    boardSize = DEFAULT_BOARD_SIZE;
    status = Status.FINISHED;
  }

  public static GameModel getInstance() {
    return instance;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public void setBoardSize(int boardSize) {
    this.boardSize = boardSize;
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
   * Reset game model
   */
  public void reset() {
    setBoard(new Board(boardSize));
    // Pick a player who will make the first move
    if ((int) (Math.random() + 0.5f) == 0) {
      setStatus(Status.PLAYER_A_MOVE);
    } else {
      setStatus(Status.PLAYER_B_MOVE);
    }
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
   * Test win conditions: 3 in a row, column or diagonal
   */
  private boolean testWinConditions() {
    int boardSize = board.getSize();
    int maxIndex = boardSize - 1;
    Cell[] row = new Cell[boardSize];
    Cell[] col = new Cell[boardSize];
    Cell[] diagonal1;
    Cell[] diagonal2;
    Cell[] diagonal3;
    Cell[] diagonal4;

    for (int i = 0; i < boardSize; i++) {
      // Test row and column
      for (int j = 0; j < boardSize; j++) {
        row[j] = board.getState()[i * boardSize + j];
        col[j] = board.getState()[j * boardSize + i];
      }
      if (winCondition(row) || winCondition(col)) {
        return true;
      }
      // Test diagonals
      if (boardSize - i >= IN_A_ROW) {
        diagonal1 = new Cell[boardSize - i];
        diagonal2 = new Cell[boardSize - i];
        diagonal3 = new Cell[boardSize - i];
        diagonal4 = new Cell[boardSize - i];
        for (int j = 0; j < boardSize - i; j++) {
          diagonal1[j] = board.getState()[i + j + j * boardSize];
          diagonal2[j] = board.getState()[j + (i + j) * boardSize];
          diagonal3[j] = board.getState()[j + (maxIndex - i - j) * boardSize];
          diagonal4[j] = board.getState()[i + j + (maxIndex - j) * boardSize];
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
   * Test array for equality of all elements
   */
  private boolean winCondition(Cell[] cells) {
    if (cells == null) {
      Log.e(TAG, "Cells array reference is null");
      return false;
    }
    int matchCount = 0;
    Cell lastCell = null;
    for (Cell cell : cells) {
      if (cell == Cell.PLAYER_A || cell == Cell.PLAYER_B) {
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

  /**
   * Game status values
   */
  public enum Status {
    FINISHED, PLAYER_A_MOVE, PLAYER_B_MOVE
  }
}
