package com.example.inok.tictactoe.mcts;

import android.annotation.SuppressLint;
import android.util.Log;

public class Logger {

  private static final String TAG = "TAG_" + Logger.class.getSimpleName();

  public static void printDivider(String msg) {
    String div = "------------------------------------------------";
    if (msg != null && msg.length() > 0) {
      div = msg + " " + div.substring(msg.length() + 1);
    }
    Log.d(TAG, div);
  }

  @SuppressLint("DefaultLocale")
  public static void printChildren(Node node) {
    if (node.edges.isEmpty()) {
      Log.d(TAG, "Node has no children");
      return;
    }
    int nodeVisits = 0;
    for (Edge edge : node.edges) {
      nodeVisits += edge.N;
    }
    Log.d(TAG, "  Action       N       W           Q           U");
    StringBuilder sb;
    for (Edge edge : node.edges) {
      double u = edge.Q + Mcts.C_UCT * Math.sqrt(Math.log(nodeVisits) / edge.N);
      sb = new StringBuilder();
      sb.append(String.format("%8d", edge.action));
      sb.append(String.format("%8d", edge.N));
      sb.append(String.format("%8d", edge.W));
      sb.append(String.format("%12.6f", edge.Q));
      sb.append(String.format("%12.6f", u));
      Log.d(TAG, sb.toString());
    }
  }
}
