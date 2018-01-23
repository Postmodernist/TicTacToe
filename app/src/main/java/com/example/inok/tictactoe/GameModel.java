package com.example.inok.tictactoe;

public class GameModel {

  public static final int GRID_SIZE = 3;
  private static GameModel instance = new GameModel();

  private GameModel() {
  }

  public static GameModel getInstance() {
    return instance;
  }
}
