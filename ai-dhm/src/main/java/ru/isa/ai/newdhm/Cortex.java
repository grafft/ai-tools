package ru.isa.ai.newdhm;

import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix2D;
import cern.colt.matrix.tint.impl.SparseIntMatrix2D;
import java.util.Random;
import cern.colt.matrix.tint.IntMatrix2D;
import cern.colt.matrix.tbit.BitMatrix;
//import com.sun.scenario.Settings;
import ru.isa.ai.newdhm.applet.HTMConfiguration;

public class Cortex {
    public int time = 0;
    public int totalTime = 0;

    // Список всех колонок
    public Region[] regions;
    private int numRegions;
    private int inputXDim = 0;
    private int inputYDim = 0;
    private BitMatrix inputBits;
    private final int SYNAPSE_MEM_SIZE = 1000;

    private enum State {
        active,
        learn
    }

    /////////////////////////////////////////////////////////////////////////
    //  Реализация
    /////////////////////////////////////////////////////////////////////////

    public Cortex(int numRegions_, HTMConfiguration.Settings[] settings) {
        numRegions = numRegions_;
        regions = new Region[numRegions];
        for(int i = 0; i < numRegions; i++){
            regions[i] = new Region(settings[i].initialParameters);
        }
    }
    /////////////////////////////////////////////////////////////////////////

    public int getInputXDim(){
        return inputXDim;
    }

    public int getInputYDim(){
        return inputYDim;
    }

    /*
    Вход для данного уровня в момент времени t. input(t, j) = 1
если j-ый бит входа = 1.
     */
    private int inputDefault(int t, int j, int k) {
            int value = t % 2 > 0 ? rnd.nextInt(2) : Math.sin(j + k + totalTime) > 0 ? 1 : 0;
            return value;
    }

    public boolean input(int c, int i){
        boolean val = inputBits.get(c,i);
        return val;
    }
    /*
    Вычисляет интервальное среднее того, как часто колонка c была активной
     после подавления.
     */
    private double updateActiveDutyCycle(int regInd, int c) {
        double value = 0.0;
        IntMatrix1D col = regions[regInd].activeColumns.viewRow(time);
        for (int  ind = 1; ind <= col.get(0); ind++ ) {
            if (c == ind -1){
                value = 1.0;
                break;
            }
        }
        return (value + totalTime * regions[regInd].columns[c].activeDutyCycle) / (totalTime + 1.0);
    }

    ////////////////
    private BitMatrix get2DcolsANDcellsAtT(int regInd, State state, int t) {
        BitMatrix list = new BitMatrix(regions[regInd].numColumns,regions[regInd].cellsPerColumn);
        for (int col = 0; col < regions[regInd].numColumns; col++) {
            int ind_i = 0;
            for (Cell i : regions[regInd].columns[col].cells) {
                if (state.equals(State.active)) {
                    list.put(col, ind_i, i.activeState.get(t));

                } else {
                    list.put(col, ind_i, i.learnState.get(t));

                }
                ind_i++;
            }
        }
        return list;
    }

    /*
    Эта процедура возвращает true, если число подключенных синапсов
    сегмента s, которые активны благодаря заданным состояниям в момент t,
    больше чем activationThreshold. Вид состояний state может быть
    activeState, или learnState.
     */
    private boolean segmentActive(int regInd, Segment s, int t, State state) {
        BitMatrix list = new BitMatrix(regions[regInd].numColumns, regions[regInd].cellsPerColumn);
        list = get2DcolsANDcellsAtT(regInd, state, t);
        int counter = 0;
        for (Synapse syn : s.synapses) {
            if (syn == null) break;
            if (list.get(syn.c, syn.i) && syn.permanence > regions[regInd].connectedPerm) {
                counter++;
            }
        }
        return counter > regions[regInd].activationThreshold;
    }


    //////////////////
    private int firstOccurrenceOfSegment(int regInd, int c, int i, Segment seg){
        boolean flag = false;
        int ind = 0;
        while(!flag && ind < regions[regInd].columns[c].cells[i].dendriteSegmentsNum ){
            if (regions[regInd].columns[c].cells[i].dendriteSegments[ind] == seg)
                flag = true;
            ind++;
        }
        return  ind-1;
    }

    /*
    Для данной клетки i колонки c, возвращает индекс сегмента такого, что
    segmentActive(s,t, state) равно true. Если активны несколько сегментов,
    то сегментам последовательностей отдается предпочтение. В противном
    случае предпочтение отдается сегментам с наибольшей активностью.
     */
    private int[] getActiveSegment(int regInd, int c, int i, int t, State state) {
        Segment[] activeSegments = new Segment[regions[regInd].columns[c].cells[i].dendriteSegmentsNum];
        int length = 0;
        for (Segment segment : regions[regInd].columns[c].cells[i].dendriteSegments) {
            if (segment == null) break;
            if (segmentActive(regInd, segment, t, state)) {
                activeSegments[length] = segment;
                length++;
            }
        }

        if (i == 1) {
            //return new int[]{c, i, region.columns.get(c).cells[i].dendriteSegments.indexOf(activeSegments[0])};
            return new int[]{c, i, firstOccurrenceOfSegment(regInd ,c, i, activeSegments[0])};
        } else {
            for (Segment seg : activeSegments) {
                if (seg == null) break;
                if (seg.sequenceSegment)
                    return new int[]{c, i,firstOccurrenceOfSegment(regInd, c, i, seg) };

            }

            BitMatrix list = new BitMatrix(regions[regInd].numColumns, regions[regInd].cellsPerColumn);
            list = get2DcolsANDcellsAtT(regInd, state, t);
            int maxActivity = 0;
            int result = -1;
            for (int j = 0; j < length; j++) {
                int counter = 0;
                for (Synapse syn : activeSegments[j].synapses) {
                    if (syn == null) break;
                    if (list.get(syn.c, syn.i) && syn.permanence > regions[regInd].connectedPerm) {
                        counter++;
                    }
                }
                if (maxActivity < counter) {
                    maxActivity = counter;
                    result = j;
                }
            }
            return new int[]{c, i, result};
        }
    }


    /*
    Для данной клетки i колонки c в момент t, находит сегмент с самым
    большим числом активных синапсов. Т.е. она ищет наилучшее
    соответствие. При этом значения перманентности синапсов допускаются и
    ниже порога connectedPerm. Число активных синапсов допускается ниже
    порога activationThreshold, но должно быть выше minThreshold. Данная
    процедура возвращает индекс сегмента. А если такого не обнаружено, то
    возвращается -1.
     */
    private int[] getBestMatchingSegment(int regInd, int c, int i, int t) {

        BitMatrix list = new BitMatrix(regions[regInd].numColumns, regions[regInd].cellsPerColumn);
        list = get2DcolsANDcellsAtT(regInd , State.active, t);
        int maxActivity = 0;
        int result = -1;
        for (int j = 0; j < regions[regInd].columns[c].cells[i].dendriteSegmentsNum; j++) {
            int counter = 0;
            Segment segment = regions[regInd].columns[c].cells[i].dendriteSegments[j];

            for (Synapse syn : segment.synapses) {
                if (syn == null) break;
                if (list.get(syn.c, syn.i)) {
                    counter++;
                }
            }
            if (maxActivity < counter) {
                maxActivity = counter;
                result = j;
            }
        }
        return maxActivity > regions[regInd].minThreshold ? new int[]{c, i, result} : new int[]{c, i, -1};
    }

    /*
    Для данной колонки возвращает клетку с самым соответствующим входу
    сегментом (как это определено выше). Если такой клетки нет, то
    возвращается клетка с минимальным числом сегментов.
     */
    private int[] getBestMatchingCell(int regInd, int c, int t) {
        int minSegments = 0;
        int cellIndex = -1;
        int minSegmentsCellIndex = -1;

        BitMatrix list = new BitMatrix(regions[regInd].numColumns, regions[regInd].cellsPerColumn);
        list =  get2DcolsANDcellsAtT(regInd, State.active, t);
        int maxActivity = 0;
        int result = -1;

        for (int i = 0; i < regions[regInd].cellsPerColumn; i++) {
            for (int j = 0; j < regions[regInd].columns[c].cells[i].dendriteSegmentsNum; j++) {
                int counter = 0;
                Segment segment = regions[regInd].columns[c].cells[i].dendriteSegments[j];

                for (Synapse syn : segment.synapses) {
                    if (syn == null) break;
                    if ( list.get(syn.c, syn.i)) {
                        counter++;
                    }
                }
                if (maxActivity < counter) {
                    maxActivity = counter;
                    result = j;
                    cellIndex = i;
                }
            }
            if (minSegments == 0 || minSegments > regions[regInd].columns[c].cells[i].dendriteSegmentsNum) {
                minSegments = regions[regInd].columns[c].cells[i].dendriteSegmentsNum;
                minSegmentsCellIndex = i;
            }
        }
        return maxActivity > regions[regInd].minThreshold ? new int[]{c, cellIndex, result} : new int[]{c, minSegmentsCellIndex, -1};
    }

    /*
    Возвращает структуру данных segmentUpdate, содержащую список
    предлагаемых изменений для сегмента s. Пусть activeSynapses список
    активных синапсов у исходных клеток которых activeState равно 1 в
    момент времени t. (Этот список будет пустым если s равно -1 при не
    существующем сегменте.) newSynapses это опциональный параметр, по
    умолчанию равный false. А если newSynapses равно true, тогда число
    синапсов, равное newSynapseCount - count(activeSynapses),
    добавляется к активным синапсам activeSynapses. Такие синапсы
    случайно выбираются из числа клеток, у которых learnState равно 1 в
    момент времени t.
     */
    private SegmentUpdate getSegmentActiveSynapses(int regInd, int c, int i, int t, int s, boolean newSynapses) {
        Synapse[] activeSynapses = new Synapse[SYNAPSE_MEM_SIZE];
        int length = 0;
        if (s >= 0) {
            for (Synapse syn : regions[regInd].columns[c].cells[i].dendriteSegments[s].synapses) {
                if (syn == null) break;
                if (regions[regInd].columns[syn.c].cells[syn.i].activeState.get(t)) {
                    activeSynapses[length] = syn;
                    length++;
                }
            }
        }
        if (newSynapses) {
            Random r = new Random();
            SparseIntMatrix2D learningCells = new SparseIntMatrix2D(regions[regInd].numColumns * regions[regInd].cellsPerColumn, 2);
            int lenLearningCells = 0;
            for (int j = 0; j < regions[regInd].numColumns; j++) {
                for (int k = 0; k < regions[regInd].cellsPerColumn; k++) {
                    if (regions[regInd].columns[j].cells[k].learnState.get(t) && !(c == j && i == k)) {
                        learningCells.setQuick(lenLearningCells , 0, j);
                        learningCells.setQuick(lenLearningCells , 1, k);
                        lenLearningCells++;
                    }
                }
            }
            for (int k = 0; k < regions[regInd].newSynapseCount - length; k++) {
                int[] idx;
                idx = learningCells.viewRow(r.nextInt(lenLearningCells - 1)).toArray();
                activeSynapses[length] = new Synapse(idx[0], idx[1], regions[regInd].initialPerm);
                length++;
            }

        }
        return new SegmentUpdate(new int[]{c, i, s}, activeSynapses);
    }

    /*
        Эта функция проходит по всему списку	segmentUpdate	и усиливает
    каждый сегмент. Для каждого элемента segmentUpdate делаются
    следующие изменения. Если positiveReinforcement равно true, тогда
    синапсы из списка activelist увеличивают значения своих перманентностей
    на величину permanenceInc. Все остальные синапсы уменьшают свои
    перманентности на величину permanenceDec. Если же
    positiveReinforcement равно false, тогда синапсы из списка активных
    уменьшают свою перманентность на величину permanenceDec. После
    этого шага любым синапсам из segmentUpdate, которые только что
    появились, добавляется значение initialPerm.
     */
    private void adaptSegments(int regInd, SegmentUpdate[] segmentList, boolean positiveReinforcement) {
        for (SegmentUpdate segUpd: segmentList) {
            if (segUpd == null) break;
            if (segUpd.segmentIndex[2] < 0) {
                Segment newSegment = new Segment();
                for (Synapse syn : segUpd.activeSynapses) {
                    if (syn == null) break;
                    newSegment.synapses[newSegment.synapsesNum] = syn;
                    newSegment.synapsesNum++;
                }
                newSegment.sequenceSegment = segUpd.sequenceSegment;
                Cell i = regions[regInd].columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]];
                i.dendriteSegments[i.dendriteSegmentsNum] = newSegment;
                i.dendriteSegmentsNum++;
                regions[regInd].columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]] = i;
            } else {
                Segment seg = regions[regInd].columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]].dendriteSegments[segUpd.segmentIndex[2]];
                seg.sequenceSegment = segUpd.sequenceSegment;
                for (Synapse syn : seg.synapses) {
                    if (syn == null) break;
                    boolean flag = false;
                    for(Synapse s: segUpd.activeSynapses ){
                        if (s == null) break;
                        if (s == syn)
                            flag = true;
                    }

                    if (flag) {
                        if (positiveReinforcement)
                            syn.permanence += regions[regInd].permanenceInc;
                        else
                            syn.permanence -= regions[regInd].permanenceDec;
                    } else {
                        if (positiveReinforcement)
                            syn.permanence -= regions[regInd].permanenceDec;
                        else
                            syn.permanence += regions[regInd].permanenceInc;
                    }
                }
                for (Synapse syn : segUpd.activeSynapses) {
                    if (syn == null) break;
                    if (!seg.segmentContainsSynapse(syn)) {
                        seg.synapses[seg.synapsesNum] = syn;
                        seg.synapsesNum++;
                    }
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////

    // main phases

    /*
    Еще до того как получить любые входные данные, регион должен быть проинициализирован, а для этого надо создать начальный список потенциальных синапсов
    для каждой колонки.
    Он будет состоять из случайного множества входных битов, выбранных из пространства входных данных.
    Каждый входной бит будет представлен синапсом с некоторым случайным значением перманентности.
    Эти значения выбираются по двум критериям.
        Во-первых, эти случайные значения должны быть из малого диапазона около connectedPerm
    (пороговое значение – минимальное значение перманентности при котором синапс считается «действующим» («подключенным»)).
    Это позволит потенциальным синапсам стать подключенными (или отключенными) после небольшого числа обучающих итераций.
        Во-вторых, у каждой колонки есть геометрический центр ее входного региона и значения перманентности должны увеличиваться по направлению
    к этому центру (т.е. у центра колонки значения перманентности ее синапсов должны быть выше).
     */

    private Random rnd = new Random();

    public void initSynapsesDefault(int regInd, Column column) {
        for (int i = 0; i < regions[regInd].numColumns; i++) {
            int dimX = rnd.nextInt(regions[regInd].getXDim());
            int dimY = rnd.nextInt(regions[regInd].getYDim());
            double perm = regions[regInd].connectedPerm + regions[regInd].connectedPerm / 2.0 - (rnd.nextDouble() / 10.0);
            double adjustment = Math.sqrt((((column.x - dimX)) ^ 2 + ((column.y - dimY)) ^ 2) / (regions[regInd].getXDim() + regions[regInd].getYDim()));

            column.potentialSynapses[column.potentialSynapsesNum] = new Synapse(dimX, dimY, Math.max(perm - adjustment, 0.0));
            column.potentialSynapsesNum++;
        }
    }

    public void initSynapsesTest(int regInd, Column column, int numInputs, double[] permArr) {
        for (int i = 0; i < numInputs; i++) {
            int dimX = rnd.nextInt(regions[regInd].getXDim());
            int dimY = rnd.nextInt(regions[regInd].getYDim());

            column.potentialSynapses[column.potentialSynapsesNum] = new Synapse(dimX, dimY, permArr[i]);
            column.potentialSynapsesNum++;
        }
    }

    public void sInitializationDefault() {

        for (int i = 0 ; i < numRegions; i++) {
            regions[i].addColumns();
            regions[i].activeColumns = new DenseIntMatrix2D(3, regions[i].numColumns + 1); //моменты t по вертикали, индексы колонок по горизонтали
            inputBits = new BitMatrix(regions[i].getXDim(), regions[i].getYDim());
            inputXDim = regions[i].getXDim();
            inputYDim = regions[i].getYDim();

            for (Column c : regions[i].columns) {
                if (c == null) break;
                initSynapsesDefault(i, c);
            }

            updateInhibitionRadius(i);
        }
    }

    public void updateInhibitionRadius(int i){
        regions[i].inhibitionRadius = regions[i].averageReceptiveFieldSize();
    }


    public void sInitializationTest(int[] inputDim, int[] columnDim) {
        /*for (int i = 0 ; i < numRegions; i++) {
            regions[i].xDimension = columnDim[0];
            regions[i].yDimension = columnDim[1];
            regions[i].cellsPerColumn = columnDim[2];
            regions[i].connectedPerm = 0.5;
            regions[i].addColumns();

            regions[i].activeColumns = new DenseIntMatrix2D(3, regions[i].numColumns + 1); //моменты t по вертикали, индексы колонок по горизонтали
            inputBits = new BitMatrix(regions[i].yDimension, regions[i].xDimension);
            inputXDim = regions[i].xDimension;
            inputYDim = regions[i].yDimension;
        }*/
    }



    public double updateOverlapDutyCycle(int regInd, int c) {
        double value = 0.0;
        if (regions[regInd].columns[c].overlap > regions[regInd].minOverlap) {
            value = 1.0;
        }
        return (value + totalTime * regions[regInd].columns[c].overlapDutyCycle) / (totalTime + 1);
    }

    /*
    Фаза 1: Перекрытие (Overlap)
    Первая фаза вычисляет значение перекрытия каждой колонки с заданным входным вектором (данными).
    Перекрытие для каждой колонки это просто число действующих синапсов подключенных к активным входным битам,
    умноженное на фактор ускорения («агрессивности») колонки.
    Если полученное число будет меньше minOverlap, то мы устанавливаем значение перекрытия в ноль.
     */
    public void setInput2DMatrix(BitMatrix inputAtT){
        inputBits = inputAtT;

        /*
            for (int i = 0; i < inputXDim; i++){
                for (int j = 0; j <  inputYDim; j++){
                System.out.print(((inputBits.get(j, i) == false) ? 0 : 1) + " ");
            }
            System.out.print("\n");
        }*/
        //System.out.print(inputBits.get(4,0));
    }


    public void sOverlap(int regInd) {
        for(Column c: regions[regInd].columns) {
            if (c == null) break;
            c.overlap = 0.0;

            c.connectedSynapses = c.connectedSynapses();

            for (Synapse synapse : c.connectedSynapses) {
                if (synapse == null) break;
                if (input(synapse.c, synapse.i))
                    c.overlap += 1;
                //c.overlap += inputDefault(time, synapse.c, synapse.i);
            }
            if (c.overlap < c.minOverlap)
                c.overlap = 0.0;
            else
                c.overlap *= c.boost;

        }
    }

    /*
    Фаза 2: Ингибирование (подавление)
    На второй фазе вычисляется какие из колонок остаются победителями после применения взаимного подавления.
    Параметр desiredLocalActivity контролирует число колонок, которые останутся победителями.
     */
    public void sInhibition(int regInd) {
/*
       System.out.print("before sInhibition: activeColumns contains:\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 30; j++)
                System.out.print(activeColumns.get(i, j) + " ");
            System.out.print("\n");
        }
*/
        int i = 0;
        int activeColumnsAtTlength = 1;
        for(Column c : regions[regInd].columns){
            if (c == null) break;
            double minLocalActivity = regions[regInd].GetMinLocalActivity(i);
            double overlap = c.overlap;
            if (overlap > 0.0 && overlap >= minLocalActivity) {
                regions[regInd].activeColumns.setQuick(time, activeColumnsAtTlength, i);
                activeColumnsAtTlength++;
            }
            i++;
        }
        regions[regInd].activeColumns.setQuick(time, 0, activeColumnsAtTlength - 1);
/*
            System.out.print("after sInhibition: activeColumns contains:\n");
            for (int j = 0; j < 3; j++) {
                for (int q = 0; q < 30; q++)
                    System.out.print(activeColumns.get(j, q) + " ");
                System.out.print("\n");
            }
*/
    }

    /*
    Фаза 3:
        Здесь обновляются значения перманентности всех синапсов, если это необходимо, равно как и фактор ускорения («агрессивности»)
    колонки вместе с ее радиусом подавления.
        Для победивших колонок, если их синапс был активен, его значение перманентности увеличивается,
    а иначе – уменьшается. Значения перманентности ограничены промежутком от 0.0 до 1.0 .
     */
    public void sLearning(int regInd) {
        for (int c = 1 ; c <= regions[regInd].activeColumns.viewRow(time).getQuick(0); c++){
           for (Synapse s: regions[regInd].columns[regions[regInd].activeColumns.viewRow(time).getQuick(c)].potentialSynapses){
                if (s == null) break;
                if (input(s.c, s.i)) {
                    s.permanence += regions[regInd].permanenceInc;
                    s.permanence = Math.min(s.permanence, 1.0);
                } else {
                    s.permanence -= regions[regInd].permanenceDec;
                    s.permanence = Math.max(s.permanence, 0.0);
                }
            }
        }

    /*
        Имеется два различных механизма ускорения помогающих колонке обучать свои соединения (связи).
        Если колонка не побеждает достаточно долго (что измеряется в activeDutyCycle), то увеличивается ее общий фактор ускорения.
        Альтернативно, если подключенные синапсы колонки плохо перекрываются с любыми входными данными достаточно долго (что измеряется
        в overlapDutyCycle), увеличиваются их значения перманентности.
    */
        int i = 0;
        for (Column c : regions[regInd].columns) {
            if (c == null) break;
            c.minDutyCycle = 0.01 * regions[regInd].maxDutyCycle(regions[regInd].neighbours(i));
            c.activeDutyCycle = updateActiveDutyCycle(regInd, i);
            c.boost = c.boostFunction();
            c.overlapDutyCycle = updateOverlapDutyCycle(regInd, i);

            if (c.overlapDutyCycle < c.minDutyCycle) {
                c.increasePermanences(0.1 * regions[regInd].connectedPerm);
            }
            i++;
        }

        updateInhibitionRadius(regInd);
    }

    /*
    Фаза 1:
    На первой фазе вычисляются активные состояния (значения activeState) для каждой клетки из победивших колонок.
    Из этих колонок далее выбирается одна клетка на колонку для обучения (learnState).
    Логика здесь следующая: если текущий прямой вход снизу был предсказан какой-либо из клеток (т.е. ее параметр predictiveState был равен 1
    благодаря какому-то ее латеральному сегменту), тогда эти клетки становятся активными .
    Если этот сегмент стал активным из-за клеток выбранных для обучения (learnState ==1), тогда такая клетка также выбирается для обучения.
    Если же текущий прямой вход снизу не был предсказан, тогда все клетки становятся активными и кроме того, клетка, лучше всего соответствующая
    входным данным, выбирается для обучения, причем ей добавляется новый латеральный дендритный сегмент.
     */
    public void tCellStates(int regInd) {
        for (int c = 1 ; c <= regions[regInd].activeColumns.viewRow(time).getQuick(0); c++){
            boolean buPredicted = false;
            boolean lcChosen = false;

            int ind=0;
            int colInd = regions[regInd].activeColumns.viewRow(time).getQuick(c);
            for (Cell i: regions[regInd].columns[colInd].cells) {
                if (i == null) break;
                if (i.predictiveState.get(time - 1 > 0 ? time - 1 : 0)) {

                    int[] s = getActiveSegment(regInd, colInd, ind, time - 1 > 0 ? time - 1 : 0, State.active);
                    if (s[2] >= 0 && regions[regInd].columns[s[0]].cells[s[1]].dendriteSegments[s[2]].sequenceSegment) {

                        buPredicted = true;
                        i.activeState.put(time, true);

                        if (segmentActive(regInd, regions[regInd].columns[s[0]].cells[s[1]].dendriteSegments[s[2]], time - 1 > 0 ? time - 1 : 0, State.learn)) {
                            lcChosen = true;
                            i.learnState.put(time, true);
                        }
                    }
                }
                ind++;
            }

            if (!buPredicted) {
                for (Cell i:  regions[regInd].columns[colInd].cells) {
                    if (i== null) break;
                    i.activeState.put(time, true);
                }
            }

            if (!lcChosen) {
                int[] lc = getBestMatchingCell(regInd, colInd, time - 1 > 0 ? time - 1 : 0);
                regions[regInd].columns[colInd].cells[lc[1]].learnState.put(time, true);
                if (time - 1 >= 0) {
                    SegmentUpdate sUpdate = getSegmentActiveSynapses(regInd,colInd, lc[1], time - 1, lc[2], true);
                    sUpdate.sequenceSegment = true;
                    Cell q = regions[regInd].columns[colInd].cells[lc[1]];
                    q.segmentUpdateList[q.segmentUpdateListNum] = sUpdate;
                    q.segmentUpdateListNum++;
                    regions[regInd].columns[colInd].cells[lc[1]] = q ;
                }
            }
        }
    }

    /*
        Фаза 2:
    Вторая фаза вычисляет состояния предсказания (предчувствия активации) для каждой клетки.
    Каждая клетка включает свое состояние предчувствия (параметр predictiveState), если любой из ее латеральных дендритных сегментов становится активным,
     т.е. достаточное число его горизонтальных (боковых, латеральных) соединений становятся активными благодаря прямому входу.
     В этом случае клетка ставит в очередь на отложенное исполнение следующий ряд своих изменений:
        а) усиление активных сейчас латеральных сегментов и
        б) усиление сегментов которые могли бы предсказать данную активацию, т.е. сегментов которые соответствуют (возможно, пока слабо)
        активности на предыдущем временном шаге.
    */
    public void tPredictiveStates(int regInd) {
        int ind_c = 0;
        for (Column c: regions[regInd].columns) {
            if (c == null) break;
            int ind_i = 0;
            for (Cell i : c.cells){
                if (i == null) break;
                for (int s = 0; s < i.dendriteSegmentsNum; s++) {
                    if (segmentActive(regInd, i.dendriteSegments[s], time, State.active)) {
                        i.predictiveState.put(time, true);
                        //a
                        SegmentUpdate activeUpdate = getSegmentActiveSynapses(regInd, ind_c, ind_i, time, s, false);
                        i.segmentUpdateList[i.segmentUpdateListNum] = activeUpdate;
                        i.segmentUpdateListNum++;
                        //б
                        int[] predSegment = getBestMatchingSegment(regInd, ind_c, ind_i, time - 1 > 0 ? time - 1 : 0);
                        SegmentUpdate predUpdate = getSegmentActiveSynapses(regInd, ind_c, ind_i, time - 1 > 0 ? time - 1 : 0, predSegment[2], true);
                        i.segmentUpdateList[i.segmentUpdateListNum] = predUpdate;
                        i.segmentUpdateListNum++;
                    }
                }
                ind_i++;
            }
            ind_c++;
        }
    }

    /*
        Фаза 3:
    Третья и последняя фаза занимается обучением.
    В этой фазе происходит реальное обновление сегментов (которое было поставлено в очередь на исполнение)
    в том случае если колонка клетки активирована прямым входом и эта клетка выбрана в качестве кандидатки для обучения .
    В противном случае, если клетка по каким-либо причинам перестала предсказывать, мы ослабляем ее латеральные сегменты
     */
    public void tLearning(int regInd) {
        for (Column c: regions[regInd].columns) {
            if (c == null) break;
            for (Cell i : c.cells) {
                if (i == null) break;
                if (i.learnState.get(time)) {
                    adaptSegments(regInd, i.segmentUpdateList, true);
                } else if (!i.predictiveState.get(time) &&
                        i.predictiveState.get(time - 1 > 0 ? time - 1 : 0)) {
                    adaptSegments(regInd, i.segmentUpdateList, false);
                }
                i.clearSegmentUpdateList();
            }
        }
    }

    public BitMatrix getColumnsMapAtT(int regInd, int t){
        BitMatrix matrix = new BitMatrix(regions[regInd].getXDim() , regions[regInd].getYDim());
        int c = 0 , r = 0;
        int len = 1;

        for (int i = 0; i < regions[regInd].numColumns ; i++)
        {
            if (i!= 0 && i % regions[regInd].getXDim() == 0) {r++; c = 0;}

            if (i == regions[regInd].activeColumns.viewRow(t).get(len) && len <= regions[regInd].activeColumns.viewRow(t).get(0)){
                matrix.put(c , r, true);
                len++;
            }
            else{
                matrix.put(c , r, false);
            }
            c++;
        }
        return matrix;
    }

    public void interactRegions(){
        for (int i = 0; i < numRegions; i++){
            sOverlap(i);
            sInhibition(i);
            sLearning(i);
            tCellStates(i);
            tPredictiveStates(i);
            tLearning(i);
            //change input matrix
            inputBits = getColumnsMapAtT(i, time);
        }
    }

    public void timestep() {
        time++;
        totalTime++;

        if (totalTime > 2) {
            time--;
            for (Region region : regions) {
                if (region == null) break;
                for (int i = 0; i < 2; i++)
                    for (int j = 0; j < region.numColumns + 1; j++) {
                        region.activeColumns.setQuick(i, j, region.activeColumns.getQuick(i + 1, j));
                    }
                for (int j = 0; j < region.numColumns + 1; j++) {
                    region.activeColumns.setQuick(2, j, 0);
                }
/*
            System.out.print("activeColumns contains 2:\n");
            for (int i = 0; i < 3; i++){
                for (int j = 0; j < 30 ; j++)
                    System.out.print(activeColumns.getQuick(i, j) + " ");
                System.out.print("\n");
            }
*/

                for (Column c : region.columns)
                    for (Cell i : c.cells) {
                        //удаляем 0-ой элемент, сдвигаем остальное
                        for (int j = 1; j < 3; j++) {
                            i.predictiveState.put(j - 1, i.predictiveState.get(j));
                            i.learnState.put(j - 1, i.learnState.get(j));
                            i.activeState.put(j - 1, i.activeState.get(j));
                        }
                        i.predictiveState.put(2, false);
                        i.learnState.put(2, false);
                        i.activeState.put(2, false);
                    }
            }
        }
/*
        for (Column c : region.columns)
            for (Cell i : c.cells) {
                i.predictiveState.add(false);
                i.learnState.add(false);
                i.activeState.add(false);
            }
   */ }
}
