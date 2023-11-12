package Group12.Imperial.gui.board.sidecharts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PlayerTreasuryGUI implements Serializable{

    private int playerCount;
    private ArrayList<Group> playerObjects;
    private Text[] playerMoney;
    private ArrayList<ArrayList<Label>> bonds;

    private VBox leftBox;
    private VBox rightBox;

    public PlayerTreasuryGUI(int playerCount) {
        this.playerCount = playerCount;
        init();
    }

    private void init() {
        bonds = new ArrayList<>();
        playerMoney = new Text[playerCount];
        playerObjects = new ArrayList<>();
        leftBox = new VBox(20);
        leftBox.setPrefWidth(250);
        leftBox.setId("sidechart");
        leftBox.setAlignment(Pos.CENTER);
        rightBox = new VBox(20);
        rightBox.setPrefWidth(250);
        rightBox.setId("sidechart");
        rightBox.setAlignment(Pos.CENTER);

        int counter = 0;
        for(int i = 0; i < playerCount; i++) {
            playerObjects.add(createPlayerObject(i, "Player " + (i+1)));
            if(counter == 0) {
                leftBox.getChildren().add(playerObjects.get(i));
                counter = 1;
            } else {
                rightBox.getChildren().add(playerObjects.get(i));
                counter = 0;
            }
        }

    }

    public VBox getLeft() { return leftBox; }
    public VBox getRight() { return rightBox; }

    public void updatePlayerTreasury(int playerIndex, int money, String[] bondsString) {
        playerMoney[playerIndex].setText(money + " Million");
        for(int i = 0; i < bondsString.length; i++) {
            bonds.get(playerIndex).get(i).setText(bondsString[i]);
        }
    }

    private Group createPlayerObject(int playerIndex, String playerName) {
        Group group = new Group();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setId("sidechart_player");
        layout.setPrefWidth(230);
        layout.setPadding(new Insets(5, 0, 5, 0));

        Label playerNameLabel = new Label(playerName);
        playerNameLabel.setTextFill(Color.BLACK);
        playerNameLabel.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5.0), new Insets(-5.0))));
        playerNameLabel.setAlignment(Pos.CENTER);
        playerNameLabel.setPrefHeight(25);
        playerNameLabel.setPrefWidth(200);
        playerNameLabel.setId("player_label");

        Label moneyTitle = new Label("Money");
        moneyTitle.setTextFill(Color.BLACK);
        moneyTitle.getStyleClass().add("bordered");
        moneyTitle.setAlignment(Pos.CENTER);
        moneyTitle.setPrefHeight(25);
        moneyTitle.setPrefWidth(200);

        Text money = new Text();
        money.setText(0 + " Million");
        money.setFill(Color.BLACK);
        playerMoney[playerIndex] = money;

        Label bondTitle = new Label("Owned Bonds");
        bondTitle.setTextFill(Color.BLACK);
        bondTitle.getStyleClass().add("bordered");
        bondTitle.setAlignment(Pos.CENTER);
        bondTitle.setPrefHeight(25);
        bondTitle.setPrefWidth(200);

        Group bondsObject = createBondObject(playerIndex);

        layout.getChildren().addAll(playerNameLabel,moneyTitle, money, bondTitle, bondsObject);
        group.getChildren().add(layout);

        return group;
    }

    private Group createBondObject(int playerIndex) {
        Group group = new Group();
        Color[] colors = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};
/*
        ImageView[] images = new ImageView[6];
        String[] imagePaths = {"/images/AU-flag.png", "/images/IT-flag.png", "/images/FR-flag.png", "/images/GB-flag.png", "/images/GE-flag.png", "/images/RU-flag.png"};
        

        for(int i = 0; i < images.length; i++) {
            Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePaths[i])).toString());
            images[i] = new ImageView();
            images[i].setImage(image);
            images[i].setFitWidth(90);
            images[i].setFitHeight(50);
            images[i].setPreserveRatio(false);
        }
*/
        HBox outerWrapper = new HBox(15);
        VBox left = new VBox(10);
        VBox right = new VBox(10);

        ArrayList<Label> labels = new ArrayList<>();

        for(int i = 0; i < 6; i++) {
            if(i < 3) {
                Label l = new Label();
                l.setBackground(new Background(new BackgroundFill(colors[i], new CornerRadii(5.0), new Insets(-5.0))));
                l.setAlignment(Pos.CENTER);
                l.setPrefHeight(20);
                l.setPrefWidth(90);
                l.setText("null");
                l.setTextFill(Color.BLACK);
                labels.add(l);
                left.getChildren().add(l);
            } else {
                Label l = new Label();
                l.setBackground(new Background(new BackgroundFill(colors[i], new CornerRadii(5.0), new Insets(-5.0))));
                l.setAlignment(Pos.CENTER);
                l.setPrefHeight(20);
                l.setPrefWidth(90);
                l.setText("null");
                l.setTextFill(Color.BLACK);
                labels.add(l);
                right.getChildren().add(l);
            }
        }
        bonds.add(labels);
        outerWrapper.getChildren().addAll(left, right);
        group.getChildren().add(outerWrapper);

        return group;
    }
    
}
