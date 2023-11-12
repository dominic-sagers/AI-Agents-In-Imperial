package Group12.Imperial.gamelogic.agents;

import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;

public interface AgentInterface{
    // Returns int of new chosen position on the rondel
    public int makeRondelChoice(GameState state) throws InterruptedException;
    // Returns arraylist (all maneuvers) of arraylist of type [fromLocation, toLocation, nationIndex, unitType, amount], where unitType=0 if ship and 1 otherwise
    public ArrayList<ArrayList<Integer>> makeManeuverChoice(GameState state);
    //Returns the location index of the chosen factory location.
    public int makeFactoryChoice(GameState state);    
    // Returns arraylist (all import actions, empty when no action) of arraylist of type [locationIndex, unitType], where unitType=0 if ship and 1 otherwise
    public ArrayList<ArrayList<Integer>> makeImportChoice(GameState state);
    // Return arraylist of type [index of bond to buy in possibleActions, index of buy option for that bond index in associatedCosts]
    public int[] makeBondBuyChoice(GameState state, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCosts);
    // Returns 0 if passive and nationIndex (in this case all nationIndexes are increased by 1 so austiahungary would be 1 instead of 0, has to be decreased later again to use it) of nation to battle if it wants to battle that nation
    public int makeBattleChoice(GameState state, int nation, int battleLocation, ArrayList<Integer> possibleBattles);
    // true if hostile, false if not
    public boolean makeHostileChoice(GameState state, int locationIndex, int nationIndex, UnitType unitType);

}
