package ru.isa.ai.newdhm;

import cern.colt.matrix.tint.impl.SparseIntMatrix1D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import cern.colt.matrix.tint.IntMatrix1D;
import ru.isa.ai.dhm.MathUtils;

public class Region {
    public Column[] columns;

    public int numColumns = 1;
    public int xDimension;
    public int yDimension;

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

    ////////////////////////////////////////////////////////
    // загрузка свойств из файла
    private final Logger logger = LogManager.getLogger(Region.class.getSimpleName());
    private final String SP_PROP_FILENAME = "htm.properties";
    private String filePropName = SP_PROP_FILENAME;

    //////////////////////////////////////////////////////////
    public Region(){
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
    /*
    neighbors(c) -  Список колонок находящихся в радиусе подавления
inhibitionRadius колонки c.
     */
    public IntMatrix1D neighbours(int c) {
        IntMatrix1D result = new SparseIntMatrix1D(1000);
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
        double[] overlaps = new double[1000];
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

    // TODO AP: аналогично с Cortex - надо всю неалгоритмическую работу вывести во вспомогательные классы
    public void loadProperties() throws RegionInitializationException {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "desiredLocalActivity":
                        this.desiredLocalActivity = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "minOverlap":
                        this.minOverlap = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "connectedPerm":
                        this.connectedPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceInc":
                        this.permanenceInc = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceDec":
                        this.permanenceDec = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "cellsPerColumn":
                        this.cellsPerColumn = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "activationThreshold":
                        this.activationThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "initialPerm":
                        this.initialPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minThreshold":
                        this.minThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "newSynapseCount":
                        this.newSynapseCount = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "xDimension":
                        this.xDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "yDimension":
                        this.yDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    default:
                        logger.error("Illegal property name: " + name);
                        break;
                }
            }
            input.close();
        } catch (IOException e) {
            throw new RegionInitializationException("Cannot load properties file " + filePropName, e);
        } catch (NumberFormatException nfe) {
            throw new RegionInitializationException("Wrong property value in property file " + filePropName, nfe);
        }
    }


    /*
    private void checkProperties() throws RegionInitializationException {
        if (numColumns <= 0)
            throw new RegionInitializationException("Column dimensions must be non zero positive values");
        if (numInputs <= 0)
            throw new RegionInitializationException("Input dimensions must be non zero positive values");
        if (numActiveColumnsPerInhArea <= 0 && (localAreaDensity <= 0 || localAreaDensity > 0.5))
            throw new RegionInitializationException("Or numActiveColumnsPerInhArea > 0 or localAreaDensity > 0 " +
                    "and localAreaDensity <= 0.5");
        if (potentialPct <= 0 || potentialPct > 1)
            throw new RegionInitializationException("potentialPct must be > 0 and <= 1");
        potentialRadius = potentialRadius > numInputs ? numInputs : potentialRadius;
    }
    */

    public void saveProperties() throws RegionInitializationException {
        Properties properties = new Properties();
        try {
                properties.setProperty("desiredLocalActivity",String.valueOf(desiredLocalActivity));
                properties.setProperty("minOverlap",String.valueOf(minOverlap));
                properties.setProperty("connectedPerm",String.valueOf(connectedPerm));
                properties.setProperty("permanenceInc", String.valueOf(permanenceInc));
                properties.setProperty("permanenceDec", String.valueOf(permanenceDec));
                properties.setProperty("activationThreshold",String.valueOf(activationThreshold));
                properties.setProperty("initialPerm",String.valueOf(initialPerm));
                properties.setProperty("minThreshold",String.valueOf(minThreshold));
                properties.setProperty("newSynapseCount",String.valueOf(newSynapseCount));
                properties.setProperty("xDimension",String.valueOf(xDimension));
                properties.setProperty("yDimension",String.valueOf(yDimension));

                FileOutputStream output = new FileOutputStream(filePropName);
                properties.store(output,"Saved settings");
                output.close();

        } catch (IOException e) {
            throw new RegionInitializationException("Cannot save properties file " + filePropName, e);
        }
    }
//////////////////////////////////////////////////////////
   public double getInhibitionRadius(){
        return inhibitionRadius;
    }
}
