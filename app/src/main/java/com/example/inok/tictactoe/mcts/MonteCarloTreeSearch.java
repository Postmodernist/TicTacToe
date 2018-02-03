package com.example.inok.tictactoe.mcts;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.inok.tictactoe.Board;
import com.example.inok.tictactoe.BotAsyncTask;
import com.example.inok.tictactoe.GameController;

import java.util.Random;
import java.util.Set;

public class MonteCarloTreeSearch {

  private static final String TAG = "TAG_" + MonteCarloTreeSearch.class.getSimpleName();
  private static final int TIME_LIMIT = 10000;
  private static final int ITERATION_LIMIT = 50000;
  private static final int ITERATION_MIN = 5000;
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
    int iterations = 0;
    int progressIterations;
    int progressTime;
    while (true) {
      // Loop exit conditions and progress update
      if (botAsyncTask.isCancelled()) {
        return Node.INVALID_MOVE;
      } else if (iterations++ < ITERATION_MIN) {
        progressIterations = (int) (iterations * 100 / (double) ITERATION_MIN + 0.5);
        GameController.getInstance().onProgressUpdate(progressIterations);
      } else if (iterations < ITERATION_LIMIT && System.currentTimeMillis() < deadline) {
        progressIterations = (int) (iterations * 100 / (double) ITERATION_LIMIT + 0.5);
        progressTime = 100 - (int) ((deadline - System.currentTimeMillis()) * 100 / TIME_LIMIT);
        if (progressIterations < progressTime) {
          GameController.getInstance().onProgressUpdate(progressTime);
        } else {
          GameController.getInstance().onProgressUpdate(progressIterations);
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
      nodeToExplore = promisingNode.hasChildren() ? promisingNode.getRandomChild() : promisingNode;
      winner = randomRollOut(nodeToExplore);
      // Update
      backPropagation(nodeToExplore, winner);
    }
    Logger.printChildren(root);
    Logger.printDivider("Iterations: " + iterations);
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
    return !board.hasWinCondition() && board.hasValidMove();
  }

  /**
   * Simulate random play and return a winner or Player.NONE in case of game draw
   */
  private Player randomRollOut(Node nodeToExplore) {
    Board board = new Board(nodeToExplore.getBoard());
    Player[] cells = board.getCells();
    Player player = nodeToExplore.getPlayer();
    Set<Integer> validMoves = new android.support.v4.util.ArraySet<>(board.getValidMoves());
    Random rand = new Random();
    int lastMove = nodeToExplore.getLastMove();
    // Random descent until hit leaf node
    while (!board.hasWinCondition(lastMove)) {
      validMoves.addAll(board.getEmptyNeighbors(lastMove));
      if (validMoves.size() > 0) {
        // Switch player
        player = player.getOpponent();
        // Make random move
        lastMove = (int) validMoves.toArray()[rand.nextInt(validMoves.size())];
        cells[lastMove] = player;
      } else {
        // No more possible moves and no winner
        return Player.NONE;
      }
    }
    return player;
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
