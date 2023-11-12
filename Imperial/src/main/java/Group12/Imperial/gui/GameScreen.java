package Group12.Imperial.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.board.investor.InvestorScreen;
import Group12.Imperial.gui.board.listener.MapElementListener.Event;
import Group12.Imperial.gui.board.map.MapElementGUI;
import Group12.Imperial.gui.board.map.MapGUI;
import Group12.Imperial.gui.board.rondel.RondelGUI;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;
import Group12.Imperial.gui.board.sidecharts.NationTreasuryGUI;
import Group12.Imperial.gui.board.sidecharts.ScoringTrackGUI;
import Group12.Imperial.gui.board.sidecharts.TaxationChartGUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameScreen implements Serializable{

    private Stage stage;
    private ControllerGUI controllerGUI;

    private Scene scene;
    private MapGUI gameMap;
    private RondelGUI rondelScreen;
    private ScoringTrackGUI scoringTrack;
    private NationTreasuryGUI nationTreasury;
    private TaxationChartGUI taxChart;
    private VBox layout;

    private int playerCount;

    private Label currentPlayerLabel;
    private HBox menuBar;
    private Color[] playerColors = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};
    private String[] playerNames;

    private Label currentPhaseLabel;

    private Button finishButton;

    private final EventKey EVENTKEY = new EventKey();

    private HBox simulationBar;

    public GameScreen(ControllerGUI controllerGUI, Stage stage) {
        this.controllerGUI = controllerGUI;
        this.stage = stage;
    }

    public void load(int playerCount, String[] playerNames, Scene scene) {
        this.playerCount = playerCount;
        this.playerNames = playerNames;
        this.scene = scene;
        gameMap = new MapGUI(this);
        loadGameScreen();
    }

    private void loadGameScreen() {
        Parent pane = gameMap.getMap();
        pane.setId("mapPane");

        menuBar = createMenuBar();

        nationTreasury = new NationTreasuryGUI();

        scoringTrack = new ScoringTrackGUI();

        taxChart = new TaxationChartGUI();

        HBox hBox = new HBox();
        hBox.getChildren().addAll(nationTreasury.getLayout(), pane, taxChart.getLayout());
        HBox.setHgrow(pane, Priority.ALWAYS);

        layout = new VBox();
        if(simulationBar != null) {
            layout.getChildren().addAll(simulationBar, menuBar, hBox, scoringTrack.getLayout());
        } else {
            layout.getChildren().addAll(menuBar, hBox, scoringTrack.getLayout());
        }

        rondelScreen = new RondelGUI(playerCount, this);

        VBox.setVgrow(hBox, Priority.ALWAYS);
        scene.setRoot(layout);

        scene.setOnKeyPressed(e -> {
            String codeString = e.getCode().toString();
            if(codeString == "ESCAPE") MainGUI.backToMainMenuAlert(stage);
        });
    }

    public void eventHappened(MapElementGUI element, Event event) {
        if (event == Event.CLICK) System.out.println(element.toString());
    }

    public HBox createMenuBar() {
        HBox menuBar = new HBox(20);
        menuBar.setId("sidechart");
        menuBar.setPrefHeight(50);

        currentPlayerLabel = new Label("null");
        currentPlayerLabel.setPrefHeight(25);
        currentPlayerLabel.setPrefWidth(200);
        currentPlayerLabel.setStyle("-fx-font-size: " + 19 + "px;");
        currentPlayerLabel.setAlignment(Pos.CENTER);

        Button rondelButton = new Button("Rondel");
        rondelButton.setPrefHeight(30);
        rondelButton.setPrefWidth(200);
        rondelButton.setStyle("-fx-font-size: " + 19 + "px;");
        rondelButton.setOnAction(e -> rondelScreen.setRoot(scene));

        finishButton = new Button("Finish Turn");
        finishButton.setPrefHeight(30);
        finishButton.setPrefWidth(200);
        finishButton.setStyle("-fx-font-size: " + 19 + "px;");
        finishButton.setOnAction(e -> { gameMap.finishedManeuver(); });

        currentPhaseLabel = new Label("null");
        currentPhaseLabel.setPrefHeight(30);
        currentPhaseLabel.setPrefWidth(280);
        currentPhaseLabel.setStyle("-fx-font-size: " + 19 + "px;");
        currentPhaseLabel.setTextFill(Color.BLACK);
        currentPhaseLabel.setBackground(new Background(new BackgroundFill(Color.rgb(255, 208, 138), new CornerRadii(5.0), new Insets(-5.0))));
        currentPhaseLabel.setAlignment(Pos.CENTER);

        menuBar.getChildren().addAll(currentPlayerLabel, rondelButton, finishButton, currentPhaseLabel);
        menuBar.setAlignment(Pos.CENTER);

        return menuBar;
    }

    public void updateMenuBar(int currentPlayer, int currentNation, String phase, boolean maneuver) {
        currentPlayerLabel.setText(playerNames[currentPlayer]);
        //currentPlayerLabel.setTextFill(playerColors[currentPlayer]);
        currentPlayerLabel.setBackground(new Background(new BackgroundFill(playerColors[currentNation], new CornerRadii(5.0), new Insets(-5.0))));
        currentPhaseLabel.setText("Current phase: " + phase);
        try {
            menuBar.getChildren().remove(finishButton);
        } catch (Exception e) {}
        if(maneuver) {
            menuBar.getChildren().add(2, finishButton);
        }
    }

    public void goTo() {
        scene.setRoot(layout);
    }

    public int getRondelChoice(int currentPlayer, int currentNation) {
        updateMenuBar(currentPlayer, currentNation, "Rondel", false);
        rondelScreen.updateMenuBar(currentPlayer, currentNation, "Rondel");
        return rondelScreen.getDecision(currentNation);
    }

    public void getManeuver(int currentPlayer, int currentNation) {
        updateMenuBar(currentPlayer, currentNation, "Maneuver", true);
        rondelScreen.updateMenuBar(currentPlayer, currentNation, "Maneuver");
        gameMap.getManeuver(0);
    }

    public boolean isManeuverLegal(int fromElement, int toElement, UnitType unitType, int amount) {
        return controllerGUI.isManeuverLegal(fromElement, toElement, unitType, amount);
    }

    public HBox getMenuBar() {
        return null;
    }

    public int buildFactory(int currentPlayer, int currentNation) {
        //System.out.println("here");
        updateMenuBar(currentPlayer, currentNation, "Factory", false);
        rondelScreen.updateMenuBar(currentPlayer, currentNation, "Factory");
        return gameMap.buildFactory(1);
    }

    public boolean isFactoryBuildLegal(int index) {
        return controllerGUI.isFactoryBuildLegal(index);
    }

    public boolean isRondelMoveLegal(int rondelIndex) {
        return controllerGUI.isRondelMoveLegal(rondelIndex);
    }

    public void addUnit(int currentPlayer, int currentNation, int element, int type, boolean shouldUpdateMenuBar) {
        if(shouldUpdateMenuBar) updateMenuBar(currentPlayer, currentNation, "Production", false);
        rondelScreen.updateMenuBar(currentPlayer, currentNation, "Production");
        if (type == 0) {
            gameMap.addUnit(currentNation, 1, element, UnitType.ARMY);
        } else {
            gameMap.addUnit(currentNation, 1, element, UnitType.SHIP);
        }
    }

    public void updateScoringTrack(int nation, int position) {
        scoringTrack.updateScoringTrack(nation, position);
    }

    public void updateNationTreasury(int nation, int money) {
        nationTreasury.updateNationTreasury(nation, money);
    }

    public void updateTaxationChart(int nation, int position) {
        taxChart.updateTaxChart(nation, position);
    }

    public void updatePlayerTreasury(int playerIndex, int money, String[] bonds) {
        rondelScreen.updatePlayerTreasury(playerIndex, money, bonds);
    }

    public void buildInitialFactory(int location) {
        gameMap.buildInitialFactory(location);
    }

    public void lockMap() {
        gameMap.lockMap();
    }

    public void unlockMap(int nation, RondelChoice choice) {
        gameMap.unlockMap(nation, choice);
    }

    //public void moveUnit(int )

    public void removeUnit(int nation, int location, UnitType type) {
        gameMap.removeUnit(nation, location, type);
    }

    public void updateUnit(int nation, int location, int strength) {
        gameMap.updateUnit(nation, location, strength);
    }

    public boolean makeIsHostileChoice(int currentPlayer){

        Alert alert = new Alert(AlertType.CONFIRMATION);

        
        alert.setHeaderText("Would you like these units to be hostile?");
        alert.setTitle("Player " + (currentPlayer+1) + ", decision required:");

        ButtonType passive = new ButtonType("Stay passive");
        alert.getButtonTypes().clear();
		alert.getButtonTypes().add(passive);

        ButtonType hostile = new ButtonType("Become hostile");
        
		alert.getButtonTypes().add(hostile);

        Optional<ButtonType> option = alert.showAndWait();

        if(option.get() == hostile){
            return true;
        }

        return false;
    }

    public int makeBattleChoice(int currentPlayer, int currentNation, int location, ArrayList<Integer> nationsPresent) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Player " + (currentPlayer+1) + " Make Battle Choice");
		alert.setHeaderText("Player " + (currentPlayer+1) + " choose what action you want to take:");

		ButtonType passive = new ButtonType("Stay passive");
        alert.getButtonTypes().clear();
		alert.getButtonTypes().add(passive);
        ButtonType austria = new ButtonType("Battle Yellow");
        ButtonType italy = new ButtonType("Battle Green");
        ButtonType france = new ButtonType("Battle Blue");
        ButtonType britain = new ButtonType("Battle Orange");
        ButtonType germany = new ButtonType("Battle Gray");
        ButtonType russia = new ButtonType("Battle Purple");
        ButtonType[] types = {austria, italy, france, britain, germany, russia};
        for(Integer i : nationsPresent) {
            if(i.intValue() != currentNation) {
                alert.getButtonTypes().add(types[i.intValue()]);
            }
        }

        Optional<ButtonType> option = alert.showAndWait();
		
        if(option.get() == passive) {
            return 0;
        } else if(option.get() == austria) {
            return 1;
        } else if(option.get() == italy) {
            return 2;
        } else if(option.get() == france) {
            return 3;
        } else if(option.get() == britain) {
            return 4;
        } else if(option.get() == germany) {
            return 5;
        } else if(option.get() == russia) {
            return 6;
        }
        return -1;
    }

    public void moveUnitAfterManeuver(int nation, UnitType type, int fromLocation, int toLocation) {
        gameMap.moveUnitAfterManeuver(nation, type, fromLocation, toLocation);
    }

    public void makeImportChoice(int currentPlayer, int currentNation) {
        //System.out.println("makeImportChoice");
        Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Player " + (currentPlayer+1) + " Make import Choice");
		alert.setHeaderText("Player " + (currentPlayer+1) + " choose what action you want to take! \n Each unit costs 1 million out of the nations treasury.");

		ButtonType finish = new ButtonType("Finish Turn");
        alert.getButtonTypes().clear();
		alert.getButtonTypes().add(finish);
        ButtonType army = new ButtonType("Buy Army");
		alert.getButtonTypes().add(army);
        ButtonType ship = new ButtonType("Buy Ship");
		alert.getButtonTypes().add(ship);

        Optional<ButtonType> option = alert.showAndWait();
		
        if(option.get() == finish) {
            controllerGUI.finishedImport();
        } else if(option.get() == army) {
            updateMenuBar(currentPlayer, currentNation, "Choose Location", false);
            int location = gameMap.getUnitImportLocation(UnitType.ARMY);
            gameMap.addUnit(currentNation, 1, location, UnitType.ARMY);
        } else if(option.get() == ship) {
            updateMenuBar(currentPlayer, currentNation, "Choose Location", false);
            int location = gameMap.getUnitImportLocation(UnitType.SHIP);
            gameMap.addUnit(currentNation, 1, location, UnitType.SHIP);
        }
    }

    public boolean isImportLegal(int index, UnitType importType) {
        return controllerGUI.isImportLegal(index, importType);
    }

    public int[] makeBondBuyChoice(int currentPlayer, int currentNation, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCost) {
        InvestorScreen investorScreen = new InvestorScreen(this, possibleActions, associatedCost);
        investorScreen.updateMenuBar(currentPlayer, currentNation, "Make investment choice");
        investorScreen.setRoot(scene);
        return (int[])Platform.enterNestedEventLoop(EVENTKEY);
    }

    public void investorChoiceWasMade(int index, int buyIndex) {
        int[] result = new int[2];
        result[0] = index;
        result[1] = buyIndex;
        goTo();
        Platform.exitNestedEventLoop(EVENTKEY, result);
    }

    public void addFlag(int nation, int elementIndex) {
        gameMap.addFlag(nation, elementIndex);
    }
    
    public void removeFlag(int elementIndex) {
        gameMap.removeFlag(elementIndex);
    }

    public void addSimulationOption() {
        simulationBar = new HBox();
        simulationBar.setPadding(new Insets(10));
        simulationBar.setId("sidechart");
        simulationBar.setAlignment(Pos.CENTER);

        Label tickCounter = new Label("0");
        tickCounter.setAlignment(Pos.CENTER);
        tickCounter.setTextFill(Color.BLACK);
        tickCounter.setStyle("-fx-font-size: " + 11 + "px;");
        tickCounter.setBackground(new Background(new BackgroundFill(Color.MEDIUMSLATEBLUE, new CornerRadii(5.0), new Insets(-5.0))));


        Button tickForWard = new Button("Next Tick");
        tickForWard.setPrefWidth(270);
        tickForWard.setPrefHeight(30);
        tickForWard.setStyle("-fx-font-size: " + 11 + "px;");
        tickForWard.setOnAction(e -> {
            int tick = controllerGUI.nextTick();
            tickCounter.setText(String.valueOf(tick));
        });
        simulationBar.getChildren().add(tickForWard);
    }

    public void updateRondelManually(int currentPlayer, int nation, int position) {
        rondelScreen.updateRondelManually(currentPlayer, nation, position);
    }
}
