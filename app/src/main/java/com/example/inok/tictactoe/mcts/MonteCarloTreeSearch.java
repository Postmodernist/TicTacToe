package com.example.inok.tictactoe.mcts;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inok.tictactoe.Board;

public class MonteCarloTreeSearch {

  private static final String TAG = "TAG_" + MonteCarloTreeSearch.class.getSimpleName();
  private static final int TIME_LIMIT = 2000;
  private static final int WIN_SCORE = 1;
  private static final int LOSS_SCORE = -1;
  private Player opponent;
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
    // Set opponent
    opponent = player.getOpponent();
    // Setup tree
    if (root == null || (root = getChild(board)) == null) {
      // First time called or opponent made an unexplored move
      root = new Node(board, opponent);
    } else {
      // Root is made from child
      root.setParent(null);
    }
    // Explore tree
    Node promisingNode;
    Node nodeToExplore;
    Player winner;
    while (System.currentTimeMillis() < deadline) {
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
    // Select best move
    Node mostVisitedChild = root.getMostVisitedChild();
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
    if (root.hasChildren()) {
      Log.d(TAG, "Root has " + root.getChildren().size() + " children");
    }
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
    // Rule out nodes which lead to loss in one turn
    if (nodeToExplore.getBoard().hasWinCondition() && nodeToExplore.getPlayer() == opponent) {
      Node parent = nodeToExplore.getParent();
      if (parent != null) {
        parent.setWinScore(Integer.MIN_VALUE);
      }
      return opponent;
    }
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
    if (player == Player.NONE) {
      // Game draw, nothing changes
      return;
    }
    Node node = nodeToExplore;
    while (node != null) {
      node.incrementVisitCount();
      if (node.getWinScore() != Integer.MIN_VALUE) {
        if (node.getPlayer() == player) {
          node.addWinScore(WIN_SCORE);
        } else if (node.getPlayer() == player.getOpponent()) {
          node.addWinScore(LOSS_SCORE);
        } else {
          Log.e(TAG, "Encountered node with invalid player value");
        }
      }
      node = node.getParent();
    }
  }
}
