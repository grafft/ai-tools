package ru.isa.ai.dhm.poolers;

/**
 * Created by GraffT on 03.05.2014.
 */
public interface ISpatialPooler {

    /**
     * Initialize the spatial pooler using the given parameters.
     *
     * @param inputDimensions  A list of integers representing the
     *                         dimensions of the input vector. Format is [height, width,
     *                         depth, ...], where each value represents the size of the
     *                         dimension. For a topology of one dimesion with 100 inputs
     *                         use [100]. For a two dimensional topology of 10x5
     *                         use [10,5].
     * @param columnDimensions A list of integers representing the
     *                         dimensions of the columns in the region. Format is [height,
     *                         width, depth, ...], where each value represents the size of
     *                         the dimension. For a topology of one dimension with 2000
     *                         columns use 2000, or [2000]. For a three dimensional
     *                         topology of 32x64x16 use [32, 64, 16].
     */
    void initialize(int[] inputDimensions, int[] columnDimensions) throws SpatialPoolerInitializationException;

    /**
     * This is the main workshorse method of the SpatialPooler class. This
     * method takes an input vector and computes the set of output active
     * columns. If 'learn' is set to True, this method also performs
     * learning.
     *
     * @param inputVector  An array of integer 0's and 1's that comprises
     *                     the input to the spatial pooler. The length of the
     *                     array must match the total number of input bits implied by
     *                     the constructor (also returned by the method getNumInputs). In
     *                     cases where the input is multi-dimensional, inputVector is a
     *                     flattened array of inputs.
     * @param learn        A boolean value indicating whether learning should be
     *                     performed. Learning entails updating the permanence values of
     *                     the synapses, duty cycles, etc. Learning is typically on but
     *                     setting learning to 'off' is useful for analyzing the current
     *                     state of the SP. For example, you might want to feed in various
     *                     inputs and examine the resulting SDR's. Note that if learning
     *                     is off, boosting is turned off and columns that have never won
     *                     will be removed from activeVector.
     * @param activeVector An array representing the winning columns after
     *                     inhinition. The size of the array is equal to the number of
     *                     columns (also returned by the method getNumColumns). This array
     *                     will be populated with 1's at the indices of the active columns,
     *                     and 0's everywhere else. In the case where the output is
     *                     multi-dimensional, activeVector represents a flattened array
     *                     of outputs.
     */
    void compute(byte[] inputVector, boolean learn, byte[] activeVector);

}
