package com.example.inok.tictactoe.mcts;

public class Edge {

  public Node inNode;
  public Node outNode;
  public int action;
  public int player;
  public int N = 0;
  public int W = 0;
  public double Q = 0;

  public Edge(Node inNode, Node outNode, int action) {
    this.inNode = inNode;
    this.outNode = outNode;
    this.action = action;
    this.player = inNode.state.getPlayer();
  }
}
