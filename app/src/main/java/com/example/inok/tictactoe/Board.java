package com.example.inok.tictactoe;

/**
 * Board state
 */
public class Board {

  private int size;
  private Cell[] state;

  public Board(int size) {
    this.size = size;
    this.state = createState(size);
  }

  public int getSize() {
    return size;
  }

  public Cell[] getState() {
    return state;
  }

  public void setState(Cell[] state) {
    this.state = state;
  }

  /**
   * Get value of the cell at given position
   */
  public Cell getPosition(int position) {
    if (position >= 0 && position < state.length) {
      return state[position];
    }
    return Cell.INVALID;
  }

  /**
   * Set value of the cell at given position
   */
  public boolean setPosition(Cell player, int position) {
    if (position >= 0 && position < state.length && state[position] == Cell.EMPTY) {
      state[position] = player;
      return true;
    }
    return false;
  }

  /**
   * Check if board has at least one empty cell
   */
  public boolean hasEmptyCell() {
    for (Cell cell : state) {
      if (cell == Cell.EMPTY) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create a new board state of given size with empty cells
   */
  private Cell[] createState(int size) {
    Cell[] state = new Cell[size * size];
    for (int i = 0; i < state.length; i++) {
      state[i] = Cell.EMPTY;
    }
    return state;
  }

  /**
   * Cell values
   */
  enum Cell {
    INVALID, EMPTY, PLAYER_A, PLAYER_B
  }
}
