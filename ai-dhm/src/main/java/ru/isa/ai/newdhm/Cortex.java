package ru.isa.ai.newdhm;

import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix2D;
//import cern.colt.matrix.tint.impl.SparseIntMatrix1D;
import ru.isa.ai.newdhm.applet.ExtensionGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import cern.colt.matrix.tint.IntMatrix2D;
import cern.colt.matrix.tbit.BitMatrix;
//import cern.colt.matrix.tint.impl.SparseIntMatrix2D;
import cern.colt.matrix.tint.impl.SparseIntMatrix3D;


public class Cortex {
    public int time = 0;
    public int totalTime = 0;

    public int[] inputDimensions;
    public int numInputs = 1;

    // Список всех колонок
    public Region region = new Region();

    //+ TODO AP: в максимуме случаев нужно стараться исопльзовать либо массивы intов (именно примитивных), либо оптимизированные специальные классы типа Sparse...
    public SparseIntMatrix3D inputBits;

    /*Список индексов колонок – победителей благодаря прямым
    входным данным. (Выход пространственного группировщика)
   */
    // + TODO AP: нужен, конечно,  IntMatrix2D или Sparse...
    public IntMatrix2D activeColumns;

    public enum State {
        active,
        learn
    }

    private Random r = new Random();
    /////////////////////////////////////////////////////////////////////////
    //  Реализация
    /////////////////////////////////////////////////////////////////////////

    public Cortex() {

    }

    /////////////////////////////////////////////////////////////////////////

    // + TODO AP: везде нужно испоьзовать простые int, double и т.п. вместо объектных оберток - это быстрее
    /*
    Вход для данного уровня в момент времени t. input(t, j) = 1
если j-ый бит входа = 1.
     */
    public int input(int t, int j, int k) {
        // TODO AP: нужно обязательно отделить GUI от бизнес-логики, т.е. от алгоритмов,
        // TODO AP: нужно постараться сохранить идеологию класса ru.isa.ai.dhm.poolers.SpatialPooler - оставить только то, что нужно для реализации алгоритмов - все остальное, GUI генерацию дефолтного входа и т.п. - вынести наржу, в другие классы
        if (ExtensionGUI.Input == null)
            return t % 2 > 0 ? rnd.nextInt(2) : Math.sin(j + k + totalTime) > 0 ? 1 : 0;
        else {
            byte[] buffer = ExtensionGUI.Input;
            int l = buffer.length;
            int width = l / region.xDimension;
            int height = 256 / region.yDimension;
            int amount = 0;
            for (int i = j * width; i < (j + 1) * width; i++) {
                if ((k + 1) * height - 128 < buffer[j] && buffer[j] > k * height - 128)
                    amount++;
            }
            return amount > width / 10 ? 1 : 0;
        }
    }

    /*
    Вычисляет интервальное среднее того, как часто колонка c была активной
     после подавления.
     */
    public double updateActiveDutyCycle(int c) {
        double value = 0.0;
        IntMatrix1D col = activeColumns.viewColumn(time);
        for (int  ind = 0; ind < col.size(); ind++ ) {
            if (c == ind) {
                value = 1.0;
                break;
            }
        }
        /*for(int idx: activeColumns.get(time))   //SparseIntMatrix2D
            if (c.equals(idx)) {
                value = 1.0;
                break;
            }*/
       // return (value + totalTime.floatValue() * region.columns.get(c).activeDutyCycle) / (totalTime.floatValue() + 1.0);
        return (value + totalTime * region.columns[c].activeDutyCycle) / (totalTime + 1.0);
    }

    ////////////////
    // + TODO AP: массивы boolean - дорого - надо испольовать BitSet и т.п.
    BitMatrix get2DcolsANDcellsAtT(State state, int t) {
        BitMatrix list = new BitMatrix(region.numColumns,region.cellsPerColumn);
        for (int col = 0; col < region.numColumns; col++) {
            int ind_i = 0;
            for (Cell i : region.columns[col].cells) {
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
    public boolean segmentActive(Segment s, int t, State state) {

        BitMatrix list = new BitMatrix(region.numColumns, region.cellsPerColumn);
        list = get2DcolsANDcellsAtT(state, t);
        int counter = 0;
        for (Synapse syn : s.synapses) {
            if (syn == null) break;
            if (list.get(syn.c, syn.i) && syn.permanence > region.connectedPerm) {
                counter++;
            }
        }
        return counter > region.activationThreshold;
    }


    //////////////////
    private int firstOccurrenceOfSegment(int c, int i, Segment seg){
        boolean flag = false;
        int ind = 0;
        while(!flag && ind < region.columns[c].cells[i].dendriteSegmentsNum ){
            if (region.columns[c].cells[i].dendriteSegments[ind] == seg)
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
    public int[] getActiveSegment(int c, int i, int t, State state) {
        // +TODO AP: везде надо использовать массивы, где это возможно, вместо списков
        Segment[] activeSegments = new Segment[region.columns[c].cells[i].dendriteSegmentsNum];
        int length = 0;
        for (Segment segment : region.columns[c].cells[i].dendriteSegments) {
            if (segmentActive(segment, t, state)) {
                activeSegments[length] = segment;
                //activeSegments.add(segment);
                length++;
            }
        }

        //if (activeSegments.size() == 1) {
        if (i == 1) {
            //return new int[]{c, i, region.columns.get(c).cells[i].dendriteSegments.indexOf(activeSegments[0])};
            return new int[]{c, i, firstOccurrenceOfSegment(c, i, activeSegments[0])};
        } else {
            for (Segment seg : activeSegments) {
                if (seg.sequenceSegment)
                    return new int[]{c, i,firstOccurrenceOfSegment(c,i,seg) };

            }

            BitMatrix list = new BitMatrix(region.numColumns, region.cellsPerColumn);
            list = get2DcolsANDcellsAtT(state, t);
            int maxActivity = 0;
            int result = -1;
            for (int j = 0; j < length; j++) {
                int counter = 0;
                for (Synapse syn : activeSegments[j].synapses) {
                    if (list.get(syn.c, syn.i) && syn.permanence > region.connectedPerm) {
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
    public int[] getBestMatchingSegment(int c, int i, int t) {

        BitMatrix list = new BitMatrix(region.numColumns, region.cellsPerColumn);
        list = get2DcolsANDcellsAtT(State.active, t);
        int maxActivity = 0;
        int result = -1;
        for (int j = 0; j < region.columns[c].cells[i].dendriteSegmentsNum; j++) {
            int counter = 0;
            Segment segment = region.columns[c].cells[i].dendriteSegments[j];

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
        return maxActivity > region.minThreshold ? new int[]{c, i, result} : new int[]{c, i, -1};
    }

    /*
    Для данной колонки возвращает клетку с самым соответствующим входу
    сегментом (как это определено выше). Если такой клетки нет, то
    возвращается клетка с минимальным числом сегментов.
     */
    public int[] getBestMatchingCell(int c, int t) {
        int minSegments = 0;
        int cellIndex = -1;
        int minSegmentsCellIndex = -1;

        BitMatrix list = new BitMatrix(region.numColumns, region.cellsPerColumn);
        list =  get2DcolsANDcellsAtT(State.active, t);
        int maxActivity = 0;
        int result = -1;

        for (int i = 0; i < region.cellsPerColumn; i++) {
            for (int j = 0; j < region.columns[c].cells[i].dendriteSegmentsNum; j++) {
                int counter = 0;
                Segment segment = region.columns[c].cells[i].dendriteSegments[j];

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
            if (minSegments == 0 || minSegments > region.columns[c].cells[i].dendriteSegmentsNum) {
                minSegments = region.columns[c].cells[i].dendriteSegmentsNum;
                minSegmentsCellIndex = i;
            }
        }
        return maxActivity > region.minThreshold ? new int[]{c, cellIndex, result} : new int[]{c, minSegmentsCellIndex, -1};
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
    public SegmentUpdate getSegmentActiveSynapses(int c, int i, int t, int s, boolean newSynapses) {
        Synapse[] activeSynapses = new Synapse[1000];
        int length = 0;
        if (s >= 0) {
            for (Synapse syn : region.columns[c].cells[i].dendriteSegments[s].synapses) {
                if (syn == null) break;
                if (region.columns[syn.c].cells[syn.i].activeState.get(t)) {
                    activeSynapses[length] = syn;
                    length++;
                }
            }
        }
        if (newSynapses) {
            //Random r = new Random();
            List<int[]> learningCells = new ArrayList<>();
            //SparseIntMatrix2D learningCells; ///??
            for (int j = 0; j < region.numColumns; j++) {
                for (int k = 0; k < region.cellsPerColumn; k++) {
                    if (region.columns[j].cells[k].learnState.get(t) && !(c == j && i == k)) {
                        learningCells.add(new int[]{j, k});
                    }
                }
            }/*
            for (int k = 0; k < region.newSynapseCount - activeSynapses.size(); k++) {
                int[] idx;
                idx = learningCells.get(r.nextInt(learningCells.size()));
                activeSynapses.add(new Synapse(idx[0], idx[1], region.initialPerm));
            }*/
            for (int k = 0; k < region.newSynapseCount - length; k++) {
                int[] idx;
                idx = learningCells.get(r.nextInt(learningCells.size()));
                activeSynapses[length] = new Synapse(idx[0], idx[1], region.initialPerm);
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
    public void adaptSegments(SegmentUpdate[] segmentList, boolean positiveReinforcement) {
        for (SegmentUpdate segUpd: segmentList) {
            if (segUpd == null) break;
            // System.out.print(segUpd.segmentIndex[2] + "\r\n");
            if (segUpd.segmentIndex[2] < 0) {
                Segment newSegment = new Segment();
                for (Synapse syn : segUpd.activeSynapses) {
                    if (syn == null) break;
                    newSegment.synapses[newSegment.synapsesNum] = syn;
                    newSegment.synapsesNum++;
                }
                newSegment.sequenceSegment = segUpd.sequenceSegment;
                Cell i = region.columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]];
                i.dendriteSegments[i.dendriteSegmentsNum] = newSegment;
                i.dendriteSegmentsNum++;
                /*
                int len = region.columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]].dendriteSegmentsNum;
                region.columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]].dendriteSegments[len] = newSegment;
                region.columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]].dendriteSegmentsNum++;
                */
            } else {
                Segment seg = region.columns[segUpd.segmentIndex[0]].cells[segUpd.segmentIndex[1]].dendriteSegments[segUpd.segmentIndex[2]];
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
                            syn.permanence += region.permanenceInc;
                        else
                            syn.permanence -= region.permanenceDec;
                    } else {
                        if (positiveReinforcement)
                            syn.permanence -= region.permanenceDec;
                        else
                            syn.permanence += region.permanenceInc;
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

    Random rnd = new Random();

    void initSynapsesDefault(Column column) {
        for (int i = 0; i < region.numColumns; i++) {
            int dimX = rnd.nextInt(region.xDimension);
            int dimY = rnd.nextInt(region.yDimension);
            double perm = region.connectedPerm + region.connectedPerm / 2.0 - (rnd.nextDouble() / 10.0);
            double adjustment = Math.sqrt((((column.x - dimX)) ^ 2 + ((column.y - dimY)) ^ 2) / (region.xDimension + region.yDimension));

            column.potentialSynapses[column.potentialSynapsesNum] = new Synapse(dimX, dimY, Math.max(perm - adjustment, 0.0));
            column.potentialSynapsesNum++;
        }
/*
        int i = 0;
        while (column.potentialSynapses[i] != null){
            i++;
        }
        System.out.print(i);*/
    }

    // +TODO AP: все методы - с маленькой буквы!
    public void sInitialization() {
 /*
       this.inputDimensions = new int[inputDimensions.length];
        numInputs = 1;
        for (int i = 0; i < inputDimensions.length; i++) {
            numInputs *= inputDimensions[i];
            this.inputDimensions[i] = inputDimensions[i];
        }
        this.region.cellsPerColumn = regionDimensions[0];
        this.region.xDimension = regionDimensions[1];
        this.region.yDimension = regionDimensions[2];

        region.numColumns = region.xDimension * region.yDimension;
*/
        int ind = 0;
        for (int i = 0; i < region.xDimension; i++) {
            for (int j = 0; j < region.yDimension; j++) {
                region.columns[ind] = new Column(region, i, j);
                ind++;
            }
        }

        activeColumns = new DenseIntMatrix2D(region.xDimension, region.yDimension);
        //activeColumns = new ArrayList<List<int>>();

        //inputBits = new ArrayList<int[][]>();
       // inputBits.add(new int[region.xDimension][region.yDimension]);
        inputBits = new SparseIntMatrix3D(region.cellsPerColumn,region.xDimension,region.yDimension);

        for (Column c : region.columns) {
            if (c == null) break;
            initSynapsesDefault(c);
        }

       /* for (Column c : region.columns)
            for (Cell i : c.cells) {
                i.learnState.add(false);
                i.activeState.add(false);
                i.predictiveState.add(false);
            }*/

        region.inhibitionRadius = region.averageReceptiveFieldSize();
        ///////////////////////////
        // loadProperties();
        // checkProperties();
    }

    public double updateOverlapDutyCycle(int c) {
        double value = 0.0;
        if (region.columns[c].overlap > region.minOverlap) {
            value = 1.0;
        }
        return (value + totalTime * region.columns[c].overlapDutyCycle) / (totalTime + 1);
    }

    /*
    Фаза 1: Перекрытие (Overlap)
    Первая фаза вычисляет значение перекрытия каждой колонки с заданным входным вектором (данными).
    Перекрытие для каждой колонки это просто число действующих синапсов подключенных к активным входным битам,
    умноженное на фактор ускорения («агрессивности») колонки.
    Если полученное число будет меньше minOverlap, то мы устанавливаем значение перекрытия в ноль.
     */

    public void sOverlap() {
        for(Column c: region.columns) {
            if (c == null) break;
            c.overlap = 0.0;

            c.connectedSynapses = c.connectedSynapses();

            for (Synapse synapse : c.connectedSynapses) {
                if (synapse == null) break;
                c.overlap += input(time, synapse.c, synapse.i);
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
    public void sInhibition() {
        int i = 0;
        for(Column c : region.columns){
            if (c == null) break;
            double minLocalActivity = region.GetMinLocalActivity(i);
            double overlap = c.overlap;
            if (overlap > 0.0 && overlap >= minLocalActivity) {
                activeColumns.setQuick(c.x,c.y,i);
            }
            i++;
        }
    }

    /*
    Фаза 3:
        Здесь обновляются значения перманентности всех синапсов, если это необходимо, равно как и фактор ускорения («агрессивности»)
    колонки вместе с ее радиусом подавления.
        Для победивших колонок, если их синапс был активен, его значение перманентности увеличивается,
    а иначе – уменьшается. Значения перманентности ограничены промежутком от 0.0 до 1.0 .
     */
    public void sLearning() {
        //for (int c : activeColumns.get(time)) {
        for (int c = 0 ; c < activeColumns.viewColumn(time).size(); c++){
            for (Synapse s : region.columns[c].potentialSynapses) {
                if (s == null) break;
                if (input(time, s.c, s.i) > 0) {
                    s.permanence += region.permanenceInc;
                    s.permanence = Math.min(s.permanence, 1.0);
                } else {
                    s.permanence -= region.permanenceDec;
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
        for (Column c : region.columns) {
            if (c == null) break;
            c.minDutyCycle = 0.01 * region.maxDutyCycle(region.neighbours(i));
            c.activeDutyCycle = updateActiveDutyCycle(i);
            c.boost = c.boostFunction();
            c.overlapDutyCycle = updateOverlapDutyCycle(i);

            if (c.overlapDutyCycle < c.minDutyCycle) {
                c.increasePermanences(0.1 * region.connectedPerm);
            }
            i++;
        }

        region.inhibitionRadius = region.averageReceptiveFieldSize();
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
    public void tCellStates() {
        for (int c = 0 ; c < activeColumns.viewColumn(time).size(); c++){
            boolean buPredicted = false;
            boolean lcChosen = false;

            int ind=0;
            for (Cell i: region.columns[c].cells) {
                if (i == null) break;
                if (i.predictiveState.get(time - 1 > 0 ? time - 1 : 0)) {

                    int[] s = getActiveSegment(c, ind, time - 1 > 0 ? time - 1 : 0, State.active);
                    if (s[2] >= 0 && region.columns[s[0]].cells[s[1]].dendriteSegments[s[2]].sequenceSegment) {

                        buPredicted = true;
                        i.activeState.put(time, true);

                        if (segmentActive(region.columns[s[0]].cells[s[1]].dendriteSegments[s[2]], time - 1 > 0 ? time - 1 : 0, State.learn)) {
                            lcChosen = true;
                            i.learnState.put(time, true);
                        }
                    }
                }
                ind++;
            }

            if (!buPredicted) {
                for (Cell i:  region.columns[c].cells) {
                    if (i== null) break;
                    i.activeState.put(time, true);
                }
            }

            if (!lcChosen) {
                int[] lc = getBestMatchingCell(c, time - 1 > 0 ? time - 1 : 0);
                Cell i = region.columns[c].cells[lc[1]];
                i.learnState.put(time, true);
                if (time - 1 >= 0) {
                    SegmentUpdate sUpdate = getSegmentActiveSynapses(c, lc[1], time - 1, lc[2], true);
                    sUpdate.sequenceSegment = true;
                    i.segmentUpdateList[i.segmentUpdateListNum] = sUpdate;
                    i.segmentUpdateListNum++;
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
    public void tPredictiveStates() {
        int ind_c = 0;
        for (Column c: region.columns) {
            if (c == null) break;
            int ind_i = 0;
            for (Cell i : c.cells){
                if (i == null) break;
                for (int s = 0; s < i.dendriteSegmentsNum; s++) {
                    if (segmentActive(i.dendriteSegments[s], time, State.active)) {
                        i.predictiveState.put(time, true);
                        //a
                        SegmentUpdate activeUpdate = getSegmentActiveSynapses(ind_c, ind_i, time, s, false);
                        i.segmentUpdateList[i.segmentUpdateListNum] = activeUpdate;
                        i.segmentUpdateListNum++;
                        //б
                        int[] predSegment = getBestMatchingSegment(ind_c, ind_i, time - 1 > 0 ? time - 1 : 0);
                        SegmentUpdate predUpdate = getSegmentActiveSynapses(ind_c, ind_i, time - 1 > 0 ? time - 1 : 0, predSegment[2], true);
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
    public void tLearning() {
        for (Column c: region.columns) {
            if (c == null) break;
            for (Cell i : c.cells) {
                if (i == null) break;
                if (i.learnState.get(time)) {
                    adaptSegments(i.segmentUpdateList, true);
                } else if (!i.predictiveState.get(time) &&
                        i.predictiveState.get(time - 1 > 0 ? time - 1 : 0)) {
                    adaptSegments(i.segmentUpdateList, false);
                }
                i.clearSegmentUpdateList();
            }
        }
    }

    public void timestep() {
        time++;
        totalTime++;

        if (totalTime > 2) {
            time--;
            //activeColumns.remove(time - 2);

         /*   for (Column c : region.columns)
                for (Cell i : c.cells) {
                    i.predictiveState.remove(time - 2);
                    i.learnState.remove(time - 2);
                    i.activeState.remove(time - 2);
                }*/
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
