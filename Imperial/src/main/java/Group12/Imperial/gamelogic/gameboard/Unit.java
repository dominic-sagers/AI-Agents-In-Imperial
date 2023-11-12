package Group12.Imperial.gamelogic.gameboard;

import java.io.Serializable;

import Group12.Imperial.gamelogic.Nation;

public class Unit implements Serializable {

    public enum UnitType implements Serializable{
        SHIP, ARMY
    }

    private UnitType type;
    private Nation owner;
    private int strength;
    private boolean isHostile;
    private int location;

    public Unit(UnitType type, Nation owner, int strength, int location) {
        this.type = type;
        this.owner = owner;
        this.strength = strength;
        this.location = location;
    }

    public void addStrength(int amount) { strength += amount; }
    public boolean reduceStrength(int amount) {
        if (strength - amount <= 0) return false;
        strength -= amount;
        return true;
    }

    public UnitType getType() { return type; }
    public Nation getOwner() { return owner; }
    public void setOwner(Nation nation) { this.owner = nation; }
    public int getStrength() { return strength; }
    public boolean isHostile() { return isHostile; }
    public void setIsHostile(boolean h) { isHostile = h; }
    public int getLocation() { return location; }
    public void setLocation(int location) { this.location = location; }
    
}
