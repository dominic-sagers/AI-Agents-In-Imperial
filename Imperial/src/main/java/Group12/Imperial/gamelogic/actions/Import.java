package Group12.Imperial.gamelogic.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.PostConstruct;

import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.Map;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation.LocationType;
import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;

public class Import implements Serializable{

    private static boolean finished = false;

    public static void makeImportChoice(Controller controller, Nation nation, boolean DEBUG) {
        finished = false;
        int importCounter = 3;
        if (nation.getTreasury() < 1)
            finished = true;
        while (!finished) {
            controller.makeImportChoice();
            if(importCounter != 3) nation.reduceMoney(1);
            importCounter--;
            if (importCounter < 1) {
                finished = true;
            }
        }
        
    }

    public static ArrayList<Integer> getImportBuyOptions(Nation nation ){
        ArrayList<Integer> buyOptions = new ArrayList<>();
        if (nation.getTreasury() >= 1) {
            buyOptions.add(nation.getIndex());
            buyOptions.add(1);
        }else{
            finished = true;
        }

        return buyOptions;
    }

    public static boolean isImportLegal(Controller controller, int nation, Map map, int locationIndex, UnitType unitType) {
        MapLocation location = map.getLocation(locationIndex);
        if (!(location.isHomeProvince() && location.getOwner().getIndex() == nation)) {
            return false;

        }
        ArrayList<Integer> nationsPresent = location.getNationsPresent();
        for (Integer i : nationsPresent) {
            if (i.intValue() != nation) {
                Unit unitShip = location.getUnit(i.intValue(), UnitType.SHIP);
                if(unitShip != null && unitShip.isHostile()) return false;
                
                Unit unitArmy = location.getUnit(i.intValue(), UnitType.ARMY);
                if(unitArmy != null && unitArmy.isHostile()) return false;
            }
        }
        if (unitType == UnitType.SHIP) {
            ArrayList<MapLocation> neighbours = location.getNeighbours();
            boolean hasSea = false;
            for (MapLocation neighbour : neighbours) {
                if (neighbour.getType() == LocationType.SEA) {
                    hasSea = true;
                }
            }
            if (!hasSea)
                return false;
        }
        Unit unit = new Unit(unitType, controller.getNationOnIndex(nation), 1, locationIndex);
        location.addUnit(nation, unit);
        controller.getNationOnIndex(nation).addUnitLocation(locationIndex);
        return true;
    }

    public static boolean isImportLegalBoolean(Controller controller, int nation, Map map, int locationIndex,
            UnitType unitType) {
        MapLocation location = map.getLocation(locationIndex);
        if (!(location.isHomeProvince() && location.getOwner().getIndex() == nation)) {
            return false;

        }
        ArrayList<Integer> nationsPresent = location.getNationsPresent();
        for (Integer i : nationsPresent) {
            if (i.intValue() != nation) {
                Unit unitShip = location.getUnit(i.intValue(), UnitType.SHIP);
                if(unitShip != null && unitShip.isHostile()) return false;
                
                Unit unitArmy = location.getUnit(i.intValue(), UnitType.ARMY);
                if(unitArmy != null && unitArmy.isHostile()) return false;
            }
        }
        if (unitType == UnitType.SHIP) {
            ArrayList<MapLocation> neighbours = location.getNeighbours();
            boolean hasSea = false;
            for (MapLocation neighbour : neighbours) {
                if (neighbour.getType() == LocationType.SEA) {
                    hasSea = true;
                }
            }
            if (!hasSea)
                return false;
        }
        return true;
    }



    public static void isFinished() {
        finished = true;
    }

}
