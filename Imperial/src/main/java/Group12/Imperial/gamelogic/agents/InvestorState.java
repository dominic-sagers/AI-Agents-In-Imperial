package Group12.Imperial.gamelogic.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.actions.Investor;
import Group12.Imperial.gamelogic.agents.tneuralnet.math.Vec;
import Group12.Imperial.gamelogic.gameboard.Bond;

public class InvestorState {
    public Bond[] allBonds;
    private Vec data;
    public GameState gameState;
    
    public InvestorState(GameState fromState) {
        this.gameState = GameState.getCloneGameState(fromState);
        allBonds = new Bond[48];
        double[] vector = new double[54];
        ArrayList<Bond[]> bonds = fromState.groupBonds();
        ArrayList<Bond> ownedBonds = fromState.getPlayers()[fromState.getCurrentPlayer()].getOwnedBonds();
        int money = fromState.getPlayers()[fromState.getCurrentPlayer()].getMoney();
        int position = 0;
        for(Bond[] bondArr : bonds) {
            for(Bond bond : bondArr) {
                allBonds[position] = bond;
                ArrayList<Integer> buyOptions = Investor.getBondBuyOptions(bond, ownedBonds, money);
                if(bond.getOwner() != null && bond.getOwner().getIndex() == fromState.getCurrentPlayer()){
                    vector[position] = 0.5;
                    position++;
                }else if(bond.getOwner() == null && buyOptions.size() == 0) {
                    vector[position] = 0;
                    position++;
                } else if(bond.getOwner() == null && buyOptions.size() > 0){
                    vector[position] = 1;
                    position++;
                } else if(bond.getOwner() != null) {
                    vector[position] = 0;
                    position++;
                }else{
                    System.out.println("Something went wrong");
                }
            }
        }
        Nation[] nations = fromState.getNations();
        int[] nationScores = new int[6];
        int max = 0;
        for(int i = 0; i < nations.length; i++){
            nationScores[i] = nations[i].getPowerPoints();
            if(nationScores[i] > max) max = nationScores[i];
        }
        for(int i = 0; i < nations.length; i++){
            if(max == 0) {
                vector[position] = 1.0;
                position++;
            } else {
                vector[position] = (double)nationScores[i]/(double)max;
                position++;
            }
            
        }
        data = new Vec(vector);
    }

    public Bond[] getBonds(){
        return allBonds;
    }
    public GameState getGameState(){
        return gameState;
    }

    public Vec getData() { return data; }

    // @Override
    // public boolean equals(Object other) {
    //     InvestorState otherState = (InvestorState) other;
    //     for (int index = 0; index < bonds.length; index++) {
    //         if (!(this.bonds[index].equals(otherState.bonds[index]))) {
    //             return false;
    //         }
    //     }
    //     return true;
    // }
    // @Override
    // public int hashCode() {
    //     return Objects.hash(Arrays.hashCode(bonds));
    // }
}
