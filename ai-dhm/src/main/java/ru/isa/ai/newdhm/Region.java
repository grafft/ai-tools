package ru.isa.ai.newdhm;

import cern.colt.matrix.tint.impl.SparseIntMatrix1D;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.IntMatrix2D;

public class Region {
    public Column[] columns;

    public int numColumns = 0;
    private int xDimension;
    private int yDimension;

    /*Список индексов колонок – победителей благодаря прямым
    входным данным. (Выход пространственного группировщика)
   */
    public IntMatrix2D activeColumns;

    //Число клеток в каждой из колонок.
    public int cellsPerColumn;

    /*
    Средний размер входного (рецепторного) поля колонок
     */
    public double inhibitionRadius;

    /*
    Минимальное число активных входов колонки для ее участия
    в шаге подавления.
     */
    public int minOverlap;
    /*
    Параметр контролирующий число колонок победителей
    после шага подавления.
    */
    public int desiredLocalActivity;

    /*
    Если значение перманентности синапса больше данного параметра, то он считается подключенным (действующим).
     */
    public double connectedPerm;
    /*
    Количество значений перманентности синапсов, которые
    были увеличены при обучении.
     */
    public double permanenceInc;
    /*
    Количество значений перманентности синапсов, которые
    были уменьшены при обучении.
    */
    public double permanenceDec;
    /*
    Порог активации для сегмента. Если число активных
    подключенных синапсов в сегменте больше чем
    activationThreshold, данный сегмент считается активным.
     */
    public int activationThreshold;

    /*
   Начальное значение перманентности для синапсов
    */
    public double initialPerm;
    /*
    Минимальная активность в сегменте для обучения.
     */
    public int minThreshold;
    /*
    Максимальное число синапсов добавляемых сегменту при
    обучении.
     */
    public int newSynapseCount;

    //////////////////////////////////////////////////////////
    final private int NUM_MEMORY_CELLS = 1000;

    public Region(double[] parameters){
        this.desiredLocalActivity = (int)parameters[0];
        this.minOverlap = (int)parameters[1];
        this.connectedPerm = parameters[2];
        this.permanenceInc = parameters[3];
        this.permanenceDec = parameters[4];
        this.cellsPerColumn = (int)parameters[5];
        this.activationThreshold = (int)parameters[6];
        this.initialPerm = parameters[7];
        this.minThreshold = (int)parameters[8];
        this.newSynapseCount = (int)parameters[9];
        this.xDimension = (int)parameters[10];
        this.yDimension = (int)parameters[11];

        addColumns();
    }

    public void addColumns(){
        numColumns = xDimension * yDimension;

        columns = new Column[numColumns];

        int ind = 0;
        for (int i = 0; i < xDimension; i++) {
            for (int j = 0;j < yDimension; j++) {
                columns[ind] = new Column(this, i, j);
                ind++;
            }
        }

    }

    public void initParametersForColumns(double minOverlap, double minDutyCycle, double[] initOverlapDuty, double[] initActiveDuty){
        int i = 0;
        for(Column c : this.columns){
            c.minOverlap = minOverlap;
            c.minDutyCycle = minDutyCycle;
            c.activeDutyCycle = initActiveDuty[i];
            c.overlapDutyCycle = initOverlapDuty[i];
            i++;
        }
    }


    /*
    neighbors(c) -  Список колонок находящихся в радиусе подавления
inhibitionRadius колонки c.
     */
    public IntMatrix1D neighbours(int c) {
        IntMatrix1D result = new SparseIntMatrix1D(NUM_MEMORY_CELLS);
        int length = 1 ;
        for(int i = 0; i < numColumns; i++) {
            if ((Math.abs(columns[i].x - columns[c].x) < inhibitionRadius) &&
                    (Math.abs(columns[i].y - columns[c].y) < inhibitionRadius)){
                result.setQuick(length,i);
                length++;
            }
        }
        result.setQuick(0,length-1);
        return result;
    }


    private int partition (double[] array, int start, int end)
    {
        int marker = start;
        for ( int i = start; i <= end; i++ )
        {
            if ( array[i] <= array[end] )
            {
                double temp = array[marker];
                array[marker] = array[i];
                array[i] = temp;
                marker += 1;
            }
        }
        return marker - 1;
    }

    private void quicksort (double[] array, int start, int end)
    {
        if ( start >= end )
        {
            return;
        }
        int pivot = partition (array, start, end);
        quicksort (array, start, pivot-1);
        quicksort (array, pivot+1, end);
    }
    /*
   Для заданного списка колонок возвращает их k-ое максимальное значение
их перекрытий со входом
    */
    public double kthScore(IntMatrix1D cols, int k){
        double[] overlaps = new double[NUM_MEMORY_CELLS];
        //doubleMatrix1D overlaps;

        int length = 1;
        for(int i=1; i <= cols.getQuick(0); i++) {
            overlaps[i] = columns[cols.get(i)].overlap;
            length++;
        }
        //Arrays.sort( overlaps);
        overlaps[0] = length - 1;
        quicksort(overlaps, 1, length - 1);
        return overlaps[length - 1 - k];
    }

    /*
    Средний радиус подключенных рецептивных полей всех колонок. Размер
    подключенного рецептивного поля колонки определяется только по
    подключенным синапсам (у которых значение перманентности >=
    connectedPerm). Используется для определения протяженности
    латерального подавления между колонками.
     */

    public double averageReceptiveFieldSize() {
        double result = 0.0;

        ///////
        double xDistance;
        double yDistance;
        for(int i = 0; i < numColumns; i++) {
            xDistance = 0.0;
            yDistance = 0.0;
            for(Synapse synapse : columns[i].potentialSynapses) {
                if (synapse == null) break;
                if (synapse.permanence > connectedPerm) {
                    //double xCalculated = Math.abs(columns.get(i).x.doubleValue() - synapse.c.doubleValue());
                    //double yCalculated = Math.abs(columns.get(i).y.doubleValue() - synapse.i.doubleValue());

                    double xCalculated = Math.abs(columns[i].x - synapse.c);
                    double yCalculated = Math.abs(columns[i].y - synapse.i);

                    xDistance = xDistance > xCalculated ? xDistance : xCalculated;
                    yDistance = yDistance > yCalculated ? yDistance : yCalculated;
                }
            }
            result = (Math.sqrt(xDistance*xDistance + yDistance*yDistance) + i * result ) / (i+1);
        }
        return result;
    }

    /*
    Возвращает максимальное число циклов активности для всех заданных
    колонок.
     */
    public double maxDutyCycle(IntMatrix1D cols) {
        double max = 0.0;
        for(int col = 1; col < cols.getQuick(0)-1; col++) {
            if (max < columns[col].activeDutyCycle);
            max = columns[col].activeDutyCycle;
        }
        return max;
    }

    double GetMinLocalActivity(int i){
        return kthScore(neighbours(i), desiredLocalActivity);
    }

//////////////////////////////////////////////////////////
   public double getInhibitionRadius(){
        return inhibitionRadius;
    }
   public void setInhibitionRadius(double value) {inhibitionRadius = value; }
   public int getXDim() {return this.xDimension; }
   public int getYDim() {return this.yDimension; }
}
