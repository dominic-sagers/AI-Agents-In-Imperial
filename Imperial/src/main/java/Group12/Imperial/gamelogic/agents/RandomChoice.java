package Group12.Imperial.gamelogic.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.Player;
import Group12.Imperial.gamelogic.actions.*;
import Group12.Imperial.gamelogic.gameboard.*;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.*;

public class RandomChoice {
    static Random random = new Random();

    /**
     * Maybe silly, but necessary function for returning a random (int) number
     * within a range. The method is inclusive of the min and max values.
     * 
     * @param min Minimum bound for the range to be considered.
     * @param max Maximum bound for the range to be considered.
     * @return Returns a random integer.
     */
    public static int randomWithinRange(int min, int max) {
        int r = random.nextInt(max - min) + min;
        return r;
    }

    /**
     * Returns a random index from a list.
     * 
     * @param list A list which you would like to return a random integer index
     * @return Returns an integer representing an index.
     */
    public static int getRandomListChoice(List list) {
        return randomWithinRange(0, list.size());
    }

    // Returns map location to build factory randomly, if they cannot afford it, or
    // if there are hostile armies in every home province, or if we already built
    // all factories,
    // we return -1
    public static int makeRandomFactoryChoice(GameState state) {
        Nation currentNation = state.getNations()[state.getCurrentNation()];// grabbing currentNation from the
                                                                            // gamestate.
        if (currentNation.getTreasury() - 5 < 0) {// returns -1 if we cannot afford a factory
            return -1;
        }
        MapLocation[] locations = state.getMap().getAllMapLocations();
        ArrayList<MapLocation> possibleChoices = new ArrayList<>();
        MapLocation n;
        boolean hostile;// boolean representing if the nations present in a home province are hostile.

        for (int i = 0; i < locations.length; i++) {
            n = locations[i];
            if (n.isHomeProvince() && n.getOwner() == currentNation && n.getOwner() == currentNation
                    && n.getFactory() == null) {
                hostile = false;
                for (int j = 0; j < n.getUnits().length; j++) {
                    if (n.getUnits()[j] != null && n.getUnits()[j].isHostile()) {
                        hostile = true;
                    }
                }
                if (!hostile) {
                    possibleChoices.add(n);
                }

            }
        }

        if (possibleChoices.size() > 1) {
            int rand = getRandomListChoice(possibleChoices);// Gets random choice (index) from possibleChoices list
            int choice = possibleChoices.get(rand).getIndex();
            return choice;
        } else {
            return -1;// We return -1 if we are not able to make a factory because of hostile armies.
        }

    }

    /**
     * This method takes in variables from a certain game state and generates for
     * the currentNation in that state a random set of valid Maneuver actions. This
     * is accomplished by
     * using a validManeuvers list from the GameState.getValidManeuvers() method,
     * and randomly selecting for each location containing that nation's units a
     * valid amount of units and
     * location to move units to.
     * 
     * @param map            Map from the GameState.
     * @param currentNation  Nation who's making the Maneuver action in the current
     *                       GameState.
     * @param controller     Controller from the game (ONLY USED FOR THE
     *                       CHECKMANEUVER METHOD AND POSSIBLY NOT NECESSARY).
     * @param validManeuvers A list of Lists containing locations and their valid
     *                       maneuvers where every index contains a list of format:
     *                       "{strength, unitType, fromLocation,
     *                       toLocation1,toLocation2, toLocation3,....}".
     * @return ArrayList containing all moves made with each individual index
     *         containing a list of format "[fromLocation, toLocation, nationIndex,
     *         unitType, amount]"
     */
    public static ArrayList<ArrayList<Integer>> makeRandomManeuverAction(GameState state) {
        // Map map, Nation currentNation,Controller controller, ArrayList<ArrayList<Integer>> validManeuvers
        Map map = state.getMap();
        Controller controller = state.getController();
        Nation currentNation = state.getNations()[state.getCurrentNation()];
        ArrayList<ArrayList<Integer>> validManeuvers = state.getValidManeuvers();
        
        ArrayList<ArrayList<Integer>> movesMade = new ArrayList<>();

        for (int i = 0; i < validManeuvers.size(); i++) {// Loops through valid locations to move from
            if (random.nextBoolean()) {// 1/2 chance to decide if the AI should move a particular unit
                int randomAmount = randomWithinRange(0, validManeuvers.get(i).get(0)); // moves random amount of units
                int randomLocation = randomWithinRange(3, validManeuvers.get(i).size()); // moves units to a random
                                                                                         // valid space
                
                UnitType unitType = (validManeuvers.get(i).get(1) == 1) ? UnitType.SHIP : UnitType.ARMY;

                boolean unitIsStillThere = false;
                Unit[] unitsAtFromLocation = map.getLocation(validManeuvers.get(i).get(2)).getUnits();
                int indexOfUnitFromlocation = (unitType == UnitType.ARMY ? currentNation.getIndex() : currentNation.getIndex()+6);
                if(unitsAtFromLocation[indexOfUnitFromlocation] != null) {
                    Maneuver.moveUnit(controller, map, currentNation.getIndex(), validManeuvers.get(i).get(2), validManeuvers.get(i).get(randomLocation), unitType, 1000000000);// Moves unit
                    ArrayList<Integer> move = new ArrayList<>();
                    move.add(validManeuvers.get(i).get(2));
                    move.add(validManeuvers.get(i).get(randomLocation));
                    move.add(currentNation.getIndex());
                    move.add(validManeuvers.get(i).get(1));
                    move.add(validManeuvers.get(i).get(0));
                    movesMade.add(move);
                }

                
            }
        }

        return movesMade;
    }

    public static int makeRandomRondelChoice(GameState state) {
        Player player = state.getPlayers()[state.getCurrentPlayer()];
        int rondelIndex = state.getRondelNationPositions()[state.getCurrentNation()];
        int playerMoney = player.getMoney();
        int maxMoves = 0;
        if(rondelIndex == -1) {
            return randomWithinRange(0, 7);
        } else {
            
            if (playerMoney == 0) {
                maxMoves = 3;
            } else if (playerMoney == 2) {
                maxMoves = 4;
            } else if (playerMoney == 4) {
                maxMoves = 5;
            } else {
                maxMoves = 6;
            }

            int moveCount = randomWithinRange(0, maxMoves);
            if(moveCount > 3){
                player.reduceMoney((moveCount-3)*2);
            }
            if(rondelIndex + moveCount > 7) {
                return rondelIndex + moveCount - 7;
            } else {
                return rondelIndex + moveCount;
            }
        }

    }

    public static ArrayList<ArrayList<Integer>> makeRandomImportChoice(GameState state) {
        ArrayList<ArrayList<Integer>> importChoices = new ArrayList<>();
        Nation nation = state.getNations()[state.getCurrentNation()];
        if (nation.getTreasury() < 1) {
            return importChoices;
        }
        int counter = 3;
        int[] possibleLocations = nation.getHomeProvinces();
        ArrayList<Integer> importChoice = new ArrayList<>();
        int choiceTryCounter = 0;

        for(int i = counter; i > 0; i--) {
            if(random.nextBoolean()) {
                if (nation.getTreasury() >= 1) {
                    choiceTryCounter++;
                    int rand = randomWithinRange(0, possibleLocations.length - 1);
                    boolean armyLegal = Import.isImportLegalBoolean(state.getController(), nation.getIndex(), state.getMap(), possibleLocations[rand],
                            UnitType.ARMY);
                    boolean shipLegal = Import.isImportLegalBoolean(state.getController(), nation.getIndex(), state.getMap(),  possibleLocations[rand],
                            UnitType.SHIP);

                    if(armyLegal && shipLegal) {
                        boolean choice  = random.nextBoolean();
                        //If true, build a Army, if false, build a Ship
                        int unitType = (choice ? 1 : 0);
                        importChoice.add(possibleLocations[rand]);
                        importChoice.add(unitType);
                        importChoices.add(importChoice);
                        
                    } else if((armyLegal && !shipLegal)) {
                        importChoice.add(possibleLocations[rand]);
                        importChoice.add(1);
                        importChoices.add(importChoice);
                    } else if(!armyLegal && shipLegal) {
                        importChoice.add(possibleLocations[rand]);
                        importChoice.add(0);
                        importChoices.add(importChoice);
                    } else {
                        i++;
                    }
                    if(armyLegal || shipLegal) {
                        nation.reduceMoney(1);
                    }
                }

            }
            if(choiceTryCounter == 10) {
                break;
            }
        } 
        
        for(ArrayList<Integer> choice : importChoices){
            Unit unit = new Unit((choice.get(1).intValue() == 0 ? UnitType.SHIP : UnitType.ARMY), nation, 1, choice.get(0));
            state.getMap().getLocation(choice.get(0)).addUnit(nation.getIndex(), unit);
            nation.addUnitLocation(choice.get(0));
        }
       
        return importChoices;
    }

    public static int[] makeInvestorChoice(GameState state, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCosts) {
        if(possibleActions.size() == 0){
            int[] result = {-1, -1};
            return result;
        }
        if(possibleActions.size() != associatedCosts.size()) {
            System.out.println("Not same size");
        }
        int randomChoice = getRandomListChoice(possibleActions);
        ArrayList<Integer> buyOptions = associatedCosts.get(randomChoice);
        int random;
        if(buyOptions.size() == 2){
            random = 0;
        }else{
            random = randomWithinRange(0, buyOptions.size() / 2 - 1);
        }
        int[] choice = new int[2];
        choice[0] = randomChoice;
        choice[1] = random*2;

        return choice;
    }

}
