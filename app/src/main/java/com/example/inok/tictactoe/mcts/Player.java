package com.example.inok.tictactoe.mcts;

/**
 * Players
 */

public enum Player {
  NONE,
  PLAYER_A,
  PLAYER_B;

  public Player getOpponent() {
    switch (this) {
      case PLAYER_A:
        return PLAYER_B;
      case PLAYER_B:
        return PLAYER_A;
      default:
        return this;
    }
  }
}
