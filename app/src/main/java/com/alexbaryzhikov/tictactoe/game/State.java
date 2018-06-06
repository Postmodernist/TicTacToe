package com.alexbaryzhikov.tictactoe.game;

import java.util.HashSet;
import java.util.Set;

public final class State {

  private final int[] board;
  private final int player;
  private final String id;
  private final Set<Integer> validActions;
  private final boolean finished;
  private final int value;

  private float[] canonicalBoard;

  State(int[] board, int player) {
    this.board = board;
    this.player = player;
    this.id = Game.makeId(board);
    this.validActions = Game.getInitialValidActions();
    this.finished = false;
    this.value = 0;
  }

  private State(int[] board, int player, Set<Integer> validActions, int action) {
    this.board = board;
    this.player = player;
    this.id = Game.makeId(board);
    this.validActions = Game.updateValidActions(validActions, board, action);
    boolean opponentWon = Game.isPlayerWon(board, -player, action);
    this.finished = validActions.isEmpty() || opponentWon;
    this.value = opponentWon ? -1 : 0;
  }

  public int[] getBoard() {
    return board;
  }

  public float[] getCanonicalBoard() {
    if (canonicalBoard == null) {
      canonicalBoard = new float[Game.board_size];
      for (int i = 0; i < board.length; i++) {
        canonicalBoard[i] = board[i] * player;
      }
    }
    return canonicalBoard;
  }

  public int getPlayer() {
    return player;
  }

  public String getId() {
    return id;
  }

  public Set<Integer> getValidActions() {
    return validActions;
  }

  public boolean isFinished() {
    return finished;
  }

  public int getValue() {
    return value;
  }

  public State getNextState(int action) {
    int[] new_board = board.clone();
    new_board[action] = player;
    return new State(new_board, -player, new HashSet<>(validActions), action);
  }
}
