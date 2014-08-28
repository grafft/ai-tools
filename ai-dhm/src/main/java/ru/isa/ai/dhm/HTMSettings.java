package ru.isa.ai.dhm;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 12:02
 */
public final class HTMSettings {
    //group of default values for properties
    public static final double DESIRED_LOCAL_ACTIVITY_DEFAULT = 20.0;
    public static final double MINIMAL_OVERLAP_DEFAULT = 50.0;
    public static final double CONNECTED_PERMISSION_DEFAULT = 0.2;
    public static final double PERMANENCE_INC_DEFAULT = 0.1;
    public static final double PERMANENCE_DEC_DEFAULT = 0.1;
    public static final double CELLS_PER_COLUMN_DEFAULT = 4.0;
    public static final double ACTIVATION_THRESHOLD_DEFAULT = 10.0;
    public static final double INITIAL_PERMANENCE_DEFAULT = 0.1;
    public static final double MINIMAL_THRESHOLD_DEFAULT = 4.0;
    public static final double NEW_SYNAPSES_COUNT_DEFAULT = 30.0;
    public static final double REGION_X_DIMENSION_DEFAULT = 20.0;
    public static final double REGION_Y_DIMENSION_DEFAULT = 10.0;

    public double[] initialParameters;

    public HTMSettings() {
        initialParameters = new double[]{DESIRED_LOCAL_ACTIVITY_DEFAULT,
                MINIMAL_OVERLAP_DEFAULT,
                CONNECTED_PERMISSION_DEFAULT,
                PERMANENCE_INC_DEFAULT,
                PERMANENCE_DEC_DEFAULT,
                CELLS_PER_COLUMN_DEFAULT,
                ACTIVATION_THRESHOLD_DEFAULT,
                INITIAL_PERMANENCE_DEFAULT,
                MINIMAL_THRESHOLD_DEFAULT,
                NEW_SYNAPSES_COUNT_DEFAULT,
                REGION_X_DIMENSION_DEFAULT,
                REGION_Y_DIMENSION_DEFAULT};
    }
}
