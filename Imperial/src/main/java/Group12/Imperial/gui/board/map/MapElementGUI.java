package Group12.Imperial.gui.board.map;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.board.listener.MapElementListener;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class MapElementGUI implements Serializable{

    private SVGPath element;
    private int index;
    private String path;
    private Color color;
    private Point2D position;

    private MapElementListener listener;

    private Point2D[] unitPositions;

    private UnitGUI[] units;
    private FactoryGUI factory;
    private NationFlagGUI flag;

    public MapElementGUI(int index, String path, Color color, Point2D position, double[] objPos, FactoryType factoryType, MapElementListener listener) {
        this.index = index;
        this.path = path;
        this.color = color;
        this.position = position;
        this.listener = listener;

        init(objPos, factoryType);
    }

    private void init(double[] objPos, FactoryType type) {
        createSVGPath();
        units = new UnitGUI[3];
        if (objPos[8] != -1) {
            factory = new FactoryGUI(type, new Point2D(objPos[8], objPos[9]), 15);
        }
        if (objPos[0] != -1) {
            unitPositions = new Point2D[3];
            unitPositions[0] = new Point2D(objPos[0], objPos[1]);
            unitPositions[1] = new Point2D(objPos[2], objPos[3]);
            unitPositions[2] = new Point2D(objPos[4], objPos[5]);
            flag = new NationFlagGUI(new Point2D(objPos[6], objPos[7]), color, 20, 12);
        }
    }

    public boolean addUnit(UnitGUI newUnit) {
        for (int i = 0; i < units.length; i++) {
            if (units[i] == null) {
                units[i] = newUnit;
                positionUnit(i);
                return true;
            } else if (units[i].getNation() == newUnit.getNation() && units[i].getType() == newUnit.getType()) {
                units[i].addUnit(newUnit.getNumber());
                if(newUnit.getLastElement() != null) {
                    listener.removeUnit(newUnit.getNation(), newUnit.getLastElement().getIndex(), newUnit.getType());
                } else {
                    listener.removeProducedUnit(newUnit.getUnit());
                }
                return true;
            }
        }
        return false;
    }

    public void unitWasMoved(int nation, UnitType type) {
        for (int i = 0; i < units.length; i++) {
            if (units[i] != null) {
                if (units[i].getNation() == nation && units[i].getType() == type) {
                    for (int j = i; j < units.length-1; j++) {
                        units[j] = units[j+1];
                    }
                    units[units.length-1] = null;
                }
            }
        }

    }

    public UnitGUI removeUnit(int nation, UnitType type) {
        UnitGUI unit = null;
        for(UnitGUI u : units) {
            if(u != null && u.getNation() == nation && u.getType() == type) unit = u;
        }
        unitWasMoved(nation, type);
        return unit;
    }

    private void positionUnit(int i) {
        units[i].setLastLocation(index);
        if (units[i].getLastElement() != null) { 
            units[i].getLastElement().unitWasMoved(units[i].getNation(), units[i].getType());
        }
        units[i].setLastElement(this);
        units[i].setLastPosition(unitPositions[i]);
        units[i].getUnit().setTranslateX(0);
        units[i].getUnit().setTranslateY(0);
        units[i].getUnit().setLayoutX(unitPositions[i].getX());
        units[i].getUnit().setLayoutY(unitPositions[i].getY());
        units[i].getUnit().setMouseTransparent(false);
    }

    private void createSVGPath() {
        element = new SVGPath();
        element.setContent(path);
        element.setFill(color);
        element.setStroke(Color.BLACK);
        element.setLayoutX(position.getX());
        element.setLayoutY(position.getY());

        element.setOnMouseClicked(e -> listener.wasClicked(this));
        element.setOnMouseEntered(e -> listener.wasReleased(this));
    }

    public SVGPath getElement() {
        return element;
    }

    public ArrayList<UnitGUI> getUnits() {
        
        ArrayList<UnitGUI> activeUnits = new ArrayList<>();
        for (UnitGUI unit : units) {
            if (unit != null) activeUnits.add(unit);
        }
        
        return activeUnits;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }

    public Rectangle addFactory() {
        return factory.getFactory();
    }
    
    public Rectangle addFlag(int nation) {
        flag.changeColor(nation);
        return flag.getFlag();
    }

    public void lock() {
        element.setMouseTransparent(true);
        for (UnitGUI unit : units) {
            if (unit != null) {
                unit.getUnit().setMouseTransparent(true);
            }
        }
        if (factory != null)factory.getFactory().setMouseTransparent(true);
        if (flag != null) flag.getFlag().setMouseTransparent(true);
    }

    public void unlock(int nation, RondelChoice choice) {
        if (choice == RondelChoice.FACTORY || choice == RondelChoice.IMPORT) {
            element.setMouseTransparent(false);
        } else if (choice == RondelChoice.MANEUVER) {
            element.setMouseTransparent(false);
            for (UnitGUI unit : units) {
                if (unit != null) {
                    if (unit.getNation() == nation) unit.getUnit().setMouseTransparent(false);
                }
            }
        }
    }

    public void updateUnit(int nation, int strength) {
        for(UnitGUI u : units) {
            if(u != null && u.getNation() == nation) u.setNumber(strength);
        }
    }
}
