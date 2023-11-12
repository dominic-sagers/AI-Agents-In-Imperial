package Group12.Imperial.gamelogic;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Factory;
import Group12.Imperial.gamelogic.gameboard.Unit;

public class Nation implements Serializable{

    public enum NationName implements Serializable{ 
        AUSTRIAHUNGARY(0, "Austria Hungary"), ITALY(1, "Italy"), FRANCE(2, "France"), GREATBRITAIN(3, "Great Britain"), GERMANY(4, "Germany"), RUSSIA(5, "Russia");

        public int index;
        public String stringRepresentation;

        private NationName(int index, String stringRepresentation) {
            this.index = index;
            this.stringRepresentation = stringRepresentation;
        }
    }

    private int index;
    private NationName name;
    private int[] homeProvinces;
    private int treasury;
    private ArrayList<Factory> factories;
    private ArrayList<Integer> unitLocations;
    private ArrayList<Integer> ownedLocations;
    private int powerPoints;

    public Nation(int index, NationName name, int[] homeProvinces) {
        this.index = index;
        this.name = name;
        this.homeProvinces = homeProvinces;
        init();
    }

    private void init() {
        factories = new ArrayList<>();
        unitLocations = new ArrayList<>();
        ownedLocations = new ArrayList<>();
        powerPoints = 0;

        for (int i : homeProvinces) { ownedLocations.add(i); }
    }

    public void addMoney(int amount) { treasury += amount; }
    public boolean reduceMoney(int amount) {
        treasury -= amount;
        if (treasury < 0) {
            treasury += amount;
            return false;
        }
        return true;
    }
    public int getTreasury() { return treasury; }
    public void setTreasury(int treasury) { this.treasury = treasury;}

    public int[] getHomeProvinces() { return homeProvinces; }

    public void addOwnedLocation(int locationIndex) { 
        if(!ownedLocations.contains(Integer.valueOf(locationIndex))) ownedLocations.add(locationIndex); 
    }
    public void removeOwnedLocation(int locationIndex) { ownedLocations.remove(Integer.valueOf(locationIndex)); }
    public ArrayList<Integer> getOwnedLocations() { return ownedLocations; }
    public void setOwnedLocations(ArrayList<Integer> ownedLocations) { this.ownedLocations = ownedLocations; }

    public void addUnitLocation(int location) { if(!unitLocations.contains(Integer.valueOf(location))) unitLocations.add(Integer.valueOf(location)); }
    public void removeUnitLocation(int location) { unitLocations.remove(Integer.valueOf(location)); }
    public ArrayList<Integer> getUnitLocations() { return unitLocations; }
    public void setUnitLocations(ArrayList<Integer> unitLocations) { this.unitLocations = unitLocations;}

    public void addFactory(Factory factory) { factories.add(factory); }
    public void removeFactory(Factory factory) { factories.remove(factory); }
    public ArrayList<Factory> getFactories() { return factories; }
    public void setFactories(ArrayList<Factory> factories) { this.factories = factories; }
    
    public int getIndex() { return index; }
    public NationName getName() { return name; }

    public int getPowerPoints() { return powerPoints; }
    public boolean addPowerPoints(int amount) { 
        powerPoints += amount; 
        if(powerPoints >= 25) return true;
        return false;
    }
    public void setPowerPoints(int powerPoints) { this.powerPoints = powerPoints; }

    @Override
    public String toString() {
        return name.stringRepresentation;
    }
    
}
