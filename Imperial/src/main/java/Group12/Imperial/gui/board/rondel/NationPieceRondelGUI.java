package Group12.Imperial.gui.board.rondel;

import java.io.Serializable;

import Group12.Imperial.gui.board.listener.RondelListener;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class NationPieceRondelGUI implements Serializable{

    private Circle body;
    private int player;

    private RondelListener listener;

    private Point2D lastPosition;
    private int lastRondelElement;

    private double startX;
    private double startY;

    public NationPieceRondelGUI(int player, Color color, double radius, Point2D point, RondelListener listener) {
        this.player = player;
        this.listener = listener;
        this.lastPosition = point;
        this.lastRondelElement = -1;
        init(radius, color, point);
    }

    private void init(double radius, Color color, Point2D point) {
        body = new Circle(radius, color);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(3);
        body.setLayoutX(point.getX());
        body.setLayoutY(point.getY());
        
        body.setOnMousePressed(e -> {
            startX = e.getSceneX() - body.getTranslateX();
            startY = e.getSceneY() - body.getTranslateY();
        });
        body.setOnMouseDragged(e -> {
            body.setTranslateX(e.getSceneX() - startX);
            body.setTranslateY(e.getSceneY() - startY);
        });
        body.setOnMouseReleased(e -> {
            body.setMouseTransparent(true);
            listener.dragEnded(this);
        });
    }

    public void setPosition(Point2D point) {
        body.setTranslateX(0);
        body.setTranslateY(0);
        body.setLayoutX(point.getX());
        body.setLayoutY(point.getY());
        lastPosition = point;
        body.setMouseTransparent(false);
    }

    public void setToLastPosition() {
        body.setTranslateX(0);
        body.setTranslateY(0);
        body.setLayoutX(lastPosition.getX());
        body.setLayoutY(lastPosition.getY());
        body.setMouseTransparent(false);
    }

    public void lock() { body.setMouseTransparent(true); }
    public void unlock() { body.setMouseTransparent(false); }
    public void setLastRondelPos(int i) { lastRondelElement = i; }
    public int getLastRondelPos() { return lastRondelElement; }
    public int getPlayer() { return player; }
    public Circle getElement() { return body; }

    @Override
        public String toString() {
            return "" + player;
        }
}
