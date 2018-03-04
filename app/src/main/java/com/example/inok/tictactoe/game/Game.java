package com.example.inok.tictactoe.game;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 'Four' game. The first player to have 4 pieces in a straight line wins.
 * A player can't place a piece on a square surrounded by empty squares.
 */
public class Game {

  public static int N = 6;
  public static int board_size;
  public static State state;

  private static int[] zero_board;
  private static int[] index_board;
  private static List<int[]> win_positions;
  private static Set<Integer> borders;
  private static int[][] neighbors;
  private static Random random = new Random();

  static {
    init();
  }

  /**
   * Initialize game
   */
  private static void init() {
    board_size = N * N;
    zero_board = new int[board_size];
    index_board = new int[board_size];
    for (int i = 0; i < board_size; i++) {
      index_board[i] = i;
    }
    win_positions = findWinPositions();
    borders = findBorders();
    neighbors = findNeighbors();
  }

  /**
   * Set board size
   */
  public static void setN(int n) {
    N = n;
    init();
  }

  /**
   * Restart game
   */
  public static void restart() {
    int player = random.nextInt(2) * 2 - 1;
    state = new State(zero_board, player, 0);
  }

  /**
   * Convert board to byte string
   */
  @NonNull
  public static String makeId(int[] board) {
    ByteBuffer bytes = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE * board.length);
    bytes.asIntBuffer().put(board);
    return new String(bytes.array());
  }

  /**
   * Test action validity in current game state
   */
  public static boolean isValidAction(int action) {
    for (int i : state.getValidActions()) {
      if (action == i) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return all valid actions
   */
  public static int[] findValidActions(int[] board) {
    int[] buf = new int[board_size];
    int k = 0;
    for (int i : index_board) {
      if (isValidAction(board, i)) {
        buf[k++] = i;
      }
    }
    int[] actions = new int[k];
    System.arraycopy(buf, 0, actions, 0, k);
    return actions;
  }

  /**
   * Test action validity on the board
   */
  private static boolean isValidAction(int[] board, int action) {
    // Square is not empty
    if (board[action] != 0) {
      return false;
    }
    // Border square
    if (borders.contains(action)) {
      return true;
    }
    // Square has non-empty neighbor
    for (int i : neighbors[action]) {
      if (board[i] != 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return true if player made a winning move
   */
  public static boolean isPlayerWon(int[] board, int player) {
    boolean failed;
    for (int[] win : win_positions) {
      failed = false;
      for (int i : win) {
        if (board[i] != player) {
          failed = true;
          break;
        }
      }
      if (!failed) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return all possible win positions
   */
  private static List<int[]> findWinPositions() {
    List<int[]> winPositions = new ArrayList<>();
    for (int[] line : findLines(index_board)) {
      for (int j = 0; j < line.length - 3; j++) {
        int[] four = new int[4];
        System.arraycopy(line, j, four, 0, 4);
        winPositions.add(four);
      }
    }
    return winPositions;
  }

  private static ArrayList<int[]> findLines(int[] board) {
    ArrayList<int[]> lines = new ArrayList<>();
    int[] board_f = flipLR(board);
    int[] row = new int[N];
    int[] col = new int[N];
    for (int i = 0; i < N; i++) {
      row[i] = board[i];
      col[i] = board[i * N];
    }
    lines.add(row);
    lines.add(col);
    lines.add(diagonal(board, 0));
    lines.add(diagonal(board_f, 0));
    for (int i = 1; i < N; i++) {
      row = new int[N];
      col = new int[N];
      for (int j = 0; j < N; j++) {
        row[j] = board[j + i * N];
        col[j] = board[i + j * N];
      }
      lines.add(row);
      lines.add(col);
      if (N - i >= 4) {
        lines.add(diagonal(board, i));
        lines.add(diagonal(board, -i));
        lines.add(diagonal(board_f, i));
        lines.add(diagonal(board_f, -i));
      }
    }
    return lines;
  }

  private static int[] diagonal(int[] array, int offset) {
    int x = 0;
    int y = 0;
    if (offset >= 0) {
      x += offset;
    } else {
      y += -offset;
    }
    ArrayList<Integer> diagonal = new ArrayList<>();
    for (; x < N && y < N; x++, y++) {
      diagonal.add(array[x + y * N]);
    }
    int[] res = new int[diagonal.size()];
    for (int i = 0; i < diagonal.size(); i++) {
      res[i] = diagonal.get(i);
    }
    return res;
  }

  private static int[] flipLR(int[] array) {
    int[] res = new int[array.length];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        res[j + i * N] = array[N - j - 1 + i * N];
      }
    }
    return res;
  }

  /**
   * Return a set of border positions
   */
  private static Set<Integer> findBorders() {
    Set<Integer> borders = new HashSet<>();
    for (int i : index_board) {
      if (isBorder(i)) {
        borders.add(i);
      }
    }
    return borders;
  }

  private static boolean isBorder(int pos) {
    int rem = pos % N;
    return pos < N || pos >= board_size - N || rem == 0 || rem == N - 1;
  }

  /**
   * Return neighbors for each square
   */
  private static int[][] findNeighbors() {
    @SuppressLint("UseSparseArrays")
    int[][] neighbors = new int[board_size][];
    for (int i : index_board) {
      if (i == 0) {  // upper left corner
        neighbors[i] = new int[]{i + 1, i + N, i + N + 1};
      } else if (i == N - 1) {  // upper right corner
        neighbors[i] = new int[]{i - 1, i + N - 1, i + N};
      } else if (i == N * (N - 1)) {  // lower left corner
        neighbors[i] = new int[]{i - N, i - N + 1, i + 1};
      } else if (i == N * N - 1) {  // lower right corner
        neighbors[i] = new int[]{i - N - 1, i - N, i - 1};
      } else if (i < N) {  // upper row
        neighbors[i] = new int[]{i - 1, i + 1, i + N - 1, i + N, i + N + 1};
      } else if (i > N * (N - 1)) {  // upper row
        neighbors[i] = new int[]{i - N - 1, i - N, i - N + 1, i - 1, i + 1};
      } else if (i % N == 0) {  // left column
        neighbors[i] = new int[]{i - N, i - N + 1, i + 1, i + N, i + N + 1};
      } else if (i % N == N - 1) {  // right column
        neighbors[i] = new int[]{i - N - 1, i - N, i - 1, i + N - 1, i + N};
      } else {  // inner squares
        neighbors[i] = new int[]
            {i - N - 1, i - N, i - N + 1, i - 1, i + 1, i + N - 1, i + N, i + N + 1};
      }
    }
    return neighbors;
  }
}
