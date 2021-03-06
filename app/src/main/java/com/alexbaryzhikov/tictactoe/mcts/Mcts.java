package com.alexbaryzhikov.tictactoe.mcts;

import android.annotation.SuppressLint;
import android.util.Log;

import com.alexbaryzhikov.tictactoe.AgentAsyncTask;
import com.alexbaryzhikov.tictactoe.GameController;
import com.alexbaryzhikov.tictactoe.game.Game;
import com.alexbaryzhikov.tictactoe.game.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Monte Carlo tree search
 */

public class Mcts {

  private static final String TAG = "MCTS";

  private static final double C_UCT = 1.41;
  private static final int TIME_LIMIT = 5000;

  private Map<String, Node> tree = new HashMap<>();
  private Node root;

  /**
   * Perform MCTS simulations starting from current game state.
   * Return a vector of MCTS score over all actions.
   */
  public int[] getDistribution(State state) {
    // Set root node
    if (tree.containsKey(state.getId())) {
      root = tree.get(state.getId());
      pruneTree();
    } else {
      createTree(state);
    }
    // Explore the tree
    Logger.printDivider("Tree exploration");
    AgentAsyncTask agentAsyncTask = GameController.getAgentAsyncTask();
    long deadline = System.currentTimeMillis() + TIME_LIMIT;
    int progressTime;
    int iter = 0;
    while (System.currentTimeMillis() < deadline) {
      if (agentAsyncTask.isCancelled()) {  // task is aborted, bail out
        return new int[0];
      }
      progressTime = 100 - (int) ((deadline - System.currentTimeMillis()) * 100 / TIME_LIMIT);
      GameController.onProgressUpdate(progressTime);
      simulate();
      iter++;
    }
    // Return visit counts
    int[] visitCounts = new int[Game.board_size];
    for (Edge edge : root.edges) {
      visitCounts[edge.action] = edge.N;
    }
    Logger.printChildren(root);
    Logger.printDivider("Iterations: " + iter);
    return visitCounts;
  }

  /**
   * Move to leaf node, evaluate it, and back propagate the value
   */
  private void simulate() {
    List<Edge> breadcrumbs = new ArrayList<>();
    Node leaf = moveToLeaf(breadcrumbs);
    int value;
    if (leaf.state.isFinished()) {
      value = leaf.state.getValue();
    } else {
      value = rollout(leaf.state);
      expandNode(leaf);
    }
    backPropagate(leaf, value, breadcrumbs);
  }

  /**
   * Move down the tree until hit a leaf node
   */
  private Node moveToLeaf(List<Edge> breadcrumbs) {
    Node node = root;
    while (!node.edges.isEmpty()) {
      Edge bestEdge = getBestEdge(node);
      node = bestEdge.outNode;
      breadcrumbs.add(bestEdge);
    }
    return node;
  }

  /**
   * Return a result of a random rollout
   */
  private int rollout(State state) {
    int player = state.getPlayer();
    int action;
    Random random = new Random();
    List<Integer> validActions;
    while (!state.isFinished()) {
      validActions = new ArrayList<>(state.getValidActions());
      action = validActions.get(random.nextInt(validActions.size()));
      state = state.getNextState(action);
    }
    return state.getPlayer() == player ? state.getValue() : -state.getValue();
  }

  /**
   * Expand node
   */
  private void expandNode(Node node) {
    State newState;
    Node newNode;
    for (int action : node.state.getValidActions()) {
      newState = node.state.getNextState(action);
      if (tree.containsKey(newState.getId())) {
        newNode = tree.get(newState.getId());
      } else {
        newNode = new Node(newState);
        tree.put(newState.getId(), newNode);
      }
      node.edges.add(new Edge(node, newNode, action));
    }
  }

  /**
   * Back propagate the value up the tree
   */
  private void backPropagate(Node node, int value, List<Edge> breadcrumbs) {
    int player = node.state.getPlayer();
    for (Edge edge : breadcrumbs) {
      edge.N++;
      edge.W += edge.player == player ? value : -value;
      edge.Q = (double) edge.W / edge.N;
    }
  }

  /**
   * Pick edge with highest upper confidence bound
   */
  private Edge getBestEdge(Node node) {
    int nodeVisits = 0;
    for (Edge edge : node.edges) {
      nodeVisits += edge.N;
    }
    double max_u = -Double.MAX_VALUE;
    double u;
    Edge best_edge = null;
    for (Edge edge : node.edges) {
      if (edge.N == 0) {
        return edge;
      }
      u = edge.Q + C_UCT * Math.sqrt(Math.log(nodeVisits) / edge.N);
      if (u > max_u) {
        max_u = u;
        best_edge = edge;
      }
    }
    return best_edge;
  }

  /**
   * Create a new tree
   */
  private void createTree(State state) {
    tree = new HashMap<>();
    root = new Node(state);
    tree.put(state.getId(), root);
  }

  /**
   * Keep only subtree of the node and prune the rest
   */
  private void pruneTree() {
    Map<String, Node> subtree = new HashMap<>();
    subtree.put(root.state.getId(), root);
    copySubtree(subtree, root);
    tree = subtree;
  }

  private void copySubtree(Map<String, Node> subtree, Node node) {
    for (Edge edge : node.edges) {
      subtree.put(edge.outNode.state.getId(), edge.outNode);
      copySubtree(subtree, edge.outNode);
    }
  }

  private static final class Node {

    final State state;
    List<Edge> edges = new ArrayList<>();

    Node(State state) {
      this.state = state;
    }
  }

  private static final class Edge {

    final Node outNode;
    final int action;
    final int player;

    int N = 0;
    int W = 0;
    double Q = 0;

    Edge(Node inNode, Node outNode, int action) {
      this.outNode = outNode;
      this.action = action;
      this.player = inNode.state.getPlayer();
    }
  }

  private static class Logger {

    static void printDivider(String msg) {
      String div = "------------------------------------------------";
      if (msg != null && msg.length() > 0) {
        div = msg + " " + div.substring(msg.length() + 1);
      }
      Log.d(TAG, div);
    }

    @SuppressLint("DefaultLocale")
    static void printChildren(Node node) {
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
}
