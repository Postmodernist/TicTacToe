package com.example.inok.tictactoe;

import android.util.Log;

import com.example.inok.tictactoe.mcts.Player;

public class GameModel {

  public static final String TAG = "TAG_" + GameModel.class.getSimpleName();
  private static final GameModel instance = new GameModel();
  private int boardSize = 3;
  private Board board;
  private Player player = Player.NONE;

  private GameModel() {
    // Constructor use is not allowed
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

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
    Log.d(TAG, "Current player: " + player);
  }

  /**
   * Restart the game
   */
  public void restart() {
    setBoard(new Board(boardSize));
    // Pick a player who will make the first move
    if ((int) (Math.random() + 0.5f) == 0) {
      setPlayer(Player.PLAYER_A);
    } else {
      setPlayer(Player.PLAYER_B);
    }
  }

  /**
   * Make a move. Update current player according to the outcome
   */
  public boolean makeMove(int position) {
    boolean result = false;
    if (player == Player.PLAYER_A || player == Player.PLAYER_B) {
      if (result = board.set(player, position)) {
        if (board.hasWinCondition()) {
          // Current player wins
          GameController.getInstance().displayWinner(player);
          setPlayer(Player.NONE);
        } else if (board.hasEmptyCell()) {
          // Game continues
          setPlayer(player.getOpponent());
        } else {
          // No more free cells and no winner -- game draw
          GameController.getInstance().displayWinner(Player.NONE);
          setPlayer(Player.NONE);
        }
      } else {
        Log.e(TAG, "Invalid move: " + player + ", position " + position);
      }
    }
    return result;
  }
}
