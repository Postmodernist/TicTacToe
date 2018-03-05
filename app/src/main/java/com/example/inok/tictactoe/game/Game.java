package com.example.inok.tictactoe.game;

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
  private static Set<Integer> borders;
  private static int[][] neighbors;
  private static int[][][] win_segments;
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
    borders = findBorders();
    neighbors = findNeighbors();
    win_segments = findWinSegments();
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
    state = new State(zero_board, player);
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


  /* VALID ACTIONS ------------------------------------------------------------------------------ */

  /**
   * Test action validity in current game state
   */
  public static boolean isValidAction(int action) {
    return state.getValidActions().contains(action);
  }

  /**
   * Return initial set of valid actions
   */
  public static Set<Integer> getInitialValidActions() {
    return new HashSet<>(borders);
  }

  /**
   * Update valid actions
   */
  public static Set<Integer>
  updateValidActions(Set<Integer> validActions, int[] board, int action) {
    validActions.remove(action);
    List<Integer> new_actions = new ArrayList<>();
    for (int i : neighbors[action]) {
      if (board[i] == 0) {
        new_actions.add(i);
      }
    }
    validActions.addAll(new_actions);
    return validActions;
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

  /**
   * Return true if square is on the border of the board
   */
  private static boolean isBorder(int square) {
    int rem = square % N;
    return square < N || square >= board_size - N || rem == 0 || rem == N - 1;
  }

  /**
   * Return neighbors for each square
   */
  private static int[][] findNeighbors() {
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
      } else if (i > N * (N - 1)) {  // lower row
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

  /* WIN CONDITIONS ----------------------------------------------------------------------------- */

  /**
   * Return true if player made a winning move
   */
  public static boolean isPlayerWon(int[] board, int player, int action) {
    boolean failed;
    for (int[] segment : win_segments[action]) {
      failed = false;
      for (int i : segment) {
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
   * Return all possible win segments of each square
   */
  private static int[][][] findWinSegments() {
    int[][][] winSegments = new int[board_size][][];
    for (int square : index_board) {
      List<int[]> validSegments = new ArrayList<>();
      for (int[] segment : getSegments(square)) {
        if (isValidSegment(segment)) {
          validSegments.add(segment);
        }
      }
      winSegments[square] = validSegments.toArray(new int[validSegments.size()][]);
    }
    return winSegments;
  }

  /**
   * Get all segments of length 4 that include the square
   */
  private static int[][] getSegments(int square) {
    int[][] segments = new int[16][4];
    for (int j = 0, l = -3; l < 1; j++, l++) {
      for (int k = 0; k < 4; k++) {
        segments[j][k] = square + k + l;  // row
        segments[j + 4][k] = square + N * (k + l);  // column
        segments[j + 8][k] = square + (N + 1) * (k + l);  // diagonal \
        segments[j + 12][k] = square + (N - 1) * (k + l);  // diagonal /
      }
    }
    return segments;
  }

  /**
   * Test if segment is a valid continuous board segment
   */
  private static boolean isValidSegment(int[] segment) {
    // Out of bounds
    for (int i : segment) {
      if (i < 0 || i >= board_size) {
        return false;
      }
    }
    // Tearing
    for (int i = 0; i < 3; i++) {
      if (Math.abs(segment[i] % N - segment[i + 1] % N) > 1) {
        return false;
      }
    }
    return true;
  }
}
