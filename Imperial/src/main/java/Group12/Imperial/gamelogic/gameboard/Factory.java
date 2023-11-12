package Group12.Imperial.gamelogic.gameboard;

import java.io.Serializable;

import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation;

public class Factory implements Serializable{

    public enum FactoryType implements Serializable{
        BLUE, BROWN
    }

    private MapLocation location;
    private FactoryType type;

    public Factory(MapLocation location, FactoryType type) {
        this.location = location;
        this.type = type;
    }

    public void produce(int amount) {
        if (type == FactoryType.BLUE) {
            location.addUnit(location.getOwner(), UnitType.SHIP, amount);
        } else {
            location.addUnit(location.getOwner(), UnitType.ARMY, amount);
        }
    }

    public FactoryType getType() { return type; }
    public int getLocationIndex() { return location.getIndex(); }
    public void setLocation(MapLocation location) { this.location = location; }
    
}
