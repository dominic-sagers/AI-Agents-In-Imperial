package Group12.Imperial.gamelogic.gameboard.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.gameboard.Factory;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;

public class MapLocation implements Serializable{

    public enum LocationType implements Serializable{
        LAND, SEA
    }

    private ArrayList<MapLocation> neighbours;

    private LocationType type;
    private Nation owner;
    private int index;
    private Unit[] units;
    private Factory factory;
    private boolean isHomeProvince;
    private FactoryType factoryType;

    /**
     * A location on the map
     * 
     * @param index The index of that location
     */
    public MapLocation(int index) {
        this.index = index;
    }

    /**
     * Initialises the object with all needed information
     * 
     * @param type           The type of the location
     * @param neighbours     A list of all neigbouring locations
     * @param initialOwner   The initial owner of this location, null if no owner
     * @param isHomeProvince Specifies if the location is a home province of a
     *                       nation
     */
    public void init(LocationType type, ArrayList<MapLocation> neighbours, Nation initialOwner, boolean isHomeProvince,
            FactoryType factoryType) {
        this.type = type;
        this.neighbours = neighbours;
        this.owner = initialOwner;
        this.isHomeProvince = isHomeProvince;
        this.factoryType = factoryType;
        this.units = new Unit[12];
    }

    /**
     * Adds a Unit to this location. It is NOT checked whether that is currently
     * legal to do in this location!
     * 
     * @param nation The nation the Unit belongs to
     * @param type   The type of the unit
     * @param amount The amount of units
     */
    public void addUnit(Nation nation, UnitType type, int amount) {
        int nationIndex = nation.getIndex();
        if (type == UnitType.SHIP) {
            nationIndex += 6;
        }
        if (units[nationIndex] == null) {
            units[nationIndex] = new Unit(type, nation, amount, index);
        } else {
            units[nationIndex].addStrength(amount);
        }
    }

    public void addUnit(int nationIndex, Unit unit) {
        if (unit.getType() == UnitType.SHIP) {
            nationIndex += 6;
        }
        if (units[nationIndex] == null) {
            units[nationIndex] = unit;
        } else {
            units[nationIndex].addStrength(unit.getStrength());
        }
    }

    public Unit removeUnit(int nationIndex, UnitType type, int amount) {
        if (type == UnitType.SHIP) {
            nationIndex += 6;
        }
        if(units[nationIndex] == null) {
            System.out.println("Unit is null at location");
            return null;
        } else if (units[nationIndex].getStrength() - amount <= 0) {
            
            Unit unit = units[nationIndex];
            units[nationIndex] = null;
            return unit;
            
        } else {
            units[nationIndex].reduceStrength(amount);
            return new Unit(type, units[nationIndex].getOwner(), amount, index);
        }
    }

    /**
     * Gets all nations which currently have units in this location
     * 
     * @return An ArrayList of all those nations
     */
    public ArrayList<Integer> getNationsPresent() {
        ArrayList<Integer> nationsPresent = new ArrayList<>();
        for (Unit unit : units) {
            if (unit != null) {
                int nationToAdd = unit.getOwner().getIndex();
                if (!nationsPresent.contains(nationToAdd))
                    nationsPresent.add(nationToAdd);
            }
        }
        return nationsPresent;
    }

    /**
     * Initialises a battle between nations and reduces the units until there is a
     * winner.
     * 
     * @param nations The nations which want to battle
     */
    public ArrayList<Integer> battle(Controller controller, ArrayList<Integer> nations) {
        ArrayList<Integer> nationsPresent = getNationsPresent();
        boolean[] stillStanding = new boolean[4];

        // stillStanding looks like [ARMY first nation, ARMY second nation, SHIP first nation, SHIP second antion]

        stillStanding[0] = (units[nations.get(0)] != null ? true : false);
        stillStanding[1] = (units[nations.get(1)] != null ? true : false);
        stillStanding[2] = (units[nations.get(0)+6] != null ? true : false);
        stillStanding[3] = (units[nations.get(1)+6] != null ? true : false);
        
        boolean thereIsWinner = false;
        while (!thereIsWinner) {
            for (int i = 0; i < nations.size(); i++) {
                if (stillStanding[i]) {
                    if(units[nations.get(i)] == null) {
                        System.out.println("Unit is null");
                    }
                    stillStanding[i] = units[nations.get(i)].reduceStrength(1);

                    if (!stillStanding[i]) {
                        units[nations.get(i)] = null;
                        controller.removeUnitGUI(index, nations.get(i).intValue(), UnitType.ARMY);
                    }
                } else if (stillStanding[i + 2]) {
                    if(units[nations.get(i) + 6] == null) {
                        System.out.println("Unit is null");
                    }
                    stillStanding[i + 2] = units[nations.get(i) + 6].reduceStrength(1);
                    if (!stillStanding[i + 2]) { 
                        units[nations.get(i) + 6] = null;
                        controller.removeUnitGUI(index, nations.get(i).intValue(), UnitType.SHIP);
                    }
                }

            }
            int counter = 0;
            for (int i = 0; i < nations.size(); i++) {
                if (stillStanding[i]) {
                    counter++;
                } else if (stillStanding[i + 2]) {
                    counter++;
                }
            }
            if (counter < 2)
                thereIsWinner = true;
        }
        return nationsPresent;
    }

    public void buildFactory(Factory factory) {
        this.factory = factory;
    }

    public Unit[] getUnits(){
        return this.units;
    }

    public Factory getFactory() {
        return factory;
    }

    public FactoryType getFactoryType() {
        return factoryType;
    }

    public int getIndex() {
        return index;
    }

    public void setOwner(Nation owner) {
        this.owner = owner;
    }

    public Nation getOwner() {
        return owner;
    }

    public LocationType getType() {
        return type;
    }

    public boolean isHomeProvince() {
        return isHomeProvince;
    }

    public ArrayList<MapLocation> getNeighbours() {
        return neighbours;
    }

    public Unit getUnit(int nationIndex, UnitType unitType) {
        if (unitType == UnitType.ARMY)
            return units[nationIndex];
        return units[nationIndex + 6];
    }

    public int getStrengthOfNation(int nationIndex) {
        int strength = 0;
        if(units[nationIndex] != null) {
            strength += units[nationIndex].getStrength();
        }
        if(units[nationIndex+6] != null) {
            strength += units[nationIndex+6].getStrength();
        }
        return strength;
    }

    public Unit[] getAllUnitsOfNation(int nation) {
        Unit[] allUnitsOfNation = { units[nation], units[nation + 6] };
        return allUnitsOfNation;
    }

    public boolean hasHostileUnitOfOtherNation(int ownNation) {
        for(int i = 0; i < units.length; i++) {
            if(units[i] != null && i != ownNation && i != (ownNation + 6)) {
                if(units[i].isHostile()) {
                    return true;
                }  
            }     
        }
        return false;
    }

    public String getUnitsAsString() {
        String output = "{";
        for(Unit u : units) {
            if(u != null) output = output + "(Nation:" + u.getOwner().getIndex() + ",Strength:" + u.getStrength() + ",Type:" + u.getType() + ")";
        }
        output = output + "}";
        return output;
    }

}
