package com.example.inok.tictactoe.game;

public class State {

  private int[] board;
  private int player;
  private int turn;
  private String id;
  private int[] validActions;
  private boolean finished;
  private int value;

  public State(int[] board, int player, int turn) {
    this.board = board;
    this.player = player;
    this.turn = turn;
    this.id = Game.makeId(board);
    this.validActions = Game.findValidActions(board);
    boolean opponentWon = Game.isPlayerWon(board, -player);
    this.finished = validActions.length == 0 || opponentWon;
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

  public int[] getValidActions() {
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
    return new State(new_board, -player, turn + 1);
  }
}
