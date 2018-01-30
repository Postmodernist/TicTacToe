package com.example.inok.tictactoe.mcts;

import android.util.Log;

import com.example.inok.tictactoe.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node {

  public static final int INVALID_MOVE = -1;
  private static final String TAG = "TAG_" + Node.class.getSimpleName();
  private Node parent;
  private List<Node> children = new ArrayList<>();
  private Board board;      // game board with state that this node represents
  private Player player;    // player that has to make a turn
  private int lastMove;     // move between parent and this node
  private int visitCount;   // number of plays that have been run at or below this node
  private int winScore;     // accumulated win/loss value
  private Random rnd = new Random();

  private Node() {
    // This constructor is not allowed
  }

  public Node(Board board, Player player) {
    this.board = new Board(board);
    this.player = player;
    lastMove = INVALID_MOVE;
    visitCount = 0;
    winScore = 0;
  }

  public Node(Node node) {
    parent = node.getParent();
    for (Node child : node.getChildren()) {
      children.add(new Node(child));
    }
    board = new Board(node.getBoard());
    player = node.getPlayer();
    lastMove = node.getLastMove();
    visitCount = node.getVisitCount();
    winScore = node.getWinScore();
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public List<Node> getChildren() {
    return children;
  }

  public boolean hasChildren() {
    return children.size() > 0;
  }

  public Board getBoard() {
    return board;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public int getLastMove() {
    return lastMove;
  }

  public void setLastMove(int lastMove) {
    this.lastMove = lastMove;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public void incrementVisitCount() {
    visitCount++;
  }

  public int getWinScore() {
    return winScore;
  }

  public void setWinScore(int winScore) {
    this.winScore = winScore;
  }

  public void addWinScore(int score) {
    if (winScore != Integer.MIN_VALUE) {
      winScore += score;
    }
  }

  /**
   * Get random child
   */
  public Node getRandomChild() {
    if (!hasChildren()) {
      Log.e(TAG, "getRandomChild failed: this node has no children");
      return null;
    }
    int index = rnd.nextInt(children.size());
    return children.get(index);
  }

  /**
   * Get the child with the most visit count
   */
  public Node getMostVisitedChild() {
    if (!hasChildren()) {
      Log.e(TAG, "getMostVisitedChild failed: this node has no children");
      return null;
    }
    Node bestChild = children.get(0);
    for (Node child : children) {
      if (child.getVisitCount() > bestChild.getVisitCount()) {
        bestChild = child;
      }
    }
    return bestChild;
  }

  /**
   * Expand this node
   */
  public void expand() {
    if (children.size() > 0) {
      Log.e(TAG, "Node has already been expanded");
      return;
    }
    for (int position : board.getEmptyPositions()) {
      Node node = new Node(board, player.getOpponent());
      if (!node.getBoard().set(player.getOpponent(), position)) {
        Log.e(TAG, "Invalid move during node expansion: " + position);
      }
      node.setParent(this);
      node.setLastMove(position);
      children.add(node);
    }
  }

  /**
   * Make random move
   */
  public boolean makeRandomMove() {
    List<Integer> emptyPositions = board.getEmptyPositions();
    if (emptyPositions.size() > 0) {
      // Switch player
      player = player.getOpponent();
      // Make random move
      int index = rnd.nextInt(emptyPositions.size());
      board.set(player, emptyPositions.get(index));
      return true;
    }
    return false;
  }
}
