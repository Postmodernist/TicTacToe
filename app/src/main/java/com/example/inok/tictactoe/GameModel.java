package com.example.inok.tictactoe;

/**
 * Model contains game state and behavior
 */
public class GameModel {

  public static final int DEFAULT_BOARD_SIZE = 3;
  public static final int STATUS_FINISHED = 0;
  public static final int STATUS_PLAYING = 1;
  private static GameModel instance = new GameModel();

  private Board board;
  private int status;

  private GameModel() {
    status = STATUS_FINISHED;
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

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void onCellClick(int position) {
    board.getState()[position] = Board.CELL_P1;
  }
}
