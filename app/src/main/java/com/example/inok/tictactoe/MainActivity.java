package com.example.inok.tictactoe;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.inok.tictactoe.mcts.Player;

public class MainActivity extends AppCompatActivity implements GameView {

  private static final String TAG = "TAG_GameView";
  private static final GameController controller = GameController.getInstance();
  private static final GameModel model = GameModel.getInstance();
  private GridView boardGrid;
  private BoardAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Register activity with game controller
    controller.setView(this);

    // Display dimensions
    int displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int displayHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Status bar + app bar size
    int contentViewTop = getStatusAndAppBarHeight();

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
    boardGrid.setNumColumns(model.getBoardSize());
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
        Log.d(TAG, "Player move: " + position);
        controller.onPlayerClick(position);
      }
    });

    // Insert Board view into root layout
    RelativeLayout rootLayout = findViewById(R.id.root_layout);
    rootLayout.addView(boardGrid);

    // If no current player -- start a new game
    if (model.getPlayer() == Player.NONE) {
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
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.new_game:
        controller.startNewGame();
        Toast.makeText(this, R.string.new_game_started, Toast.LENGTH_SHORT).show();
        return true;
      case R.id.board_size_1:
        model.setBoardSize(5);
        Toast.makeText(this, R.string.board_size_set_1, Toast.LENGTH_SHORT).show();
        return true;
      case R.id.board_size_2:
        model.setBoardSize(6);
        Toast.makeText(this, R.string.board_size_set_2, Toast.LENGTH_SHORT).show();
        return true;
      case R.id.board_size_3:
        model.setBoardSize(7);
        Toast.makeText(this, R.string.board_size_set_3, Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBoardSizeChanged() {
    boardGrid.setNumColumns(model.getBoardSize());
  }

  @Override
  public void onGameStateUpdated() {
    adapter.setBoard(model.getBoard());
    adapter.notifyDataSetChanged();
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

  /**
   * View adapter for board grid
   */
  private class BoardAdapter extends BaseAdapter {

    private Board board;

    public void setBoard(Board board) {
      this.board = board;
    }

    /**
     * How many items are in the data set represented by this Adapter
     */
    @Override
    public int getCount() {
      if (board != null) {
        return board.getCells().length;
      }
      return 0;
    }

    /**
     * Get the data item associated with the specified position in the data set
     */
    @Override
    public Object getItem(int position) {
      if (board != null) {
        return board.get(position);
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
      if (board == null) {
        return null;
      }
      if (convertView == null) {
        convertView = new View(MainActivity.this);
      }
      int viewSize = boardGrid.getColumnWidth();
      convertView.setLayoutParams(new GridView.LayoutParams(viewSize, viewSize));
      Player cell = (Player) getItem(position);
      switch (cell) {
        case NONE:
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
