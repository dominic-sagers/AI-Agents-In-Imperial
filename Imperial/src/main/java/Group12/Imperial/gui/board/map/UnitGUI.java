package Group12.Imperial.gui.board.map;

import java.io.Serializable;

import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.board.listener.MapElementListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class UnitGUI implements Serializable{

    private Group unit;
    private Text text;
    private MapElementListener listener;

    private UnitType type;
    private int number;
    private int nation;

    private double startX;
    private double startY;

    private int lastLocation;
    private MapElementGUI lastElement;

    private Color[] colors = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};

    private Point2D lastPosition;

    public UnitGUI(UnitType type, Point2D pos, int number, int nation, double radius, int locationIndex, MapElementListener listener) {
        this.lastLocation = locationIndex;
        this.type = type;
        this.number = number;
        this.nation = nation;
        this.listener = listener;
        init(radius, pos);
    }

    private void init(double radius, Point2D pos) {
        lastPosition = pos;
        unit = new Group();

        Group body = new Group();
        if (type == UnitType.SHIP) {
            Circle circle = new Circle(radius);
            circle.setFill(colors[nation]);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
            Rectangle rec = new Rectangle(radius*2+8, radius, colors[nation]);
            rec.setStroke(Color.BLACK);
            rec.setLayoutX(circle.getCenterX()-radius-4);
            rec.setLayoutY(circle.getCenterY());
            rec.setStrokeWidth(2);
            text = new Text(circle.getCenterX()-4, circle.getCenterY()+4, Integer.toString(number));
            body.getChildren().addAll(rec, circle, text);
        } else {
            Circle circle = new Circle(radius);
            circle.setFill(colors[nation]);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
            text = new Text(circle.getCenterX()-4, circle.getCenterY()+4, Integer.toString(number));
            body.getChildren().addAll(circle, text);
        }
    
        unit.getChildren().addAll(body);
        unit.setLayoutX(pos.getX());
        unit.setLayoutY(pos.getY());

        unit.setOnMousePressed(e -> {
            e.setDragDetect(false);
            startX = e.getSceneX() - unit.getTranslateX();
            startY = e.getSceneY() - unit.getTranslateY();
            e.consume();
        });
        unit.setOnMouseDragged(e -> {
            e.setDragDetect(false);
            unit.setTranslateX(e.getSceneX() - startX);
            unit.setTranslateY(e.getSceneY() - startY);
            e.consume();
        });
        unit.setOnMouseReleased(e -> {
            unit.setMouseTransparent(true);
            listener.dragEnded(this);
        });
    }

    public void setToLastPosition() {
        unit.setTranslateX(0);
        unit.setTranslateY(0);
        unit.setLayoutX(lastPosition.getX());
        unit.setLayoutY(lastPosition.getY());
        unit.setMouseTransparent(false);
    }

    public void repositionUnit(Point2D point, MapElementGUI element) {
        unit.setTranslateX(0);
        unit.setTranslateY(0);
        unit.setLayoutX(point.getX());
        unit.setLayoutX(point.getY());
        lastPosition = point;
        unit.setMouseTransparent(false);
        lastElement = element;
    }

    public void setLastLocation(int x) { lastLocation = x; }
    public int getLastLocation() { return lastLocation; }
    public MapElementGUI getLastElement() { return lastElement; }
    public void setLastElement(MapElementGUI element) { lastElement = element; }
    public Group getUnit() { return unit; }
    public void setLastPosition(Point2D p) { lastPosition = p; }
    public Point2D getPosition() { return new Point2D(unit.getLayoutX()+unit.getTranslateX(), unit.getLayoutY()+unit.getTranslateY()); }
    public int getNation() { return nation; }
    public int getNumber() { return number; }
    public void setNumber(int number) { 
        this.number = number; 
        text.setText(Integer.toString(this.number));
    }
    public UnitType getType() { return type; }
    public void addUnit(int number) { 
        this.number += number; 
        text.setText(Integer.toString(this.number));
    }
    public boolean decreaseUnit(int number) {
        if ((this.number-number)<=0) {
            return false;
        } else {
            this.number -= number;
            text.setText(Integer.toString(this.number));
            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        return ((UnitGUI)o).getNation() == nation;
    }

    @Override
    public String toString() {
        return "" + nation;
    }
}
