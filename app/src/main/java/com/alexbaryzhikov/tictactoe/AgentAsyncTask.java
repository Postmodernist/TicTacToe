package com.alexbaryzhikov.tictactoe;

import android.os.AsyncTask;
import android.util.Log;

import com.alexbaryzhikov.tictactoe.agents.Agent;

public class AgentAsyncTask extends AsyncTask<Void, Void, Integer> {

  private Agent agent;

  AgentAsyncTask(Agent agent) {
    this.agent = agent;
  }

  @Override
  protected Integer doInBackground(Void... voids) {
    return agent.getAction();
  }

  @Override
  protected void onPostExecute(Integer position) {
    GameController.onAgentClick(position);
  }
}
