package com.example.inok.tictactoe.mcts;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inok.tictactoe.Board;
import com.example.inok.tictactoe.BotAsyncTask;
import com.example.inok.tictactoe.GameController;

public class MonteCarloTreeSearch {

  private static final String TAG = "TAG_" + MonteCarloTreeSearch.class.getSimpleName();
  private static final int TIME_LIMIT = 10000;
  private static final int ITER_LIMIT = 50000;
  private static final int ITER_MIN = 5000;
  private static final int WIN_SCORE = 1;
  private static final int LOSS_SCORE = -1;
  private Node root;

  /**
   * Find the best move
   */
  public int getNextMove(Board board, Player player) {
    // Set deadline
    long deadline = System.currentTimeMillis() + TIME_LIMIT;
    // Validate board
    if (board == null) {
      Log.e(TAG, "Board is null");
      throw new NullPointerException();
    }
    // Setup tree
    if (root == null || (root = getChild(board)) == null) {
      // First time called or opponent made an unexplored move
      root = new Node(board, player.getOpponent());
    } else {
      // Root is made from child
      root.setParent(null);
    }
    // Explore tree
    Logger.printDivider("Explore tree");
    Node promisingNode;
    Node nodeToExplore;
    Player winner;
    BotAsyncTask botAsyncTask = GameController.getInstance().getBotAsyncTask();
    int iters = 0;
    int progressIter;
    int progressTime;
    while (true) {
      // Loop exit conditions and progress update
      if (botAsyncTask.isCancelled()) {
        return Node.INVALID_MOVE;
      } else if (iters++ < ITER_MIN) {
        progressIter = (int) (iters * 100 / (double) ITER_MIN + 0.5);
        GameController.getInstance().onProgressUpdate(progressIter);
      } else if (iters < ITER_LIMIT && System.currentTimeMillis() < deadline) {
        progressIter = (int) (iters * 100 / (double) ITER_LIMIT + 0.5);
        progressTime = 100 - (int) ((deadline - System.currentTimeMillis()) * 100 / TIME_LIMIT);
        if (progressIter < progressTime) {
          GameController.getInstance().onProgressUpdate(progressTime);
        } else {
          GameController.getInstance().onProgressUpdate(progressIter);
        }
      } else {
        break;
      }
      // Selection
      promisingNode = getPromisingNode();
      // Expansion
      if (isExpandable(promisingNode)) {
        promisingNode.expand();
      }
      // Simulation
      if (promisingNode.hasChildren()) {
        nodeToExplore = promisingNode.getRandomChild();
      } else {
        nodeToExplore = promisingNode;
      }
      winner = randomRollOut(nodeToExplore);
      // Update
      backPropagation(nodeToExplore, winner);
    }
    Logger.printChildren(root);
    // Select best move
    Node mostVisitedChild = root.getMostVisitedChild();
    Logger.printDivider(null);
    if (mostVisitedChild == null) {
      // No moves available
      return Node.INVALID_MOVE;
    }
    // Keep the selected subtree and discard everything else
    root = mostVisitedChild;
    root.setParent(null);
    return root.getLastMove();
  }

  /**
   * Find child with the same board
   */
  @Nullable
  private Node getChild(Board board) {
    for (Node child : root.getChildren()) {
      if (child.getBoard().isEqual(board)) {
        return child;
      }
    }
    Log.d(TAG, "Subtree not found");
    return null;
  }

  /**
   * Select child node with the best UCT score
   */
  private Node getPromisingNode() {
    Node node = root;
    while (node.hasChildren()) {
      node = Uct.getMaxUctNode(node);
    }
    return node;
  }

  /**
   * Check if node can be played further
   */
  private boolean isExpandable(Node node) {
    Board board = node.getBoard();
    return !board.hasWinCondition() && board.hasEmptyCell();
  }

  /**
   * Simulate random play and return a winner or Player.NONE in case of game draw
   */
  private Player randomRollOut(Node nodeToExplore) {
    // Random descent until hit leaf node
    Node node = new Node(nodeToExplore.getBoard(), nodeToExplore.getPlayer());
    while (!node.getBoard().hasWinCondition()) {
      if (!node.makeRandomMove()) {
        // No more possible moves and no winner
        return Player.NONE;
      }
    }
    return node.getPlayer();
  }

  /**
   * Propagate win score and visit count up the tree
   */
  private void backPropagation(Node nodeToExplore, Player player) {
    for (Node node = nodeToExplore; node != null; node = node.getParent()) {
      node.incrementVisitCount();
      if (node.getPlayer() == player) {
        node.addWinScore(WIN_SCORE);
      } else if (node.getPlayer() == player.getOpponent()) {
        node.addWinScore(LOSS_SCORE);
      }
    }
  }
}
