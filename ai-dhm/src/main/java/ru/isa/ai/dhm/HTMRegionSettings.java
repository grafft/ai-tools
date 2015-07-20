package ru.isa.ai.dhm;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 12:02
 */
// настройки региона
public final class HTMRegionSettings {

    public HTMRegionSettings(int id)
    {
        this.id=id;
    }
    final public int id;

    public int xDimension = 30; // ширина региона (в колонках)
    public int yDimension = 30; // высота региона (в колонках)
    public int xInput = 20;//100; // ширина входного слоя (в сигналах)
    public int yInput = 10;//100; // высота входного слоя (в сигналах)
    /**
     * This parameter deteremines the extent of the
     * input that each column can potentially be connected to. This
     * can be thought of as the input bits that are visible to each
     * column, or a 'receptive field' of the field of vision. A large
     * enough value will result in global coverage, meaning
     * that each column can potentially be connected to every input
     * bit. This parameter defines a square (or hyper square) area: a
     * column will have a max square potential pool with sides of
     * length (2 * potentialRadius + 1).
     */
    public int potentialRadius = 16;      // TODO: сделать в зависимотси от размеров? InputWidth / ColonNumsOnXAxis
    /**
     * Число клеток в каждой из колонок.
     */
    public int cellsPerColumn = 4;
    /**
     * Максимальное значение синапсов, добавляемых сегменту при обучении.
     */
    public int newSynapseCount = 10;

    /**
     *  Параметр, контролирующий число колонок победителей.
     */
    public int desiredLocalActivity = 2; // TODO должен быть меньше чем число колонок
    /**
     * Минимальное число активных входов колонки для ее участия в шаге подавления.
     */
    public int minOverlap = 0;
    /**
     * Если значение перманентности синапса больше этого значения, то он ситается подключенным.
     */
    public double connectedPerm = 0.1;
    /**
     * Количество значений перманентности синапсов, которые были увелечины при обучении.
     */
    public double permanenceInc = 0.1;
    /**
     * Количество значений перманентности синапсов, которые были уменьшены при обучении.
     */
    public double permanenceDec = 0.01;
    /**
     * Порог активации для сегмента. Если число активных подключенных синапсов большем этого значения, данный
     * сегмент считается активным.
     */
    public double activationThreshold = 10.0;
    /**
     * Начальное значение перманентности для синапсов.
     */
    public double initialPerm = 0.1;
    /**
     * Минимальное число активных синапсов для сегмент при поиске лучшего
     */
    public double minThreshold = 4.0;

    /**
     * The percent of the inputs, within a column's
     * potential radius, that a column can be connected to. If set to
     * 1, the column will be connected to every input within its
     * potential radius. This parameter is used to give each column a
     * unique potential pool when a large potentialRadius causes
     * overlap between the columns. At initialization time we choose
     * ((2*potentialRadius + 1)^(# inputDimensions) * connectedPct)
     * input bits to comprise the column's potential pool.
     */
    public double connectedPct = 1;

    /**
     * Длина периода подсчета рабочих циклов
     */
    public int dutyCyclePeriod = 1000;
    public double maxBoost = 10.0;
    public double initConnectedPct = 0.5;
    public double stimulusInc;                  //TODO: негде не загружается!
    public int initialInhibitionRadius = 10;

    @Override
    public int hashCode() {
        return  id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HTMRegionSettings))
            return false;
        if (obj == this)
            return true;

        return this.id==((HTMRegionSettings)obj).id;
    }

}
