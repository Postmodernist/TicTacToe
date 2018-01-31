package com.example.inok.tictactoe.mcts;

import android.util.Log;

/**
 * Upper Confidence Tree score
 */

public class Uct {

  private static final String TAG = "TAG_" + Uct.class.getSimpleName();
  private static final double C = 1.41; // exploration bias factor

  public static Node getMaxUctNode(Node parent) {
    if (parent == null) {
      Log.e(TAG, "getMaxUctNode failed: parent is null");
      throw new NullPointerException();
    }
    if (parent.getChildren() == null) {
      Log.e(TAG, "getMaxUctNode failed: children is null");
      throw new NullPointerException();
    }
    if (parent.getChildren().size() == 0) {
      Log.e(TAG, "getMaxUctNode failed: children is empty, returning parent");
      return parent;
    }
    // Find child with max UTC score
    int parentVisitCount = parent.getVisitCount();
    Node bestChild = parent.getChildren().get(0);
    for (Node child : parent.getChildren()) {
      if (uctScore(parentVisitCount, child) > uctScore(parentVisitCount, bestChild)) {
        bestChild = child;
      }
    }
    return bestChild;
  }

  public static double uctScore(int parentVisitCount, Node node) {
    int nodeWinScore = node.getWinScore();
    int nodeVisitCount = node.getVisitCount();
    if (nodeVisitCount == 0) {
      return Double.MAX_VALUE;
    }
    double exploit = nodeWinScore / (double) nodeVisitCount;
    double explore = C * Math.sqrt(Math.log(parentVisitCount) / (double) nodeVisitCount);
    return exploit + explore;
  }
}
