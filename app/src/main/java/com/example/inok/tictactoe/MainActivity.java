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

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private GridView boardGrid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Display dimensions
    int displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int displayHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Status bar + app bar size
    int contentViewTop = getStatusAndAppBarSize();
    Log.i(TAG, "Content view top: " + contentViewTop);

    // Board width including margins
    int boardWidth = displayWidth < displayHeight ? displayWidth : displayHeight - contentViewTop;
    boardWidth -= (int) (getResources().getDimension(R.dimen.app_margin) * 2 + 0.5f);

    // Board GridView
    boardGrid = new GridView(this);
    RelativeLayout.LayoutParams gridLayoutParams = new RelativeLayout.LayoutParams(
        boardWidth, boardWidth);
    gridLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    boardGrid.setLayoutParams(gridLayoutParams);
    boardGrid.setNumColumns(GameModel.GRID_SIZE);
    boardGrid.setHorizontalSpacing((int) getResources().getDimension(R.dimen.grid_spacing));
    boardGrid.setVerticalSpacing((int) getResources().getDimension(R.dimen.grid_spacing));
    boardGrid.setAdapter(new BoardAdapter());
    boardGrid.setDrawSelectorOnTop(true); // enable ripple effect
    boardGrid.setSelector(R.drawable.circular_ripple);
    boardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Item " + position);
        view.setBackground(getDrawable(R.drawable.circle_a));
      }
    });

    RelativeLayout rootLayout = findViewById(R.id.root_layout);
    rootLayout.addView(boardGrid);
  }

  /** Get status bar height + app bar height */
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

  /** View adapter for board grid */
  private class BoardAdapter extends BaseAdapter {

    /** How many items are in the data set represented by this Adapter */
    @Override
    public int getCount() {
      return GameModel.GRID_SIZE * GameModel.GRID_SIZE;
    }

    /** Get the data item associated with the specified position in the data set */
    @Override
    public Object getItem(int position) {
      return null;
    }

    /** Get the row id associated with the specified position in the list */
    @Override
    public long getItemId(int position) {
      return 0;
    }

    /** Get a View that displays the data at the specified position in the data set */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        int viewSize = boardGrid.getColumnWidth();
        convertView = new View(MainActivity.this);
        convertView.setLayoutParams(new GridView.LayoutParams(viewSize, viewSize));
      }
      convertView.setBackground(getDrawable(R.drawable.circle_0));
      return convertView;
    }
  }
}
