package Group12.Imperial.gamelogic.gameboard;

import java.io.Serializable;

public class TaxChart implements Serializable{
    
    private int[] taxChartNationPos;

    public TaxChart() {
        init();
    }

    private void init() {
        taxChartNationPos = new int[6];
    }

    public int repositionNation(int nation, int taxValue) {
        int lastPos = taxChartNationPos[nation];

        if(taxValue <= 5) {
            taxChartNationPos[nation] = 0;
        } else if(taxValue == 6) {
            taxChartNationPos[nation] = 1;
        } else if(taxValue == 7) {
            taxChartNationPos[nation] = 2;
        } else if(taxValue == 8) {
            taxChartNationPos[nation] = 3;
        } else if(taxValue == 9) {
            taxChartNationPos[nation] = 4;
        } else if(taxValue == 10) {
            taxChartNationPos[nation] = 5;
        } else if(taxValue == 11) {
            taxChartNationPos[nation] = 6;
        } else if(taxValue == 12) {
            taxChartNationPos[nation] = 7;
        } else if(taxValue == 13) {
            taxChartNationPos[nation] = 8;
        } else if(taxValue == 14) {
            taxChartNationPos[nation] = 9;
        } else if(taxValue >= 15) {
            taxChartNationPos[nation] = 10;
        }

        if(lastPos < taxChartNationPos[nation]) return (taxChartNationPos[nation]-lastPos);
        return 0;
    }

    public int getNationPosition(int nation) { return taxChartNationPos[nation]; }
}
