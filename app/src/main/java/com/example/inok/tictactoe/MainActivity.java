package com.example.inok.tictactoe;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.inok.tictactoe.game.Game;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GameView {

  @BindView(R.id.root_layout)
  RelativeLayout rootLayout;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  private BoardAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    // Register GameView with the GameController
    GameController.registerView(this);

    // Get display dimensions
    int displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int displayHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Status bar + app bar size
    int contentViewTop = getStatusAndAppBarHeight();

    // Board width including margins
    int boardWidth = displayWidth < displayHeight ? displayWidth : displayHeight - contentViewTop;
    boardWidth -= (int) (getResources().getDimension(R.dimen.app_margin) * 2 + 0.5f);

    // Board view
    GridView boardGrid = new GridView(this);

    // View parameters
    RelativeLayout.LayoutParams gridLayoutParams =
        new RelativeLayout.LayoutParams(boardWidth, boardWidth);
    gridLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    boardGrid.setLayoutParams(gridLayoutParams);
    boardGrid.setNumColumns(Game.N);
    boardGrid.setHorizontalSpacing((int) getResources().getDimension(R.dimen.grid_spacing));
    boardGrid.setVerticalSpacing((int) getResources().getDimension(R.dimen.grid_spacing));
    boardGrid.setDrawSelectorOnTop(true); // enable ripple effect
    boardGrid.setSelector(R.drawable.circular_ripple);

    // Grid adapter
    adapter = new BoardAdapter(this, boardGrid,
        getDrawable(R.drawable.circle_0),
        getDrawable(R.drawable.circle_a),
        getDrawable(R.drawable.circle_b));
    boardGrid.setAdapter(adapter);

    // Item click listener
    boardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameController.onPlayerClick(position);
      }
    });

    // Insert Board view into root layout
    rootLayout.addView(boardGrid);

    if (Game.state == null) {
      GameController.startNewGame();
    } else {
      if (!Game.state.isFinished() && Game.state.getPlayer() == -1) {
        // Agent is thinking -- show progress bar
        progressBar.setVisibility(View.VISIBLE);
      }
      onGameStateUpdated();
    }
  }

  @Override
  protected void onDestroy() {
    GameController.unregisterView();
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.new_game:
        GameController.startNewGame();
        Toast.makeText(this, R.string.new_game_started, Toast.LENGTH_SHORT).show();
        return true;
      case R.id.difficulty_easy:
        GameController.setDifficulty("easy");
        Toast.makeText(this, R.string.difficulty_set_easy, Toast.LENGTH_SHORT).show();
        return true;
      case R.id.difficulty_medium:
        GameController.setDifficulty("medium");
        Toast.makeText(this, R.string.difficulty_set_medium, Toast.LENGTH_SHORT).show();
        return true;
      case R.id.difficulty_hard:
        GameController.setDifficulty("hard");
        Toast.makeText(this, R.string.difficulty_set_hard, Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onGameStateUpdated() {
    adapter.setBoard(Game.state.getBoard());
    adapter.notifyDataSetChanged();
  }

  @Override
  public void showProgressBar(boolean show) {
      progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setProgressPercent(int progress) {
    progressBar.setProgress(progress);
  }

  /**
   * Get status bar height + app bar height
   */
  private int getStatusAndAppBarHeight() {
    // Status bar height
    int statusBarHeight = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen",
        "android");
    if (resourceId > 0) {
      statusBarHeight = getResources().getDimensionPixelSize(resourceId);
    }
    // App bar height
    int[] attrs = new int[]{android.R.attr.actionBarSize};
    final TypedArray styledAttributes = getTheme().obtainStyledAttributes(attrs);
    int appBarHeight = (int) styledAttributes.getDimension(0, 0);
    styledAttributes.recycle();
    return statusBarHeight + appBarHeight;
  }
}
