package Group12.Imperial.gui.board.sidecharts;

import java.io.Serializable;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ScoringTrackGUI implements Serializable{

    private HBox layout;
    private ArrayList<ArrayList<Rectangle>> nationPiecesBody;

    private NationPieceGUI[] nationPieces;
    
    public ScoringTrackGUI() {
        init();
    }

    private void init() {
        nationPieces = new NationPieceGUI[6];
        for(int i = 0; i < nationPieces.length; i++) {
            nationPieces[i] = new NationPieceGUI(i);
        }
        nationPiecesBody = new ArrayList<>();
        VBox vBox = new VBox();

        HBox scoringObjects = new HBox();

        Group object0 = createScoringObject(0, 54, 67);

        for (int i = 1; i <= 25; i++) {
            scoringObjects.getChildren().add(createScoringObject(i, 54, 50));
        }

        vBox.getChildren().addAll(createMultiplierRow(2), scoringObjects);

        Group group = new Group();
        group.getChildren().add(vBox);

        layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        layout.setId("sidechart");
        layout.getChildren().addAll(object0, group);
        layout.setPadding(new Insets(5, 0, 5, 0));

        for(NationPieceGUI n : nationPieces) n.setPosition(0);
    }

    private HBox createMultiplierRow(double reduction) {
        HBox box = new HBox();

        Label label1 = new Label("Credit x" + 0);
        label1.setPrefHeight(10-reduction);
        label1.setPrefWidth(216-reduction);
        label1.setTextFill(Color.BLACK);
        label1.setAlignment(Pos.CENTER);
        label1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        label1.getStyleClass().add("bordered");
        box.getChildren().add(label1);

        for(int i = 1; i < 5; i++) {
            Label label = new Label("Credit x" + i);
            label.setPrefHeight(10-reduction);
            label.setPrefWidth(270-reduction);
            label.setTextFill(Color.BLACK);
            label.setAlignment(Pos.CENTER);
            label.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            label.getStyleClass().add("bordered");
            box.getChildren().add(label);
        }
        
        Label label6 = new Label("Credit x5");
        label6.setPrefHeight(10-1.0);
        label6.setPrefWidth(54-1.0);
        label6.setTextFill(Color.BLACK);
        label6.setAlignment(Pos.CENTER);
        label6.getStyleClass().add("bordered");
        label6.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        box.getChildren().add(label6);

        return box;
    }

    private Group createScoringObject(int index, double width, double height) {
        Group object = new Group();

        Rectangle rectangle = new Rectangle(width-1.0, height-1.0);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(0.5);
        rectangle.setId("sidechart_inside");

        Text text = new Text(""+index);
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-size: " + 15 + "px;");
        text.setX(rectangle.getX() + 0.5*rectangle.getWidth() - 6);
        text.setY(rectangle.getY() + 0.5*rectangle.getHeight() + 6);

        Rectangle nation0 = new Rectangle((width-1.0)/3, (height-1.0)/4, Color.YELLOW);
        nation0.setX(1.0);
        nation0.setY(1.0);
        nation0.setOpacity(0);
        Rectangle nation1 = new Rectangle((width-1.0)/3, (height-1.0)/4, Color.GREEN);
        nation1.setX(nation0.getWidth());
        nation1.setY(1.0);
        nation1.setOpacity(0);
        Rectangle nation2 = new Rectangle((width-1.0)/3, (height-1.0)/4, Color.BLUE);
        nation2.setX(nation0.getWidth()*2-1.0);
        nation2.setY(1.0);
        nation2.setOpacity(0);
        Rectangle nation3 = new Rectangle((width-1.0)/3, (height-1.0)/4, Color.ORANGERED);
        nation3.setX(1.0);
        nation3.setY(nation0.getHeight()*3);
        nation3.setOpacity(0);
        Rectangle nation4 = new Rectangle((width-1.0)/3, (height-1.0)/4, Color.DARKGRAY);
        nation4.setX(nation0.getWidth());
        nation4.setY(nation0.getHeight()*3);
        nation4.setOpacity(0);
        Rectangle nation5 = new Rectangle((width-1.0)/3, (height-1.0)/4, Color.PURPLE);
        nation5.setX(nation0.getWidth()*2-1.0);
        nation5.setY(nation0.getHeight()*3);
        nation5.setOpacity(0);
        ArrayList<Rectangle> nationPiecesInside = new ArrayList<>();
        nationPiecesInside.add(nation0);
        nationPiecesInside.add(nation1);
        nationPiecesInside.add(nation2);
        nationPiecesInside.add(nation3);
        nationPiecesInside.add(nation4);
        nationPiecesInside.add(nation5);
        nationPiecesBody.add(nationPiecesInside);

        object.getChildren().addAll(rectangle, text, nation0, nation1, nation2, nation3, nation4, nation5);

        return object;
    }

    public void updateScoringTrack(int nationIndex, int position) {
        nationPieces[nationIndex].setPosition(position);
    }

    public HBox getLayout() { return layout; }

    class NationPieceGUI {

        private int nationIndex;
        private int position;

        public NationPieceGUI(int nationindex) {
            this.nationIndex = nationindex;
            this.position = 0;
        }

        public int getNationIndex() { return nationIndex; }
        public int getPosition() { return position; }
        public void setPosition(int position) {
            nationPiecesBody.get(this.position).get(nationIndex).setOpacity(0.0);
            this.position = position; 
            nationPiecesBody.get(position).get(nationIndex).setOpacity(1.0);
        }
    }
}
