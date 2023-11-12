package Group12.Imperial.gamelogic.agents;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.actions.Import;
import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.Player;
import Group12.Imperial.gamelogic.actions.Maneuver;
import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.map.Map;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation.LocationType;

import java.util.ArrayList;
import java.util.Random;

public class Rules {

    // produce armies(maybe will be more useful)
    public static int factoryRule(GameState state) {
        Nation currentNation = state.getNations()[state.getCurrentNation()];
        if (currentNation.getTreasury() - 5 < 0) {
            return -1;
        }

        MapLocation[] locations = state.getMap().getAllMapLocations();
        MapLocation n;
        boolean hostile;// boolean representing if the nations present in a home province are hostile.

        for (int i = 0; i < locations.length; i++) {
            n = locations[i];
            if (n.isHomeProvince() && n.getOwner() == currentNation && n.getFactory() == null
                    && n.getFactoryType() == FactoryType.BROWN) {
                hostile = false;
                for (int j = 0; j < n.getUnits().length; j++) {
                    if (n.getUnits()[j] != null && n.getUnits()[j].isHostile()) {
                        hostile = true;
                    }
                }
                if (!hostile) {
                    return n.getIndex();
                }
            }
        }
        return -1;
    }



    // look for unoccupied spaces and move there OR always try to move to the
    // furthest place
    public static ArrayList<ArrayList<Integer>> manueuverRule(GameState state) {
        Nation nation = state.getNations()[state.getCurrentNation()];
        ArrayList<ArrayList<Integer>> maneuvers = new ArrayList<>();
        ArrayList<ArrayList<Integer>> validManeuvers = state.getValidManeuvers();
        Map map = state.getMap();
        Controller controller = state.getController();


        for (int i = 0; i < validManeuvers.size(); i++) {
            
            ArrayList<Integer> freeTerritories = new ArrayList<>();
            ArrayList<Integer> claimableTerritories = new ArrayList<>();
            ArrayList<Integer> enemyTerritories = new ArrayList<>();
            
            int ownStrength = validManeuvers.get(i).get(0);
            
            for(int j = 3; j < validManeuvers.get(i).size(); j++) {
                MapLocation location = map.getLocation(validManeuvers.get(i).get(j));
                ArrayList<Integer> nationsPresent = location.getNationsPresent();
                if(nationsPresent.size() == 0 && !location.isHomeProvince()){
                    freeTerritories.add(j);
                }
                if(nationsPresent.size() > 0 && !location.isHomeProvince()) {
                    
                    for(int k = 0; k < nationsPresent.size(); k++){
                        if(nationsPresent.get(k) == nation.getIndex()) {

                        } else {
                            int enemyStrength = 0;
                            Unit army = location.getUnit(nationsPresent.get(k), UnitType.ARMY);
                            Unit ship = location.getUnit(nationsPresent.get(k), UnitType.SHIP);
                            if(army != null) {
                                enemyStrength += army.getStrength();
                            }
                            if(ship != null) {
                                enemyStrength += ship.getStrength();
                            }
                           if(ownStrength > enemyStrength) {
                                claimableTerritories.add(j);
                           }
                        }
                    }
                }
                if(nationsPresent.size() > 0 && location.isHomeProvince() && location.getOwner().getIndex() != nation.getIndex()) {
                    
                   enemyTerritories.add(j);
                }

            }
            int targetLocation = -1;
            if(freeTerritories.size() > 0) {
                targetLocation = freeTerritories.get(RandomChoice.getRandomListChoice(freeTerritories));
            }else if (claimableTerritories.size() > 0){
                targetLocation = claimableTerritories.get(RandomChoice.getRandomListChoice(claimableTerritories));
            } else if(enemyTerritories.size() > 0) {
                targetLocation = enemyTerritories.get(RandomChoice.getRandomListChoice(enemyTerritories));
            }else{
                targetLocation = RandomChoice.randomWithinRange(3, validManeuvers.get(i).size());
            }


            Unit.UnitType unitType = (validManeuvers.get(i).get(1) == 1) ? Unit.UnitType.SHIP : Unit.UnitType.ARMY;
            Maneuver.moveUnit(controller, map, nation.getIndex(), validManeuvers.get(i).get(2),
                    validManeuvers.get(i).get(targetLocation), unitType, 1000000);
            ArrayList<Integer> move = new ArrayList<>();
            move.add(validManeuvers.get(i).get(2));
            move.add(validManeuvers.get(i).get(targetLocation));
            move.add(nation.getIndex());
            move.add(validManeuvers.get(i).get(1));
            move.add(validManeuvers.get(i).get(0));
            maneuvers.add(move);

        }

        return maneuvers;
    }
// rondel = { RondelChoice.MANEUVER, RondelChoice.TAXATION, RondelChoice.FACTORY,
//             RondelChoice.PRODUCTION, RondelChoice.MANEUVER, RondelChoice.INVESTOR, RondelChoice.IMPORT,
//             RondelChoice.PRODUCTION };
    public static int rondelRule(GameState state){
        Player player = state.getPlayers()[state.getCurrentPlayer()];
        int playerMoney = player.getMoney();
        int rondelIndex = state.getRondelNationPositions()[state.getCurrentNation()];
        int totalFactories = state.getNations()[state.getCurrentNation()].getFactories().size();
        int totalArmy = state.getNations()[state.getCurrentNation()].getUnitLocations().size();
        int nationTreasury = state.getNations()[state.getCurrentNation()].getTreasury();
        int ownedLocations = state.getNations()[state.getCurrentNation()].getOwnedLocations().size();
        
        if(rondelIndex == -1) {
            return 2; 
        } else if(player.hasInvestor() && rondelIndex < 5 && isReachable(rondelIndex, 7) && playerMoney >= 5) {//Pass investor
            return 7;
        } else if(player.hasInvestor() && rondelIndex < 5 && isReachable(rondelIndex, 6) && playerMoney >= 5) {
            return 6;
        } else if(isReachable(rondelIndex, 2) && nationTreasury >= 5 && totalFactories < 4) {
            return 2;
        } else if(isReachable(rondelIndex, 3) && totalArmy <= 2){
            return 3;
        } else if(isReachable(rondelIndex, 7) && totalArmy <= 2){
            return 7;
        } else if(isReachable(rondelIndex, 1) && ownedLocations > 6) {
            return 1;
        } else if(isReachable(rondelIndex, 0) && totalArmy > 0){
            return 0;
        } else if(isReachable(rondelIndex, 4) && totalArmy > 0){
            return 4;
        } else if(isReachable(rondelIndex, 6) && nationTreasury >= 3) {
            return 6;
        } else if(isReachable(rondelIndex, 5) && playerMoney > 5) {
            return 5;
        } else if(isReachable(rondelIndex, 3)) {
            return 3;
        } else if(isReachable(rondelIndex, 7)) {
            return 7;
        } else {
            if(rondelIndex + 1 > 7) {
                return 0;
            } else {
                return rondelIndex + 1;
            }
        }
    }

    // try to ignore moving more than 3 spaces so that you can keep the money to
    // invest
    // choose the rondel choice you can perform
    // advance: let it calculate victory points and only finish the game if it's the
    // winning player
    // public static int rondelRule(GameState state) {
        
    //     Player player = state.getPlayers()[state.getCurrentPlayer()];
    //     int rondelIndex = state.getRondelNationPositions()[state.getCurrentNation()];
    //     int playerMoney = player.getMoney();
    //     int maxMoves = 3;
    //     int totalFactories = state.getNations()[state.getCurrentNation()].getFactories().size();
    //     int totalArmy = state.getNations()[state.getCurrentNation()].getUnitLocations().size();
        
    //     if(rondelIndex == -1) return 3;
    //     //investor
    //     if(rondelIndex < 5 && rondelIndex + maxMoves <= 5 && player.hasInvestor() && playerMoney > 3){
    //         return 5;
    //     }
    //     //import
    //     else if(rondelIndex < 6 && rondelIndex + maxMoves >= 6 && playerMoney > 15){
    //         return 6;
    //     }
    //     //factory
    //     else if((rondelIndex < 2 || 7 < rondelIndex && rondelIndex - 8 < 2) && (rondelIndex + maxMoves >= 2 || 7 < rondelIndex && rondelIndex - 8 + maxMoves >= 2) && totalFactories < (3 * totalArmy)){
    //         return 2;
    //     }
    //     //taxation
    //     else if((rondelIndex < 1 || 7 < rondelIndex && rondelIndex - 8 < 1) && (rondelIndex + maxMoves >= 1 || 7 < rondelIndex && rondelIndex - 8 + maxMoves >= 1) && totalFactories > 3){
    //         return 1;
    //     }
    //     //prdouction
    //     else if(rondelIndex < 3 && rondelIndex + 3 >= 3 && totalArmy < 5){
    //         return 3;
    //     }
    //     //production
    //     else if(rondelIndex < 7 && rondelIndex + 3 >= 7 && totalArmy < 5){
    //         return 7;
    //     }
    //     //maneuver
    //     else if(rondelIndex - 8 < 0 && rondelIndex - 8 + maxMoves >= 0 && totalArmy > 0){
    //         return 0;
    //     }
    //     //maneuver
    //     else if(rondelIndex < 4 && rondelIndex + 3 >= 4 && totalArmy > 0){
    //         return 4;
    //     }

    //     return -1;
    // }

    private static boolean isReachable(int currentPosition, int desiredPosition) {
        for(int i = 0; i < 3; i++) {
            if(currentPosition + 1 > 7) {
                currentPosition = 0;
            } else {
                currentPosition += 1;
            }
            if(currentPosition == desiredPosition) {
                return true;
            }
        }
        return false;
    }

    // always max import if you have the money
    // focus on importing armies instead of fleets
    public static ArrayList<ArrayList<Integer>> importRule(GameState state) {
        Nation nation = state.getNations()[state.getCurrentNation()];
        int counter = 0;
        ArrayList<ArrayList<Integer>> importChoices = new ArrayList<>();
        if(nation.getTreasury() < 1){
            return importChoices;    
        }
       
        // determine how many units to place
        if(nation.getTreasury() >= 3) {
            counter = 3;
        } else{
            counter = nation.getTreasury();
        }

        /*
        * get list of home provinces that do nothave hostile armies
        * then get number of those provinces that have their own units in them
        * then check the <50%
        * if <50% then spread units
        * else stack units on highest strength unit.
        * if units are same strength then pick random.
        */
        ArrayList<Integer> possibleArmyLocations = new ArrayList<>();
        ArrayList<Integer> possibleShipLocations = new ArrayList<>();
        ArrayList<Integer> locationsWithOwnUnits = new ArrayList<>();
        MapLocation[] locations = state.getMap().getAllMapLocations();
        int[] homeProvinces = nation.getHomeProvinces();
 
        // first get list of home provinces that do not have hostile armies and get number of provinces that has own units in them
        for (int i = 0; i < homeProvinces.length; i++) {
            MapLocation loc = locations[homeProvinces[i]];
            
            boolean armyLegal = Import.isImportLegalBoolean(state.getController(), nation.getIndex(), state.getMap(), loc.getIndex(), UnitType.ARMY);
            boolean shipLegal = Import.isImportLegalBoolean(state.getController(), nation.getIndex(), state.getMap(), loc.getIndex(), UnitType.SHIP);            
            
            if(armyLegal) {
                possibleArmyLocations.add(loc.getIndex());
            } else if(shipLegal) {
                possibleShipLocations.add(loc.getIndex());
            }

            Unit[] allUnitsAtLoc = loc.getAllUnitsOfNation(nation.getIndex());
            if(allUnitsAtLoc[0] != null || allUnitsAtLoc[1] != null) {
                locationsWithOwnUnits.add(loc.getIndex());
            }
        }
        double percentageSpread = (double)locationsWithOwnUnits.size() / 5.0;
        boolean shouldSpread = (percentageSpread < 0.5) ? true : false;

        if(shouldSpread) {
            boolean importWorked = false;
            for(int i = 0; i < possibleArmyLocations.size(); i++){
                if(!locationsWithOwnUnits.contains(possibleArmyLocations.get(i)) && !importWorked) {
                    for(int j = 0 ; j < counter ; j++) {
                        ArrayList<Integer> importList = new ArrayList<>();
                        importList.add(possibleArmyLocations.get(i));
                        importList.add(1);
                        importChoices.add(importList);
                        importWorked = true;
                    }
                    
                }
            }
            if(!importWorked) {
                for(int i = 0; i < possibleShipLocations.size(); i++) {
                    if(!locationsWithOwnUnits.contains(possibleShipLocations.get(i)) && !importWorked) {
                        for(int j = 0 ; j < counter ; j++) {
                            ArrayList<Integer> importList = new ArrayList<>();
                            importList.add(possibleShipLocations.get(i));
                            importList.add(0);
                            importChoices.add(importList);
                            importWorked = true;
                        }
                    }
                }
            }
            
        } else {
            int maxStrength = 0;
            ArrayList<Integer> maxChoice = new ArrayList<>();
            
            for(Integer posImport : locationsWithOwnUnits) {
                
                if(possibleArmyLocations.contains(posImport) && locations[posImport].getStrengthOfNation(nation.getIndex()) > maxStrength){
                    maxChoice = new ArrayList<Integer>();
                    maxChoice.add(posImport);
                    maxChoice.add(1);
                    maxStrength = locations[posImport].getStrengthOfNation(nation.getIndex());
                } else if(possibleShipLocations.contains(posImport) && locations[posImport].getStrengthOfNation(nation.getIndex()) > maxStrength){
                    maxChoice = new ArrayList<Integer>();
                    maxChoice.add(posImport);
                    maxChoice.add(0);
                    maxStrength = locations[posImport].getStrengthOfNation(nation.getIndex());
                }
            }

            for(int j = 0 ; j < counter ; j++) {
                importChoices.add(maxChoice);
            }
        }
        
        for(ArrayList<Integer> choice : importChoices){
            Unit unit = new Unit((choice.get(1).intValue() == 0 ? UnitType.SHIP : UnitType.ARMY), nation, 1, choice.get(0));
            state.getMap().getLocation(choice.get(0)).addUnit(nation.getIndex(), unit);
            nation.addUnitLocation(choice.get(0));
            nation.reduceMoney(1);
        }

        return importChoices;
    }

    // always make it aggresive for places that it's not yours
    public static boolean hostileRule(GameState state) {
        Nation nation = state.getNations()[state.getCurrentNation()];
        ArrayList<Integer> ownedLocations = nation.getOwnedLocations();
        MapLocation[] locations = state.getMap().getAllMapLocations();

        boolean hostile = true;

        for (int i = 0; i < locations.length; i++) {
            if (locations[i].equals(ownedLocations)) {
                hostile = false;
            } else {
                hostile = true;
            }

        }
        return hostile;
    }


    public static int[] investorRule(GameState state, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCosts) {
        int choice = -1;
        if(associatedCosts.size() == 0){
            int[] output = new int[2];
            output[0] = -1;
            return output;
        }
        for (int i = 0; i < possibleActions.size(); i++) {
            int highest = -1;
            if(possibleActions.get(i).getValue() > highest){
                highest = possibleActions.get(i).getValue();
                choice = i;
            }
        }
        ArrayList<Integer> options = associatedCosts.get(choice);
        
        int[] output = new int[2];
        output[0] = choice;
        
        return output;
    }

}
