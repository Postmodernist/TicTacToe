package com.example.inok.tictactoe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class GameController {

  private static final String TAG = "TAG_" + GameController.class.getSimpleName();
  private static GameController instance = new GameController();
  private GameModel model;
  private GameView view;

  private GameController() {
    model = GameModel.getInstance();
  }

  public static GameController getInstance() {
    return instance;
  }

  public void setView(GameView view) {
    this.view = view;
  }

  public void freeView() {
    view = null;
  }

  /**
   * Start a new game
   */
  public void startNewGame() {
    checkViewReference();
    model.setBoard(new Board());
    model.setStatus(GameModel.Status.PLAYER_A_MOVE);
    view.onGameStateUpdated();
  }

  /**
   * Process player cell click
   */
  public void onCellClick(int position) {
    checkViewReference();
    if (!model.makeMove(position)) {
      Toast.makeText((Context) view, R.string.invalid_move, Toast.LENGTH_SHORT).show();
    }
    view.onGameStateUpdated();
  }

  /**
   * Display the winner
   */
  public void displayWinner(GameModel.Status status) {
    checkViewReference();
    switch (status) {
      case PLAYER_A_MOVE:
        Toast.makeText((Context) view, R.string.player_a_win, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Player A wins");
        break;
      case PLAYER_B_MOVE:
        Toast.makeText((Context) view, R.string.player_b_win, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Player B wins");
        break;
      case FINISHED:
        Toast.makeText((Context) view, R.string.game_draw, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Game draw");
        break;
      default:
        Log.e(TAG, "Invalid game status");
        break;
    }
  }

  /**
   * Assert view reference is not null
   */
  private void checkViewReference() {
    if (view == null) {
      Log.e(TAG, "View is null");
      throw new NullPointerException("Controller.view is null");
    }
  }
}
