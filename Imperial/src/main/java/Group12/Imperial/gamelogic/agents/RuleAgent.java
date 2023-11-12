package Group12.Imperial.gamelogic.agents;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;

public class RuleAgent implements AgentInterface, Serializable {

    @Override
    public int makeRondelChoice(GameState state) throws InterruptedException {
        
        return Rules.rondelRule(state);
    }

    @Override
    public ArrayList<ArrayList<Integer>> makeManeuverChoice(GameState state) {
        // return RandomChoice.makeRandomManeuverAction(state);
        return Rules.manueuverRule(state);
    }

    @Override
    public int makeFactoryChoice(GameState state) {
        
        return Rules.factoryRule(state);
    }

    @Override
    public ArrayList<ArrayList<Integer>> makeImportChoice(GameState state) {
        
        return Rules.importRule(state);
    }

    @Override
    public int[] makeBondBuyChoice(GameState state, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCosts) { 
        return Rules.investorRule(state, possibleActions, associatedCosts);
    }

    @Override
    public int makeBattleChoice(GameState state, int nation, int battleLocation, ArrayList<Integer> possibleBattles) {
        int j = possibleBattles.indexOf(nation);
        if(j != -1) possibleBattles.remove(j);
        if(possibleBattles.size() == 1) return possibleBattles.get(0) + 1;

        int choice = possibleBattles.get(RandomChoice.getRandomListChoice(possibleBattles)) + 1;
        
        return choice;
    }

    @Override
    public boolean makeHostileChoice(GameState state, int locationIndex, int nationIndex, UnitType unitType) {
        
        return true;
    }
    
}
