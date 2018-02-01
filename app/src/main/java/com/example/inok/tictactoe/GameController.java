package com.example.inok.tictactoe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.inok.tictactoe.mcts.Player;

import java.lang.ref.WeakReference;

public class GameController {

  private static final String TAG = "TAG_" + GameController.class.getSimpleName();
  private static final GameController instance = new GameController();
  private static final GameModel model = GameModel.getInstance();
  private WeakReference<GameView> view;
  private Bot bot = new MctsAgent();

  private GameController() {
    // Constructor use is not allowed
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
    model.restart();
    view.get().onBoardSizeChanged();
    view.get().onGameStateUpdated();
    if (model.getPlayer() == Player.PLAYER_B) {
      view.get().showProgressBar(true);
      new BotAsyncTask(bot, model).execute();
    }
  }

  /**
   * Process player click
   */
  public void onPlayerClick(int position) {
    switch (model.getPlayer()) {
      case PLAYER_A:
        if (model.makeMove(position)) {
          view.get().onGameStateUpdated();
          if (model.getPlayer() == Player.PLAYER_B) {
            // Pass turn to bot
            view.get().showProgressBar(true);
            new BotAsyncTask(bot, model).execute();
          }
        } else {
          Toast.makeText((Context) view.get(), R.string.invalid_move, Toast.LENGTH_SHORT).show();
        }
        break;
      case PLAYER_B:
        Toast.makeText((Context) view.get(), R.string.wait_turn, Toast.LENGTH_SHORT).show();
        break;
      case NONE:
        Toast.makeText((Context) view.get(), R.string.start_game, Toast.LENGTH_SHORT).show();
        break;
      default:
        Log.e(TAG, "Invalid turn status: " + model.getPlayer());
        break;
    }
  }

  /**
   * Process bot click
   */
  public void onBotClick(int position) {
    view.get().showProgressBar(false);
    if (model.makeMove(position)) {
      view.get().onGameStateUpdated();
    } else {
      Log.e(TAG, "Invalid bot move: " + position);
    }
  }

  /**
   * Display the winner
   */
  public void displayWinner(Player player) {
    switch (player) {
      case PLAYER_A:
        Toast.makeText((Context) view.get(), R.string.player_a_win, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Player wins");
        break;
      case PLAYER_B:
        Toast.makeText((Context) view.get(), R.string.player_b_win, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Bot wins");
        break;
      case NONE:
        Toast.makeText((Context) view.get(), R.string.game_draw, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Game draw");
        break;
      default:
        Log.e(TAG, "Unknown player");
        break;
    }
  }

  /** Update bot progress bar*/
  public void onProgressUpdate(int progress) {
    view.get().setProgressPercent(progress);
  }
}
