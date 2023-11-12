package Group12.Imperial.gui.board.map;

import java.io.Serializable;

import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FactoryGUI implements Serializable{

    private FactoryType type;
    private Point2D position;

    private Rectangle body;

    public FactoryGUI(FactoryType type, Point2D position, double radius) {
        this.type = type;
        this.position = position;
        init(radius);
    }

    private void init(double radius) {
        body = new Rectangle(radius, radius);
        if (type == FactoryType.BLUE) { body.setFill(Color.DEEPSKYBLUE); 
        } else { body.setFill(Color.BROWN); }
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(2);
        body.setLayoutX(position.getX());
        body.setLayoutY(position.getY());
    }

    public Point2D getPosition() { return new Point2D(body.getLayoutX()+body.getTranslateX(), body.getLayoutY()+body.getTranslateY()); }
    public Rectangle getFactory() { return body; }
    
    @Override
    public boolean equals(Object o) {
        FactoryGUI other = (FactoryGUI)o;
        if (position.equals(other.getPosition())) return true;
        return false;
    }
}
