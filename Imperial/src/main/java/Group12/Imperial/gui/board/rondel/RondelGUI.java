package Group12.Imperial.gui.board.rondel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import Group12.Imperial.gui.EventKey;
import Group12.Imperial.gui.GameScreen;
import Group12.Imperial.gui.PathContainer;
import Group12.Imperial.gui.board.listener.RondelListener;
import Group12.Imperial.gui.board.listener.MapElementListener.Event;
import Group12.Imperial.gui.board.sidecharts.PlayerTreasuryGUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

public class RondelGUI implements Serializable{

    public enum RondelChoice implements Serializable{
        IMPORT,
        PRODUCTION,
        MANEUVER,
        TAXATION,
        FACTORY,
        INVESTOR
    }
    
    private NationPieceRondelGUI[] playersRondel;
    private RondelElement[] elements;
    private Image rondelBackground;
    private int playerCount;
    private RondelListener listener;

    private GameScreen gameScreen;
    private VBox layout;

    private long lastEvent;
    private NationPieceRondelGUI lastEventPiece;
    private final EventKey EVENTKEY = new EventKey();
    private Label currentPlayerLabel;
    private Label currentPhaseLabel;

    private Color[] nationColours = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.ORANGERED, Color.DARKGRAY, Color.PURPLE};
    private PlayerTreasuryGUI playerTreasury;

    public RondelGUI(int playerCount, GameScreen gameScreen) {
        this.playerCount = playerCount;
        this.gameScreen = gameScreen;
        init();
    }

    private void init() { 
        playersRondel = new NationPieceRondelGUI[6];
        listener = new RondelListener(this);

        createElements();

        playerTreasury = new PlayerTreasuryGUI(playerCount);

        rondelBackground = new Image(RondelGUI.class.getResource("/images/rondel.png").toString());
        ImageView image = new ImageView(rondelBackground);
        StackPane pane = new StackPane();

        Group group = new Group();
        group.getChildren().add(image);
        group.getChildren().addAll(getPaths());
        group.getChildren().addAll(getPieces());
        
        Group wrapper = new Group();
        wrapper.getChildren().add(group);
        pane.getChildren().addAll(wrapper);
        pane.setId("rondel_center");

        pane.setOnMouseReleased(e -> listener.wasReleased());

        HBox menuBar = createMenuBar();

        HBox middle = new HBox();
        middle.getChildren().addAll(playerTreasury.getLeft(), pane, playerTreasury.getRight());
        HBox.setHgrow(pane, Priority.ALWAYS);

        layout = new VBox();
        layout.getChildren().addAll(menuBar, middle);
        layout.setAlignment(Pos.CENTER);
        VBox.setVgrow(middle, Priority.SOMETIMES);
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

        Button rondelButton = new Button("Game Board");
        rondelButton.setPrefHeight(30);
        rondelButton.setPrefWidth(200);
        rondelButton.setStyle("-fx-font-size: " + 19 + "px;");
        rondelButton.setOnAction(e -> gameScreen.goTo());

        currentPhaseLabel = new Label("null");
        currentPhaseLabel.setPrefHeight(30);
        currentPhaseLabel.setPrefWidth(280);
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

    public void updatePlayerTreasury(int playerIndex, int money, String[] bondsText) {
        playerTreasury.updatePlayerTreasury(playerIndex, money, bondsText);
    }

    public void setRoot(Scene scene) {
        scene.setRoot(layout);
    }

    public int getDecision (int nation) {
        lockPieces(nation);
        return (int)Platform.enterNestedEventLoop(EVENTKEY);
    }

    private void lockPieces(int exception) {
        for (int i = 0; i < playersRondel.length; i++) {
            playersRondel[i].lock();
        }
        playersRondel[exception].unlock();
    }

    private void lockPieces() {
        for (int i = 0; i < playersRondel.length; i++) {
            playersRondel[i].lock();
        }
    }

    private SVGPath[] getPaths() {
        SVGPath[] paths = new SVGPath[elements.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = elements[i].svgPath;
        }
        return paths;
    }

    private Circle[] getPieces() {
        Circle[] paths = new Circle[playersRondel.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = playersRondel[i].getElement();
        }
        return paths;
    }

    private Point2D[] readPosFile(int length, String filePath) {
        Point2D[] positions = new Point2D[length];
        try {
            Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(filePath));
            int counter = 0;
            while(scanner.hasNextLine()) {
                Scanner row = new Scanner(scanner.nextLine());
                row.useDelimiter(",");
                positions[counter] = new Point2D(Double.parseDouble(row.next()), Double.parseDouble(row.next()));
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return positions;
    }

    public void eventHappenedElement(RondelElement element, Event event) {
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lastEvent);
        if (elapsedTime < 10) {
            if (element == null) {
                lastEventPiece.setToLastPosition();
            } else if (gameScreen.isRondelMoveLegal(element.getIndex())) {
                element.relocatePiece(lastEventPiece.getPlayer());
                lastEventPiece.setLastRondelPos(element.getIndex());
                lockPieces();
                Platform.exitNestedEventLoop(EVENTKEY, element.getIndex());
            }
        }
    }

    public void eventHappenedPlayer(NationPieceRondelGUI element, Event event) {
        lastEvent = System.nanoTime();
        lastEventPiece = element;
    }

    private void createElements() {
        elements = new RondelElement[8];
        Point2D[] pos = readPosFile(8, "positions_rondel.csv");
        Point2D[] playerPos = readPosFile(54, "positions_rondel_players.csv");
        elements[0] = new RondelElement(0, PathContainer.rondel1, RondelChoice.MANEUVER, pos[0], Arrays.copyOfRange(playerPos, 0, 6), listener);
        elements[1] = new RondelElement(1, PathContainer.rondel2, RondelChoice.TAXATION, pos[1], Arrays.copyOfRange(playerPos, 6, 12), listener);
        elements[2] = new RondelElement(2, PathContainer.rondel3, RondelChoice.FACTORY, pos[2], Arrays.copyOfRange(playerPos, 12, 18), listener);
        elements[3] = new RondelElement(3, PathContainer.rondel4, RondelChoice.PRODUCTION, pos[3], Arrays.copyOfRange(playerPos, 18, 24), listener);
        elements[4] = new RondelElement(4, PathContainer.rondel5, RondelChoice.MANEUVER, pos[4], Arrays.copyOfRange(playerPos, 24, 30), listener);
        elements[5] = new RondelElement(5, PathContainer.rondel6, RondelChoice.INVESTOR, pos[5], Arrays.copyOfRange(playerPos, 30, 36), listener);
        elements[6] = new RondelElement(6, PathContainer.rondel7, RondelChoice.IMPORT, pos[6], Arrays.copyOfRange(playerPos, 36, 42), listener);
        elements[7] = new RondelElement(7, PathContainer.rondel8, RondelChoice.PRODUCTION, pos[7], Arrays.copyOfRange(playerPos, 42, 48), listener);

        playersRondel[0] = new NationPieceRondelGUI(0, Color.YELLOW, 25, playerPos[48], listener);
        playersRondel[1] = new NationPieceRondelGUI(1, Color.GREEN, 25, playerPos[49], listener);
        playersRondel[2] = new NationPieceRondelGUI(2, Color.BLUE, 25, playerPos[50], listener);
        playersRondel[3] = new NationPieceRondelGUI(3, Color.ORANGERED, 25, playerPos[51], listener);
        playersRondel[4] = new NationPieceRondelGUI(4, Color.DARKGRAY, 25, playerPos[52], listener);
        playersRondel[5] = new NationPieceRondelGUI(5, Color.PURPLE, 25, playerPos[53], listener);
        
        for (RondelElement element : elements) {
            element.givePlayers(playersRondel);
        }
    }

    public void updateRondelManually(int currentPlayer, int nation, int position) {
        lockPieces();
        elements[position].relocatePiece(nation);
        playersRondel[nation].setLastRondelPos(position);
        updateMenuBar(currentPlayer, nation, elements[position].type.toString());
    }

}
