package com.example.inok.tictactoe.mcts;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.example.inok.tictactoe.AgentAsyncTask;
import com.example.inok.tictactoe.GameController;
import com.example.inok.tictactoe.game.Game;
import com.example.inok.tictactoe.game.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Probabilistic Monte Carlo tree search
 */

public class MctsP {

  private static final double C_PUCT = 1.41;

  private Map<String, Node> tree = new HashMap<>();
  private Node root;
  private Evaluator evaluator;

  public MctsP(@NonNull AssetManager assetManager) {
    evaluator = Evaluator.create(assetManager);
  }

  /**
   * Perform MCTS simulations starting from current game state.
   * Return a vector of MCTS score over all actions.
   *
   * @param state Root game state
   * @return Actions visit counts
   */
  public int[] getDistribution(State state) {
    // Set root node
    if (tree.containsKey(state.getId())) {
      root = tree.get(state.getId());
      pruneTree();
    } else {
      createTree(state);
    }

    int simulations = GameController.getSimulations();
    AgentAsyncTask agentAsyncTask = GameController.getAgentAsyncTask();

    // Explore the tree
    for (int i = 0; i < simulations; i++) {
      if (agentAsyncTask.isCancelled()) {  // task is aborted, bail out
        return null;
      }
      GameController.onProgressUpdate((int) (i * 100 / (double) simulations + 0.5));
      simulate();
    }

    // Return visit counts
    int[] visits = new int[Game.board_size];
    for (Edge edge : root.edges) {
      visits[edge.action] = edge.N;
    }
    return visits;
  }

  /**
   * Move to leaf node, evaluate it, and back propagate the value
   */
  private void simulate() {
    List<Edge> breadcrumbs = new ArrayList<>();
    Node leaf = moveToLeaf(breadcrumbs);
    float value;
    if (leaf.state.isFinished()) {
      value = leaf.state.getValue();
    } else {
      float[] pi = new float[Game.board_size];
      value = evaluator.predict(pi, leaf.state.getCanonicalBoard());
      expandNode(leaf, pi);
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
   * Expand node
   */
  private void expandNode(Node node, float[] pi) {
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
      node.edges.add(new Edge(node, newNode, action, pi[action]));
    }
  }

  /**
   * Back propagate the value up the tree
   */
  private void backPropagate(Node node, float value, List<Edge> breadcrumbs) {
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
      u = edge.Q + C_PUCT * edge.P * Math.sqrt(nodeVisits) / (1 + edge.N);
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
    final float P;

    int N = 0;
    int W = 0;
    double Q = 0;

    Edge(Node inNode, Node outNode, int action, float prior) {
      this.outNode = outNode;
      this.action = action;
      this.player = inNode.state.getPlayer();
      this.P = prior;
    }
  }

}
