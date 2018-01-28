package com.example.inok.tictactoe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class GameController {

  private static final String TAG = "TAG_" + GameController.class.getSimpleName();
  private static GameController instance = new GameController();
  private GameModel model;
  private WeakReference<GameView> view;
  private Bot bot;

  private GameController() {
    model = GameModel.getInstance();
    bot = new RandomAgent();
  }

  public static GameController getInstance() {
    return instance;
  }

  public void setView(GameView view) {
    this.view = new WeakReference<>(view);
  }

  public void freeView() {
    view.clear();
    view = null;
  }

  /**
   * Start a new game
   */
  public void startNewGame() {
    Log.d(TAG, "Starting a new game");
    checkViewReference();
    model.reset();
    view.get().onBoardSizeChanged();
    view.get().onGameStateUpdated();
    if (model.getStatus() == GameModel.Status.PLAYER_B_MOVE) {
      new BotAsyncTask(bot, model).execute();
    }
  }

  /**
   * Process player cell click
   */
  public void onPlayerCellClick(int position) {
    switch (model.getStatus()) {
      case PLAYER_A_MOVE:
        checkViewReference();
        if (!model.makeMove(position)) {
          Toast.makeText((Context) view.get(), R.string.invalid_move, Toast.LENGTH_SHORT).show();
        }
        view.get().onGameStateUpdated();
        if (model.getStatus() == GameModel.Status.PLAYER_B_MOVE) {
          new BotAsyncTask(bot, model).execute();
        }
        break;
      case PLAYER_B_MOVE:
        Toast.makeText((Context) view.get(), R.string.wait_turn, Toast.LENGTH_SHORT).show();
        break;
      case FINISHED:
        Toast.makeText((Context) view.get(), R.string.start_game, Toast.LENGTH_SHORT).show();
        break;
      default:
        Log.e(TAG, "Invalid game status: " + model.getStatus());
        break;
    }
  }

  /**
   * Process bot cell click
   */
  public void onBotCellClick(int position) {
    checkViewReference();
    if (!model.makeMove(position)) {
      Log.e(TAG, "Bot made invalid move: " + position);
    }
    view.get().onGameStateUpdated();
  }

  /**
   * Display the winner
   */
  public void displayWinner(GameModel.Status status) {
    checkViewReference();
    switch (status) {
      case PLAYER_A_MOVE:
        Toast.makeText((Context) view.get(), R.string.player_a_win, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Player wins");
        break;
      case PLAYER_B_MOVE:
        Toast.makeText((Context) view.get(), R.string.player_b_win, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Bot wins");
        break;
      case FINISHED:
        Toast.makeText((Context) view.get(), R.string.game_draw, Toast.LENGTH_SHORT).show();
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
