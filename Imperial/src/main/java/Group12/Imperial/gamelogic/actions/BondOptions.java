package Group12.Imperial.gamelogic.actions;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Bond;

public class BondOptions implements Serializable {

    public ArrayList<ArrayList<Integer>> associatedCost;
    public ArrayList<Bond> possibleActions;

    public BondOptions(ArrayList<Bond> possibleActions,  ArrayList<ArrayList<Integer>> associatedCost) {
        this.possibleActions = possibleActions;
        this.associatedCost = associatedCost;
    }
}
