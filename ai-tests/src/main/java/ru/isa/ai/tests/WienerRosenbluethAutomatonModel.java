package ru.isa.ai.tests;

/**
 * Author: Aleksandr Panov
 * Date: 25.05.12
 * Time: 14:26
 */
public class WienerRosenbluethAutomatonModel implements AutomatonModel<WienerRosenbluethAutomatonModel.WRAutomatonState> {
    // Constants
    private int tauE = 5;
    private int tauR = 7;
    private double g = 0.4;
    private double h = 3;
    private int sizeN;
    private int sizeM;

    // i= x, j = y
    private WRAutomatonState[][] elements;
    private int t = 0;

    public WienerRosenbluethAutomatonModel(int sizeN, int sizeM) {
        this.sizeN = sizeN;
        this.sizeM = sizeM;
        elements = new WRAutomatonState[sizeN][sizeM];
        //setTowWavesInitial();
        //setSemiWaveInitial();
        setEmptyInitial();
    }

    private void setEmptyInitial() {
        for (int i = 0; i < sizeN; i++) {
            for (int j = 0; j < sizeM; j++) {
                elements[j][i] = new WRAutomatonState(i, j, 0, 0);
            }
        }
    }

    private void setSemiWaveInitial() {
        for (int i = 0; i < sizeN; i++) {
            for (int j = 0; j < sizeM; j++) {
                int phi =0;
                if(i <= sizeN/2 && i>sizeN/3 && j >= sizeM/2){
                    phi = (getStateCount()-1)*(i-sizeN/3)/(sizeN/6);
                }

                elements[j][i] = new WRAutomatonState(i, j, phi, 0);
            }
        }
    }

    private void setTowWavesInitial() {
        for (int i = 0; i < sizeN; i++) {
            for (int j = 0; j < sizeM; j++) {
                int phi =0;
                if(i > sizeN/3 && j >= sizeM/6 && j<= sizeM/4){
                    phi = getStateCount()/2;
                }
                if(i <= sizeN/3 && j >= sizeM/6 && j< 5*sizeM/24){
                    phi = getStateCount()/3;
                }
                if(i <= sizeN/3 && j >= 5*sizeM/24 && j<= sizeM/3){
                    phi = getStateCount()/2;
                }
                if(i > sizeN/3 && j > sizeM/4 && j<= sizeM/3){
                    phi = getStateCount()/3;
                }

                elements[j][i] = new WRAutomatonState(i, j, phi, 0);
            }
        }
    }

    @Override
    public void tick() {
        for (int j = 0; j < sizeN; j++) {
            for (int i = 0; i < sizeM; i++) {
                elements[j][i] = calculateNextState(elements[j][i]);
            }
        }
        // reset element's memory
        for (int j = 0; j < sizeN; j++) {
            for (int i = 0; i < sizeM; i++) {
                elements[j][i].reset();
            }
        }
        t++;
    }

    @Override
    public WRAutomatonState calculateNextState(WRAutomatonState currentState) {
        double summJ = 0;
        int i = currentState.getI(), j = currentState.getJ();
        for (int k = -i; k < sizeN - i; k++) {
            for (int l = -j; l < sizeM - j; l++) {
                if (k != 0 || l != 0) {
                    summJ += neighborsWeight(currentState, k, l) * calculateJ(elements[j + l][i + k]);
                }
            }
        }
        double newU = g * currentState.getU() + summJ;
        int newPhi = 0;
        if (currentState.getPhi() > 0 && currentState.getPhi() < tauE + tauR) {
            newPhi = currentState.getPhi() + 1;
        } else if (currentState.getPhi() == 0 && newU < h) {
            newPhi = 0;
        } else if (currentState.getPhi() == 0 && newU >= h) {
            newPhi = 1;
        }
        currentState.setPhi(newPhi);
        currentState.setU(newU);
        return currentState;
    }

    protected double neighborsWeight(WRAutomatonState state, int distanceX, int distanceY) {
        if (distanceX <= 1 && distanceX >= -1 && distanceY <= 1 && distanceY >= -1) {
            return 1;
        } else {
            return 0;
        }
    }

    protected double calculateJ(WRAutomatonState state) {
        // use memorized values
        if (state.getPrevPhi() > 0 && state.getPrevPhi() <= tauE) {
            return 1;
        } else {
            return 0;
        }
    }

    public WRAutomatonState getElement(int i, int j) {
        return elements[j][i];
    }

    public void setElement(WRAutomatonState element, int i, int j) {
        elements[j][i] = element;
    }

    public void setElement(int phi, double u, int i, int j) {
        elements[j][i] = new WRAutomatonState(i, j, phi, u);
    }

    public int getStateCount() {
        return tauE + tauR + 1;
    }

    public int getT() {
        return t;
    }

    public class WRAutomatonState extends AutomatonState {
        private int phi;
        private double u;

        private int prevPhi;
        private double prevU;

        public WRAutomatonState(int i, int j, int phi, double u) {
            super(i, j);
            this.phi = phi;
            this.u = u;
            this.prevPhi = phi;
            this.prevU = u;
        }

        public int getPhi() {
            return phi;
        }

        public void setPhi(int phi) {
            this.prevPhi = this.phi;
            this.phi = phi;
        }

        public double getU() {
            return u;
        }

        public void setU(double u) {
            this.prevU = this.u;
            this.u = u;
        }

        public int getPrevPhi() {
            return prevPhi;
        }

        public double getPrevU() {
            return prevU;
        }

        public void reset() {
            prevU = u;
            prevPhi = phi;
        }
    }

}
