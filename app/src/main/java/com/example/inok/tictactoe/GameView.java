package com.example.inok.tictactoe;

public interface GameView {
  void onBoardSizeChanged();
  void onGameStateUpdated();
  void showProgressBar(boolean show);
  void setProgressPercent(int progress);
}
