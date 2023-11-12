package Group12.Imperial.gui.board.sidecharts;

import java.io.Serializable;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class TaxationChartGUI implements Serializable{
    
    private VBox layout;

    private ArrayList<ArrayList<Rectangle>> nationPiecesBody;

    private NationPieceGUI[] nationPieces;

    public TaxationChartGUI() {
        init();
    }

    private void init() {
        nationPieces = new NationPieceGUI[6];
        for(int i = 0; i < nationPieces.length; i++) {
            nationPieces[i] = new NationPieceGUI(i);
        }
        nationPiecesBody = new ArrayList<>();
        layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(0, 5, 0, 5));
        layout.setId("sidechart");
        
        Group obj10 = createTaxObject("2-5", "+0");
        Group obj9 = createTaxObject("6", "+1");
        Group obj8 = createTaxObject("7", "+2");
        Group obj7 = createTaxObject("8", "+3");
        Group obj6 = createTaxObject("9", "+4");
        Group obj5 = createTaxObject("10", "+5");
        Group obj4 = createTaxObject("11", "+6");
        Group obj3 = createTaxObject("12", "+7");
        Group obj2 = createTaxObject("13", "+8");
        Group obj1 = createTaxObject("14", "+9");
        Group obj0 = createTaxObject("15+", "+10");

        layout.getChildren().addAll(obj0, obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);

        for(NationPieceGUI n : nationPieces) n.setPosition(0);
    }

    private Group createTaxObject(String value, String bonus) { 
        Group group = new Group();

        Rectangle body = new Rectangle(150, 55);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1.0);
        body.setId("sidechart_inside");

        Text valueText = new Text(value);
        valueText.setX(4.0);
        valueText.setY(body.getHeight()/2+4);
        valueText.setStyle("-fx-font-size: " + 15 + "px;");

        Text bonusText = new Text(bonus);
        bonusText.setFill(Color.BLUE);
        bonusText.setStroke(Color.BLACK);
        bonusText.setStrokeWidth(0.2);
        bonusText.setX(body.getWidth() - 35.0);
        bonusText.setY(body.getHeight()/2+4);
        bonusText.setStyle("-fx-font-size: " + 15 + "px;");

        double width = 20;

        Rectangle nation0 = new Rectangle(width, (body.getHeight()-1.0)/3, Color.YELLOW);
        nation0.setX(body.getWidth()/2-width);
        nation0.setY(1.0);
        nation0.setOpacity(0);
        Rectangle nation1 = new Rectangle(width, (body.getHeight()-1.0)/3, Color.GREEN);
        nation1.setX(body.getWidth()/2-width);
        nation1.setY(nation0.getHeight());
        nation1.setOpacity(0);
        Rectangle nation2 = new Rectangle(width, (body.getHeight()-1.0)/3, Color.BLUE);
        nation2.setX(body.getWidth()/2-width);
        nation2.setY(nation0.getHeight()*2);
        nation2.setOpacity(0);
        Rectangle nation3 = new Rectangle(width, (body.getHeight()-1.0)/3, Color.ORANGERED);
        nation3.setX(body.getWidth()/2);
        nation3.setY(1.0);
        nation3.setOpacity(0);
        Rectangle nation4 = new Rectangle(width, (body.getHeight()-1.0)/3, Color.DARKGRAY);
        nation4.setX(body.getWidth()/2);
        nation4.setY(nation0.getHeight());
        nation4.setOpacity(0);
        Rectangle nation5 = new Rectangle(width, (body.getHeight()-1.0)/3, Color.PURPLE);
        nation5.setX(body.getWidth()/2);
        nation5.setY(nation0.getHeight()*2);
        nation5.setOpacity(0);
        ArrayList<Rectangle> nationPiecesInside = new ArrayList<>();
        nationPiecesInside.add(nation0);
        nationPiecesInside.add(nation1);
        nationPiecesInside.add(nation2);
        nationPiecesInside.add(nation3);
        nationPiecesInside.add(nation4);
        nationPiecesInside.add(nation5);
        nationPiecesBody.add(nationPiecesInside);

        group.getChildren().addAll(body, valueText, bonusText, nation0, nation1, nation2, nation3, nation4, nation5);

        return group;
    }

    public void updateTaxChart(int nation, int position) { nationPieces[nation].setPosition(position); }

    public VBox getLayout() { return layout; }

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
