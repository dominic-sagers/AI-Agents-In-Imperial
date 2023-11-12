package Group12.Imperial.gamelogic.agents;

import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.GameScreen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class RandomAgent implements AgentInterface, Serializable {
    private RandomChoice rand;
    Random randomObj = new Random();
    // private GameScreen gameScreen = new gameScreen;

    @Override
    public int makeRondelChoice(GameState state) {
        return RandomChoice.makeRandomRondelChoice(state);
    }

    @Override
    public ArrayList<ArrayList<Integer>> makeManeuverChoice(GameState state) {
        return RandomChoice.makeRandomManeuverAction(state);
    }

    @Override
    public int makeFactoryChoice(GameState state){
        return RandomChoice.makeRandomFactoryChoice(state); 
    }

    @Override
    public ArrayList<ArrayList<Integer>> makeImportChoice(GameState state) {
        return RandomChoice.makeRandomImportChoice(state);
    }

    @Override
    public int[] makeBondBuyChoice(GameState state, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCosts) {
        return RandomChoice.makeInvestorChoice(state, possibleActions, associatedCosts);
    }

    @Override
    public boolean makeHostileChoice(GameState state, int locationIndex, int nationIndex, UnitType unitType) {
        return randomObj.nextBoolean();
    }

    @Override
    public int makeBattleChoice(GameState state, int nation, int battleLocation, ArrayList<Integer> possibleBattles) {
        if(randomObj.nextBoolean()){
            return 0;
        } else {
            int j = possibleBattles.indexOf(nation);
            if(j != -1) possibleBattles.remove(j);
            if(possibleBattles.size() == 1) return possibleBattles.get(0) + 1;

            int choice = possibleBattles.get(RandomChoice.getRandomListChoice(possibleBattles)) + 1;
            
            return choice;
        }
    }
    
}
