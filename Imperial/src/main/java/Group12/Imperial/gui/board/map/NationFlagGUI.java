package Group12.Imperial.gui.board.map;

import java.io.Serializable;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NationFlagGUI implements Serializable{

    private Point2D position;
    private Color color;

    private Rectangle body;

    private Color[] colors = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};

    public NationFlagGUI(Point2D position, Color color, double width, double height) {
        this.position = position;
        this.color = color;
        init(width, height);
    }

    private void init(double width, double height) {
        body = new Rectangle(width, height);
        body.setFill(color);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1);
        body.setLayoutX(position.getX());
        body.setLayoutY(position.getY());
    }

    public void changeColor(int nation) {
        this.color = colors[nation];
        body.setFill(color);
    }

    public Point2D getPosition() { return new Point2D(body.getLayoutX()+body.getTranslateX(), body.getLayoutY()+body.getTranslateY()); }
    public Rectangle getFlag() { return body; }
    
    
}
