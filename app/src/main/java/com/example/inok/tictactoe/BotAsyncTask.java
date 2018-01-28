package com.example.inok.tictactoe;

import android.os.AsyncTask;
import android.util.Log;

public class BotAsyncTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "TAG_Bot";
  private Bot bot;
  private GameModel model;

  public BotAsyncTask(Bot bot, GameModel model) {
    this.bot = bot;
    this.model = model;
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Log.e(TAG, "InterruptedException: " + e.getMessage());
    }
    return bot.nextMove(model.getBoard());
  }

  @Override
  protected void onPostExecute(Integer position) {
    Log.d(TAG, "Bot move: " + position);
    GameController.getInstance().onBotCellClick(position);
  }
}
