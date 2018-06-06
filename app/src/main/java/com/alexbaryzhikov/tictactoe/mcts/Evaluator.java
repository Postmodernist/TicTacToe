package com.alexbaryzhikov.tictactoe.mcts;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

class Evaluator {

  private static final String MODEL_FILE = "opt_model.pb";
  private static final int INPUT_DIM = 7;
  private static final String INPUT_NAME = "input_1";
  private static final String[] OUTPUT_NAMES = {"pi/Softmax", "v/Tanh"};

  private TensorFlowInferenceInterface inferenceInterface;

  private Evaluator() {  // prevents instantiation
  }

  static Evaluator create(@NonNull AssetManager assetManager) {
    Evaluator evaluator = new Evaluator();
    evaluator.inferenceInterface =
        new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    return evaluator;
  }

  /**
   * Predict Pi and Value of the canonicalBoard.
   *
   * @param pi             The array to which Pi will be written
   * @param canonicalBoard board in canonical form (pow of player 1)
   * @return Value
   */
  float predict(float[] pi, float[] canonicalBoard) {
    if (pi == null || pi.length != INPUT_DIM * INPUT_DIM) {
      throw new IllegalArgumentException("Invalid argument 'pi'");
    }

    float[] value = {0.0f};

    // Predict
    inferenceInterface.feed(INPUT_NAME, canonicalBoard, 1, INPUT_DIM, INPUT_DIM);
    inferenceInterface.run(OUTPUT_NAMES);
    inferenceInterface.fetch(OUTPUT_NAMES[0], pi);
    inferenceInterface.fetch(OUTPUT_NAMES[1], value);
    return value[0];
  }
}
