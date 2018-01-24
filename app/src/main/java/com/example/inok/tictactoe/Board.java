package com.example.inok.tictactoe;

/**
 * Board state
 */
public class Board {

  public static final int CELL_EMPTY = 0;
  public static final int CELL_P1 = 1;
  public static final int CELL_P2 = 2;

  private int[] state;

  public Board() {
    state = getDefaultState();
  }

  public Board(int[] state) {
    this.state = state;
  }

  public int[] getState() {
    return state;
  }

  public void setState(int[] state) {
    this.state = state;
  }

  private int[] getDefaultState() {
    int[] defaultState = new int[GameModel.DEFAULT_BOARD_SIZE * GameModel.DEFAULT_BOARD_SIZE];
    for (int i = 0; i < defaultState.length; i++) {
      defaultState[i] = CELL_EMPTY;
    }
    return defaultState;
  }
}
