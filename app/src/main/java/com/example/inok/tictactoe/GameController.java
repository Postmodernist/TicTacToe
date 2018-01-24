package com.example.inok.tictactoe;

import android.util.Log;

public class GameController {

  private static final String TAG = GameController.class.getSimpleName();
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

  public void startNewGame() {
    checkViewReference();
    model.setBoard(new Board());
    model.setStatus(GameModel.STATUS_PLAYING);
    view.onGameStateUpdated();
  }

  public void onCellClick(int position) {
    checkViewReference();
    model.onCellClick(position);
    view.onGameStateUpdated();
  }

  /** Assert view reference is not null */
  private void checkViewReference() {
    if (view == null) {
      Log.e(TAG, "View is null");
      throw new NullPointerException("Controller.view is null");
    }
  }
}
