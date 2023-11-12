package Group12.Imperial.gui.board.investor;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gui.GameScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class InvestorScreen implements Serializable{

    private GameScreen gameScreen;
    private ArrayList<Bond> possibleActions;
    private ArrayList<ArrayList<Integer>> associatedCost;

    private VBox layout;

    private ArrayList<BondGUI> bondGUIObjects;
    private Label currentPlayerLabel;
    private Label currentPhaseLabel;
    private Color[] nationColours = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};

    public InvestorScreen(GameScreen gameScreen, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCost) {
        this.gameScreen = gameScreen;
        this.possibleActions = possibleActions;
        this.associatedCost = associatedCost;
        init();
    }

    private void init() {
        layout = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        bondGUIObjects = new ArrayList<>();

        
        
        VBox scrollContent = new VBox(20);
        HBox vBoxWrapper = new HBox(scrollContent);
        
        scrollContent.setAlignment(Pos.CENTER);
        
        for(int i = 0; i < possibleActions.size(); i++) {
            BondGUI bondGUI = new BondGUI(this, possibleActions.get(i), associatedCost.get(i), i);
            bondGUIObjects.add(bondGUI);
            scrollContent.getChildren().add(bondGUI.getElement());
        }

        StackPane pane = new StackPane(vBoxWrapper);
        pane.setPadding(new Insets(30));
        pane.setId("bond_object");
        
        scrollPane.setContent(pane);
        HBox root = new HBox(scrollPane);
        root.setAlignment(Pos.CENTER);
        root.setId("bond_object");

        HBox menuBar = createMenuBar();
        layout.getChildren().addAll(menuBar, root);
        layout.setAlignment(Pos.CENTER);

    }

    public void optionSelected(int index, int buyIndex) {
        gameScreen.investorChoiceWasMade(index, buyIndex);
    }

    public void setRoot(Scene scene) {
        scene.setRoot(layout);
    }

    private HBox createMenuBar() {
        HBox menuBar = new HBox(20);
        menuBar.setId("sidechart");
        menuBar.setMinHeight(50);

        currentPlayerLabel = new Label("null");
        currentPlayerLabel.setPrefHeight(25);
        currentPlayerLabel.setPrefWidth(200);
        currentPlayerLabel.setStyle("-fx-font-size: " + 19 + "px;");
        currentPlayerLabel.setAlignment(Pos.CENTER);

        Button rondelButton = new Button("Finished");
        rondelButton.setPrefHeight(30);
        rondelButton.setPrefWidth(200);
        rondelButton.setStyle("-fx-font-size: " + 19 + "px;");
        rondelButton.setOnAction(e -> gameScreen.investorChoiceWasMade(-1, -1));

        currentPhaseLabel = new Label("null");
        currentPhaseLabel.setPrefHeight(30);
        currentPhaseLabel.setPrefWidth(350);
        currentPhaseLabel.setStyle("-fx-font-size: " + 19 + "px;");
        currentPhaseLabel.setTextFill(Color.BLACK);
        currentPhaseLabel.setBackground(new Background(new BackgroundFill(Color.rgb(255, 208, 138), new CornerRadii(5.0), new Insets(-5.0))));
        currentPhaseLabel.setAlignment(Pos.CENTER);

        menuBar.getChildren().addAll(currentPlayerLabel, rondelButton, currentPhaseLabel);
        menuBar.setAlignment(Pos.CENTER);

        return menuBar;
    }

    public void updateMenuBar(int currentPlayer, int currentNation, String phase) {
        currentPlayerLabel.setText("Player " + (currentPlayer+1));
        //currentPlayerLabel.setTextFill(playerColors[currentPlayer]);
        currentPlayerLabel.setBackground(new Background(new BackgroundFill(nationColours[currentNation], new CornerRadii(5.0), new Insets(-5.0))));
        currentPhaseLabel.setText("Current phase: " + phase);
    }
}
