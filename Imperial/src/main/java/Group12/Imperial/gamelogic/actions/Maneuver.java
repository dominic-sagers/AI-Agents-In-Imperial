package Group12.Imperial.gamelogic.actions;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.Map;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation.LocationType;

public class Maneuver implements Serializable{

    /**
     * Checks whether a certain move of a unit is legal for a certain nation. Uses a
     * flood fill algorithm which checks if it is legal to go to the adjacent node.
     * 
     * @param map          The map of the game
     * @param nation       The index of the nation making the move
     * @param fromLocation The index of the location the piece was moved from
     * @param toLocation   The index of the location the piece was moved to
     * @param unitType     The type of unit
     * @param movableUnits
     * @return True if a path could be found, false if not
     */
    public static boolean checkManeuver(Controller controller, Map map, int nation, int fromLocation, int toLocation, UnitType unitType, int amount, ArrayList<ArrayList<Integer>> movableUnits) {
        boolean isMovable = false;
        for (int i = 0; i < movableUnits.size(); i++) {
            if (movableUnits.get(i).get(0) == fromLocation && movableUnits.get(i).get(1) > 0) {
                isMovable = true;
            }
        }
        if(!isMovable) return false;

        ArrayList<Integer> visitedLocations = new ArrayList<>();
        boolean pathFound = false;
        ArrayList<MapLocation> frontiers = new ArrayList<>();
        ArrayList<MapLocation> newFrontiers = new ArrayList<>();

        LocationType locType;
        if (unitType == UnitType.ARMY) {
            locType = LocationType.LAND;
        } else {
            locType = LocationType.SEA;
        }

        frontiers.add(map.getLocation(fromLocation));
        visitedLocations.add(fromLocation);

        while (!pathFound) {
            if (frontiers.size() == 0)
                break;
            for (MapLocation frontier : frontiers) {
                ArrayList<MapLocation> neighbours = frontier.getNeighbours();
                for (MapLocation neighbour : neighbours) {
                    if (!visitedLocations.contains(neighbour.getIndex())) {
                        ArrayList<Integer> nationsPresent = neighbour.getNationsPresent();
                        boolean isLegalToMove = true;
                        if (neighbour.getType() != locType) {
                            if (locType == LocationType.LAND) {
                                boolean hasOwnShip = false;
                                for (Integer integer : nationsPresent) {
                                    if (integer == nation) {
                                        hasOwnShip = true;
                                    }
                                }
                                if (!hasOwnShip)
                                    isLegalToMove = false;
                                if (neighbour.getIndex() == toLocation)
                                    isLegalToMove = false;
                            } else {
                                isLegalToMove = false;
                            }
                        } else if (!(neighbour.isHomeProvince() && neighbour.getOwner().getIndex() == nation
                                && (nationsPresent.size() == 0 || (nationsPresent.size() == 1 && nationsPresent.get(0) == nation)))) {
                            isLegalToMove = false;
                            if (neighbour.getIndex() == toLocation)
                                pathFound = true;
                        } else {
                            for (Integer integer : nationsPresent) {
                                if (integer != nation) {
                                    isLegalToMove = false;
                                    if (neighbour.getIndex() == toLocation)
                                        pathFound = true;
                                }
                            }
                        }
                        if (isLegalToMove) {
                            if (neighbour.getIndex() == toLocation) {
                                pathFound = true;
                            } else {
                                newFrontiers.add(neighbour);
                                visitedLocations.add(neighbour.getIndex());
                            }
                        }
                    }
                }
            }
            frontiers = newFrontiers;
            newFrontiers = new ArrayList<>();
        }

        if (pathFound) moveUnit(controller, map, nation, fromLocation, toLocation, unitType, amount);
        
        return pathFound;
    }

    // Same as checkManeuver, just without the moveUnit() call
    public static ArrayList<Integer> checkIfManeuverIsPossible(Controller controller, Map map, int nation,
            int fromLocation, UnitType unitType) {

        ArrayList<Integer> visitedLocations = new ArrayList<>();
        boolean pathFound = false;
        ArrayList<MapLocation> frontiers = new ArrayList<>();
        ArrayList<MapLocation> newFrontiers = new ArrayList<>();

        ArrayList<Integer> possibleLocations = new ArrayList<>();

        LocationType locType;
        if (unitType == UnitType.ARMY) {
            locType = LocationType.LAND;
        } else {
            locType = LocationType.SEA;
        }

        frontiers.add(map.getLocation(fromLocation));
        visitedLocations.add(fromLocation);

        while (!pathFound) {
            if (frontiers.size() == 0)
                break;
            for (MapLocation frontier : frontiers) {
                ArrayList<MapLocation> neighbours = frontier.getNeighbours();
                for (MapLocation neighbour : neighbours) {
                    if (!visitedLocations.contains(neighbour.getIndex())) {
                        ArrayList<Integer> nationsPresent = neighbour.getNationsPresent();
                        boolean isLegalToMove = true;
                        if (neighbour.getType() != locType) {
                            if (locType == LocationType.LAND) {
                                boolean hasOwnShip = false;
                                for (Integer integer : nationsPresent) {
                                    if (integer == nation) {
                                        hasOwnShip = true;
                                    }
                                }
                                if (!hasOwnShip) {
                                    isLegalToMove = false;
                                }
                            } else {
                                isLegalToMove = false;
                            }
                        } else if(neighbour.isHomeProvince() && neighbour.getOwner() == null) {
                            System.out.println("Location " + neighbour.getIndex());
            
                        } else if (!(neighbour.isHomeProvince() && neighbour.getOwner().getIndex() == nation && (nationsPresent.size() == 0 || (nationsPresent.size() == 1 && nationsPresent.get(0) == nation)))) {
                            
                            isLegalToMove = false;
                            possibleLocations.add(neighbour.getIndex());
                        } else {
                            for (Integer integer : nationsPresent) {
                                if (integer != nation) {
                                    isLegalToMove = false;
                                    possibleLocations.add(neighbour.getIndex());
                                }
                            }
                        }
                        if (isLegalToMove) {
                            if(locType == neighbour.getType())possibleLocations.add(neighbour.getIndex());
                            newFrontiers.add(neighbour);
                            visitedLocations.add(neighbour.getIndex());
                        } else {
                            visitedLocations.add(neighbour.getIndex());
                        }
                    }
                }
            }
            frontiers = newFrontiers;
            newFrontiers = new ArrayList<>();
        }
        return possibleLocations;
    }

    public static void moveUnit(Controller controller, Map map, int nation, int fromLocation, int toLocation, UnitType unitType, int amount) {
        Unit unit = map.getLocation(fromLocation).removeUnit(nation, unitType, amount);
        if(unit == null) return;
        unit.setLocation(toLocation);
        map.getLocation(toLocation).addUnit(nation, unit);
        controller.getNationOnIndex(nation).addUnitLocation(toLocation);
        if(map.getLocation(fromLocation).getUnit(nation, unitType) == null) {
            controller.getNationOnIndex(nation).removeUnitLocation(fromLocation);
        }
        controller.moveUnitGUI(nation, unitType, fromLocation, toLocation);

        boolean battle = false;
        ArrayList<Integer> nationsPresent = map.getLocation(toLocation).getNationsPresent();
        for (Integer nationPre : nationsPresent) {
            if (nationPre.intValue() != nation) {
                battle = true;
            }
        }
        if (battle) {
            controller.battleAtLocation(toLocation);
        } else {
            if(!map.getLocation(toLocation).isHomeProvince()) {
                Nation ownerOfLocation = map.getLocation(toLocation).getOwner();
                Nation newOwner = controller.getNationOnIndex(nation);
                map.getLocation(toLocation).setOwner(newOwner);
                newOwner.addOwnedLocation(toLocation);
                if(ownerOfLocation != null && ownerOfLocation.getIndex() != newOwner.getIndex()) {
                    ownerOfLocation.removeOwnedLocation(toLocation);
                } 
                controller.addGUIFlag(newOwner.getIndex(), toLocation);
            }
            
            if (controller.makeIsHostileChoice()) {
                unit.setIsHostile(true);
            } else {
                unit.setIsHostile(false);
            }
        }
    }

}