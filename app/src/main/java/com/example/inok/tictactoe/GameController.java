package com.example.inok.tictactoe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class GameController {

  private static final String TAG = "TAG_" + GameController.class.getSimpleName();
  private static GameController instance = new GameController();
  private GameModel model;
  private GameView view;
  private Bot bot;
  private Object conch; // lock for players input sync

  private GameController() {
    model = GameModel.getInstance();
    bot = new RandomAgent();
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
    Log.d(TAG, "Starting a new game");
    checkViewReference();
    model.reset();
    view.onBoardSizeChanged();
    view.onGameStateUpdated();
    if (model.getStatus() == GameModel.Status.PLAYER_B_MOVE) {
      botMakeMove();
    }
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
    if (model.getStatus() == GameModel.Status.PLAYER_B_MOVE) {
      botMakeMove();
    }
  }

  /**
   * Display the winner
   */
  public void displayWinner(GameModel.Status status) {
    checkViewReference();
    switch (status) {
      case PLAYER_A_MOVE:
        Toast.makeText((Context) view, R.string.player_a_win, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Player wins");
        break;
      case PLAYER_B_MOVE:
        Toast.makeText((Context) view, R.string.player_b_win, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Bot wins");
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

  /**
   * Bot move
   */
  private void botMakeMove() {
    int botMove = bot.nextMove(model.getBoard());
    Log.d(TAG, "Bot move: " + botMove);
    onCellClick(botMove);
  }
}
