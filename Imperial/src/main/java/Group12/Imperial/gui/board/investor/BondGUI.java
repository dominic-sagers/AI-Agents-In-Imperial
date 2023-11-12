package Group12.Imperial.gui.board.investor;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Bond;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BondGUI implements Serializable{

    private InvestorScreen investorScreen;
    private Bond bond;
    private ArrayList<Integer> buyOptions;
    private int index;
    
    private Group body;
    private VBox layout;

    private Color[] nationColours = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};

    public BondGUI(InvestorScreen investorScreen, Bond bond, ArrayList<Integer> buyOptions, int index) {
        this.investorScreen = investorScreen;
        this.bond = bond;
        this.buyOptions = buyOptions;
        this.index = index;
        init();
    }

    private void init() {
        body = new Group();

        layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(nationColours[bond.getNationName().index], new CornerRadii(5.0), new Insets(-5.0))));
        layout.setPadding(new Insets(15, 15, 15, 15));

        String title = bond.getNationName().stringRepresentation;
        Label titleLabel = new Label(title);
        titleLabel.setBackground(new Background(new BackgroundFill(Color.GOLD, new CornerRadii(5.0), new Insets(-5.0))));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefHeight(30);
        titleLabel.setPrefWidth(260);
        titleLabel.setTextFill(Color.BLACK);
        String bondValue = "Value: " + String.valueOf(bond.getValue() + " Million");
        Label valueLabel = new Label(bondValue);
        valueLabel.setBackground(new Background(new BackgroundFill(Color.BEIGE, new CornerRadii(5.0), new Insets(-5.0))));
        valueLabel.setAlignment(Pos.CENTER);
        valueLabel.setPrefHeight(20);
        valueLabel.setPrefWidth(260);
        valueLabel.setTextFill(Color.BLACK);
        String bondInterest = "Interest: " + bond.getInterest() + " Million";
        Label interestlabel = new Label(bondInterest);
        interestlabel.setBackground(new Background(new BackgroundFill(Color.BEIGE, new CornerRadii(5.0), new Insets(-5.0))));
        interestlabel.setAlignment(Pos.CENTER);
        interestlabel.setPrefHeight(20);
        interestlabel.setPrefWidth(260);
        interestlabel.setTextFill(Color.BLACK);
        layout.getChildren().addAll(titleLabel, valueLabel, interestlabel);

        for(int i = 0; i < buyOptions.size(); i = i+2) {
            if(buyOptions.get(i) == -1) {
                String text = "Buy for " + buyOptions.get(i+1) + " Million";
                layout.getChildren().add(createButton(text, i));
            } else {
                String text = "Upgrade Bond " + buyOptions.get(i) + " for " + buyOptions.get(i+1) + " Million";
                layout.getChildren().add(createButton(text, i));
            }
        }
        body.getChildren().add(layout);
    }

    private Button createButton(String text, int buyIndex) {
        Button button = new Button(text);
        button.setPrefWidth(260);
        button.setPrefHeight(20);
        button.setOnAction(e -> {
            investorScreen.optionSelected(index, buyIndex);
        });

        return button;
    }

    public Group getElement() { return body; }
}
