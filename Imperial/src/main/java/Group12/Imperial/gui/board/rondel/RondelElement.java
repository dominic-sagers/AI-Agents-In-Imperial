package Group12.Imperial.gui.board.rondel;

import java.io.Serializable;

import Group12.Imperial.gui.board.listener.RondelListener;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;
import javafx.geometry.Point2D;
import javafx.scene.shape.SVGPath;

public class RondelElement implements Serializable{
    protected SVGPath svgPath;
    protected RondelChoice type;
    private RondelListener listener;
    private int index;

    private Point2D[] piecePositions;
    private NationPieceRondelGUI[] playerPieces;

    public RondelElement(int index, String path, RondelChoice type, Point2D point, Point2D[] piecePositions, RondelListener listener) {
        this.index = index;
        this.type = type;
        this.piecePositions = piecePositions;
        this.listener = listener;
        init(path, point);
    }

    private void init(String path, Point2D initPos) {
        svgPath = new SVGPath();
        svgPath.setContent(path);
        svgPath.setLayoutX(initPos.getX());
        svgPath.setLayoutY(initPos.getY());
        svgPath.setScaleX(1.3);
        svgPath.setScaleY(1.3);
        svgPath.setOpacity(0.0);
        
        svgPath.setOnMouseEntered(e -> listener.wasReleased(this));
    }

    public void givePlayers(NationPieceRondelGUI[] playerPieces) {
        this.playerPieces = playerPieces;
    }

    public void relocatePiece(int index) {
        playerPieces[index].setPosition(piecePositions[index]);
    }

    public int getIndex() { return index; }

    @Override
    public String toString() {
        return type.toString();
    }
}
