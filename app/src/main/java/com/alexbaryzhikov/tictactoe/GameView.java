package com.alexbaryzhikov.tictactoe;

import android.content.res.AssetManager;

public interface GameView {
  AssetManager getAssets();
  void onGameStateUpdated();
  void showProgressBar(boolean show);
  void setProgressPercent(int progress);
}
