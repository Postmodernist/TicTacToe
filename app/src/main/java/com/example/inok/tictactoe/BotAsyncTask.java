package com.example.inok.tictactoe;

import android.os.AsyncTask;
import android.util.Log;

public class BotAsyncTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "TAG_" + BotAsyncTask.class.getSimpleName();
  private Bot bot;
  private GameModel model;

  public BotAsyncTask(Bot bot, GameModel model) {
    this.bot = bot;
    this.model = model;
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    return bot.nextMove(model.getBoard(), model.getPlayer());
  }

  @Override
  protected void onPostExecute(Integer position) {
    Log.d(TAG, "Bot move: " + position);
    GameController.getInstance().onBotClick(position);
  }

  @Override
  protected void onCancelled() {
    Log.d(TAG, "Bot task is cancelled");
  }
}
