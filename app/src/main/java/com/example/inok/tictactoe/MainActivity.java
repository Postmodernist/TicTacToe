package com.example.inok.tictactoe;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.example.inok.tictactoe.Board.Cell;

public class MainActivity extends AppCompatActivity implements GameView {

  private static final String TAG = "TAG_GameView";
  private GameController controller;
  private GameModel model;
  private GridView boardGrid;
  private BoardAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Get controller and model
    controller = GameController.getInstance();
    model = GameModel.getInstance();

    // Register activity with game controller
    controller.setView(this);

    // Display dimensions
    int displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int displayHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Status bar + app bar size
    int contentViewTop = getStatusAndAppBarSize();

    // Board width including margins
    int boardWidth = displayWidth < displayHeight ? displayWidth : displayHeight - contentViewTop;
    boardWidth -= (int) (getResources().getDimension(R.dimen.app_margin) * 2 + 0.5f);

    // Board view
    boardGrid = new GridView(this);

    // View parameters
    RelativeLayout.LayoutParams gridLayoutParams = new RelativeLayout.LayoutParams(
        boardWidth, boardWidth);
    gridLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    boardGrid.setLayoutParams(gridLayoutParams);
    boardGrid.setNumColumns(GameModel.DEFAULT_BOARD_SIZE);
    boardGrid.setHorizontalSpacing((int) getResources().getDimension(R.dimen.grid_spacing));
    boardGrid.setVerticalSpacing((int) getResources().getDimension(R.dimen.grid_spacing));
    boardGrid.setDrawSelectorOnTop(true); // enable ripple effect
    boardGrid.setSelector(R.drawable.circular_ripple);

    // Grid adapter
    adapter = new BoardAdapter();
    boardGrid.setAdapter(adapter);

    // Item click listener
    boardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Click on cell " + position);
        controller.onCellClick(position);
      }
    });

    // Insert Board view into root layout
    RelativeLayout rootLayout = findViewById(R.id.root_layout);
    rootLayout.addView(boardGrid);

    // If finished -- start new game
    if (model.getStatus() == GameModel.Status.FINISHED) {
      controller.startNewGame();
    } else {
      onGameStateUpdated();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Checkout
    controller.freeView();
  }

  @Override
  public void onGameStateUpdated() {
    adapter.setBoardState(GameModel.getInstance().getBoard());
    adapter.notifyDataSetChanged();
  }

  /**
   * Get status bar height + app bar height
   */
  private int getStatusAndAppBarSize() {
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

  /**
   * View adapter for board grid
   */
  private class BoardAdapter extends BaseAdapter {

    private Cell[] boardState;

    public void setBoardState(Board board) {
      this.boardState = board.getState();
    }

    /**
     * How many items are in the data set represented by this Adapter
     */
    @Override
    public int getCount() {
      if (boardState != null) {
        return boardState.length;
      }
      return 0;
    }

    /**
     * Get the data item associated with the specified position in the data set
     */
    @Override
    public Object getItem(int position) {
      if (boardState != null) {
        return boardState[position];
      }
      return null;
    }

    /**
     * Get the row id associated with the specified position in the list
     */
    @Override
    public long getItemId(int position) {
      return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (boardState == null) {
        return null;
      }
      if (convertView == null) {
        int viewSize = boardGrid.getColumnWidth();
        convertView = new View(MainActivity.this);
        convertView.setLayoutParams(new GridView.LayoutParams(viewSize, viewSize));
      }
      Cell cell = (Cell) getItem(position);
      switch (cell) {
        case EMPTY:
          convertView.setBackground(getDrawable(R.drawable.circle_0));
          break;
        case PLAYER_A:
          convertView.setBackground(getDrawable(R.drawable.circle_a));
          break;
        case PLAYER_B:
          convertView.setBackground(getDrawable(R.drawable.circle_b));
          break;
        default:
          Log.e(TAG, "Unknown cell value: " + cell);
          break;
      }
      return convertView;
    }
  }
}
