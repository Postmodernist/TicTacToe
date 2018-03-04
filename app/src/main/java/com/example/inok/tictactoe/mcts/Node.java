package com.example.inok.tictactoe.mcts;

import com.example.inok.tictactoe.game.State;

import java.util.ArrayList;
import java.util.List;

public class Node {

  public State state;
  public List<Edge> edges = new ArrayList<>();

  public Node(State state) {
    this.state = state;
  }
}
