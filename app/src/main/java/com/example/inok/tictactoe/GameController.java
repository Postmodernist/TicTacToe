package com.example.inok.tictactoe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.inok.tictactoe.agents.Agent;
import com.example.inok.tictactoe.agents.MctsPAgent;
import com.example.inok.tictactoe.game.Game;

import java.lang.ref.WeakReference;

public class GameController {

  private static final String TAG = "GameController";
  private static WeakReference<GameView> view;
  private static Agent agent;
  private static AgentAsyncTask agentAsyncTask;
  private static String difficulty = "hard";

  public static AgentAsyncTask getAgentAsyncTask() {
    return agentAsyncTask;
  }

  public static void registerView(GameView view) {
    GameController.view = new WeakReference<>(view);
  }

  public static void unregisterView() {
    view.clear();
    view = null;
  }

  /**
   * Start a new game
   */
  public static void startNewGame() {
    if (view.get() == null) {
      throw new RuntimeException("GameView reference is empty");
    }
    Log.d(TAG, "Starting a new game");
    // Create agent
    if (agent == null) {
      agent = new MctsPAgent(view.get().getAssets());
    }
    // Cancel current agent task if any
    if (agentAsyncTask != null) {
      agentAsyncTask.cancel(false);
      endAgentTask();
    }
    Game.restart();
    view.get().onGameStateUpdated();
    if (Game.state.getPlayer() == -1) {
      runAgentTask();
    }
  }

  /**
   * Set AI difficulty
   */
  public static void setDifficulty(String difficulty) {
    GameController.difficulty = difficulty;
  }

  /**
   * Get MCTS simulations number based on difficulty
   */
  public static int getSimulations() {
    switch (difficulty) {
      case "easy":
        return 300;
      case "medium":
        return 600;
      default:
        return 1000;
    }
  }

  /**
   * Process player click
   */
  public static void onPlayerClick(int position) {
    if (Game.state.isFinished()) {  // game is over
      Toast.makeText((Context) view.get(), R.string.start_game, Toast.LENGTH_SHORT).show();
    } else if (Game.state.getPlayer() == 1) {  // player's turn
      if (Game.isValidAction(position)) {
        Log.d(TAG, "Player move: " + position);
        // Make a move
        Game.state = Game.state.getNextState(position);
        view.get().onGameStateUpdated();
        if (Game.state.isFinished()) {
          int winner = Game.state.getValue() == -1 ? 1 : 0;
          displayWinner(winner);
        } else {
          runAgentTask();
        }
      } else {
        Toast.makeText((Context) view.get(), R.string.invalid_move, Toast.LENGTH_SHORT).show();
      }
    } else {  // agent's turn
      Toast.makeText((Context) view.get(), R.string.wait_turn, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Process agent click
   */
  public static void onAgentClick(int position) {
    endAgentTask();
    if (Game.isValidAction(position)) {
      Log.d(TAG, "Agent move: " + position);
      // Make a move
      Game.state = Game.state.getNextState(position);
      view.get().onGameStateUpdated();
      if (Game.state.isFinished()) {
        int winner = Game.state.getValue() == -1 ? -1 : 0;
        displayWinner(winner);
      }
    } else {
      Log.e(TAG, "Invalid agent move: " + position);
    }
  }

  /**
   * Update agent progress bar
   */
  public static void onProgressUpdate(int progress) {
    if (view != null && view.get() != null) {
      view.get().setProgressPercent(progress);
    }
  }

  /**
   * Display the winner
   */
  private static void displayWinner(int winner) {
    switch (winner) {
      case 1:
        Toast.makeText((Context) view.get(), R.string.player_a_win, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Player wins");
        break;
      case -1:
        Toast.makeText((Context) view.get(), R.string.player_b_win, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Agent wins");
        break;
      case 0:
        Toast.makeText((Context) view.get(), R.string.game_draw, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Game draw");
        break;
      default:
        Log.e(TAG, "Unknown player");
        break;
    }
  }

  /**
   * Run agent task
   */
  private static void runAgentTask() {
    view.get().showProgressBar(true);
    agentAsyncTask = new AgentAsyncTask(agent);
    agentAsyncTask.execute();
  }

  /**
   * End agent task
   */
  private static void endAgentTask() {
    view.get().showProgressBar(false);
    agentAsyncTask = null;
  }
}
