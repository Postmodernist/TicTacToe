package com.example.inok.tictactoe.game;

import java.util.HashSet;
import java.util.Set;

public class State {

  private int[] board;
  private int player;
  private int turn;
  private String id;
  private Set<Integer> validActions;
  private boolean finished;
  private int value;

  public State(int[] board, int player) {
    this.board = board;
    this.player = player;
    this.turn = 0;
    this.id = Game.makeId(board);
    this.validActions = Game.getInitialValidActions();
    this.finished = false;
    this.value = 0;
  }

  public State(int[] board, int player, int turn, Set<Integer> validActions, int action) {
    this.board = board;
    this.player = player;
    this.turn = turn;
    this.id = Game.makeId(board);
    this.validActions = Game.updateValidActions(validActions, board, action);
    boolean opponentWon = Game.isPlayerWon(board, -player, action);
    this.finished = validActions.isEmpty() || opponentWon;
    this.value = opponentWon ? -1 : 0;
  }

  public int[] getBoard() {
    return board;
  }

  public int getPlayer() {
    return player;
  }

  public int getTurn() {
    return turn;
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
    return new State(new_board, -player, turn + 1, new HashSet<>(validActions), action);
  }
}
