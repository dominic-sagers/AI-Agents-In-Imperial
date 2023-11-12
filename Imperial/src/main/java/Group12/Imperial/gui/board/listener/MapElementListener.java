package Group12.Imperial.gui.board.listener;

import java.io.Serializable;

import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.board.map.MapElementGUI;
import Group12.Imperial.gui.board.map.MapGUI;
import Group12.Imperial.gui.board.map.UnitGUI;
import javafx.scene.Group;

public class MapElementListener implements Serializable{

    public enum Event {
        CLICK,
        DRAG, 
        RELEASED
    }

    private MapGUI game;

    public MapElementListener(MapGUI game) {
        this.game = game;
    }

    public void wasClicked(MapElementGUI element) {
        game.eventHappenedElement(element, Event.CLICK);
    }

    public void dragEnded(UnitGUI unitGUI) {
        game.eventHappenedUnit(unitGUI, Event.DRAG);
    }

    public void wasReleased(MapElementGUI element) {
        game.eventHappenedElement(element, Event.RELEASED);
    }

    public void wasReleased() {
        game.eventHappenedElement(null, Event.RELEASED);
    }

    public void addUnitToMap(Group unit) {
        game.addToGroup(unit);
    }

    public void removeUnitFromMap(Group unit) {
        game.removeFromGroup(unit);
    }

    public void removeUnit(int nation, int location, UnitType type) {
        game.removeUnit(nation, location, type);
    }

    public void removeProducedUnit(Group unit) {
        game.removeFromGroup(unit);
    }
    
}
