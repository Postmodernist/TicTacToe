package com.alexbaryzhikov.tictactoe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.lang.ref.WeakReference;

/**
 * View adapter for board grid
 */
public class BoardAdapter extends BaseAdapter {

  private static final String TAG = "BoardAdapter";
  private WeakReference<Context> context;
  private GridView boardGrid;
  private Drawable circle0;
  private Drawable circleA;
  private Drawable circleB;
  private int[] board;

  BoardAdapter(Context context, GridView boardGrid, Drawable circle0, Drawable circleA,
               Drawable circleB) {
    this.context = new WeakReference<>(context);
    this.boardGrid = boardGrid;
    this.circle0 = circle0;
    this.circleA = circleA;
    this.circleB = circleB;
  }

  public void setBoard(int[] board) {
    this.board = board;
  }

  /**
   * How many items are in the data set represented by this Adapter
   */
  @Override
  public int getCount() {
    if (board != null) {
      return board.length;
    }
    return 0;
  }

  /**
   * Get the data item associated with the specified position in the data set
   */
  @Override
  public Object getItem(int position) {
    if (board != null) {
      return board[position];
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
      convertView = new View(context.get());
    }
    int viewSize = boardGrid.getColumnWidth();
    convertView.setLayoutParams(new GridView.LayoutParams(viewSize, viewSize));
    int square = (int) getItem(position);
    switch (square) {
      case 0:
        convertView.setBackground(circle0);
        break;
      case 1:
        convertView.setBackground(circleA);
        break;
      case -1:
        convertView.setBackground(circleB);
        break;
      default:
        Log.e(TAG, "Unknown square value: " + square);
        break;
    }
    return convertView;
  }
}
