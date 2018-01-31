package com.example.inok.tictactoe.mcts;

import android.annotation.SuppressLint;
import android.util.Log;

public class Logger {

  private static final String TAG = "TAG_" + Logger.class.getSimpleName();

  public static void printDivider(String msg) {
    String div = "--------------------------------------";
    if (msg != null && msg.length() > 0) {
      div = msg + " " + div.substring(msg.length() + 1);
    }
    Log.d(TAG, div);
  }

  @SuppressLint("DefaultLocale")
  public static void printChildren(Node node) {
    if (!node.hasChildren()) {
      Log.d(TAG, "Node has no children");
      return;
    }
    Log.d(TAG, "    Move       N       W        UTC");
    StringBuilder sb;
    for (Node child : node.getChildren()) {
      sb = new StringBuilder();
      sb.append(String.format("%8d", child.getLastMove()));
      sb.append(String.format("%8d", child.getVisitCount()));
      sb.append(String.format("%8d", child.getWinScore()));
      sb.append(String.format("   %.6f", Uct.uctScore(child.getParent().getVisitCount(), child)));
      Log.d(TAG, sb.toString());
    }
  }
}
