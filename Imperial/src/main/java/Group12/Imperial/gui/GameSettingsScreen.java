package Group12.Imperial.gui;

import java.io.Serializable;

import Group12.Imperial.gamelogic.Player.PlayerType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameSettingsScreen implements Serializable{

    private Stage stage;
    private Scene scene;
    private BorderPane layout1;
    private BorderPane layout2;

    private PlayerType[] playerTypes;
    private String[] playerNames;
    private boolean readyToPlay = false;
    private boolean readyToSimualte = false;

    public GameSettingsScreen(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
    }

    public void goTo() {
        Button twoPlayers = new Button("2 Players");
        twoPlayers.setPrefWidth(270);
        twoPlayers.setPrefHeight(80);
        twoPlayers.setStyle("-fx-font-size: " + 19 + "px;");
        twoPlayers.setOnAction(e -> createPlayerSetupScreen(2));

        Button threePlayers = new Button("3 Players");
        threePlayers.setPrefWidth(270);
        threePlayers.setPrefHeight(80);
        threePlayers.setStyle("-fx-font-size: " + 19 + "px;");
        threePlayers.setOnAction(e -> createPlayerSetupScreen(3));

        Button sixPlayers = new Button("6 Players");
        sixPlayers.setPrefWidth(270);
        sixPlayers.setPrefHeight(80);
        sixPlayers.setStyle("-fx-font-size: " + 19 + "px;");
        sixPlayers.setOnAction(e -> createPlayerSetupScreen(6));

        Label text = new Label("Please choose the number of players:");
        text.setAlignment(Pos.CENTER);
        text.setTextFill(Color.BLACK);
        text.setStyle("-fx-font-size: " + 22 + "px;");
        text.setBackground(new Background(new BackgroundFill(Color.MEDIUMSLATEBLUE, new CornerRadii(5.0), new Insets(-5.0))));

        HBox buttons = new HBox(30);
        buttons.getChildren().addAll(twoPlayers, threePlayers, sixPlayers);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(70, text, buttons);
        box.setAlignment(Pos.CENTER);

        layout1 = new BorderPane();
        layout1.setCenter(box);
        scene.setRoot(layout1);
    }

    private void createPlayerSetupScreen(int numberPlayers) {
        playerTypes = new PlayerType[numberPlayers];
        playerNames = new String[numberPlayers];

        VBox leftSide = new VBox(20);
        leftSide.setAlignment(Pos.CENTER);
        VBox rightSide = new VBox(20);
        rightSide.setAlignment(Pos.CENTER);

        int counter = 0;
        for(int i = 0; i < numberPlayers; i++) {
            if(counter == 0) {
                leftSide.getChildren().add(createPlayerSetupBox(i));
                counter = 1;
            } else {
                rightSide.getChildren().add(createPlayerSetupBox(i));
                counter = 0;
            }
        }

        Label title = new Label("Choose your player setup:");
        title.setPrefHeight(100);
        title.setPrefWidth(300);
        title.setStyle("-fx-font-size: " + 19 + "px;");
        title.setTextFill(Color.BLACK);
        title.setBackground(new Background(new BackgroundFill(Color.rgb(84, 201, 255), new CornerRadii(5.0), new Insets(-5.0))));
        title.setAlignment(Pos.CENTER);

        Label info = new Label("Note:\nYou can only press start with at least 1 human player \n You can only simulate with no human player");
        info.setPrefHeight(100);
        info.setPrefWidth(300);
        info.setStyle("-fx-font-size: " + 10 + "px;");
        info.setTextFill(Color.BLACK);
        info.setBackground(new Background(new BackgroundFill(Color.rgb(84, 201, 255), new CornerRadii(5.0), new Insets(-5.0))));
        info.setAlignment(Pos.CENTER);

        VBox top = new VBox();
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(title, info);

        HBox playerSetup = new HBox(60);
        playerSetup.getChildren().addAll(leftSide, rightSide);
        playerSetup.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start");
        startButton.setPrefWidth(270);
        startButton.setPrefHeight(80);
        startButton.setStyle("-fx-font-size: " + 19 + "px;");
        startButton.setOnAction(e -> {
            if(readyToPlay) {
                try {
                    new ControllerGUI(numberPlayers, scene, playerTypes, playerNames, stage).start();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        Button simButton = new Button("Simulation");
        simButton.setPrefWidth(270);
        simButton.setPrefHeight(80);
        simButton.setStyle("-fx-font-size: " + 19 + "px;");
        simButton.setOnAction(e -> {
            if(readyToSimualte) {
                try {
                    new ControllerGUI(numberPlayers, scene, playerTypes, playerNames, stage).startSimulation();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setPrefWidth(270);
        backButton.setPrefHeight(80);
        backButton.setStyle("-fx-font-size: " + 19 + "px;");
        backButton.setOnAction(e -> scene.setRoot(layout1));

        HBox bottom = new HBox(30);
        bottom.setPadding(new Insets(0, 0, 40, 0));
        bottom.getChildren().addAll(backButton, startButton, simButton);
        bottom.setAlignment(Pos.CENTER);

        layout2 = new BorderPane();
        layout2.setPadding(new Insets(50, 0, 0, 0));
        layout2.setTop(top);
        layout2.setCenter(playerSetup);
        layout2.setBottom(bottom);
        scene.setRoot(layout2);
    }

    private VBox createPlayerSetupBox(int playerIndex) {
        VBox playerBox = new VBox(15);
        playerBox.setBackground(new Background(new BackgroundFill(Color.BROWN, new CornerRadii(5.0), new Insets(-5.0))));
        playerBox.setPadding(new Insets(15, 15, 15, 15));
        playerBox.setAlignment(Pos.CENTER);

        String name = "Player " + (playerIndex+1);
        TextField nameField = new TextField(name);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            playerNames[playerIndex] = newValue;
        });
        nameField.setPrefSize(270, 50);

        ComboBox<String> playerType = new ComboBox<>();
        playerType.setPrefSize(270, 50);
        playerType.getItems().add("Human Player");
        playerType.getItems().add("Random Agent");
        playerType.getItems().add("MCTS Agent");
        playerType.getItems().add("MCTSDQN Agent");
        playerType.getItems().add("RuleBased Agent");
        playerType.setOnAction(e -> {
            int choiceIndex = playerType.getSelectionModel().getSelectedIndex();
            if(choiceIndex == 0) {
                playerTypes[playerIndex] = PlayerType.HUMAN;
                nameField.setText("Player " + (playerIndex+1));
                playerNames[playerIndex] = "Player " + (playerIndex+1);
            } else if(choiceIndex == 1) {
                playerTypes[playerIndex] = PlayerType.RANDOM;
                nameField.setText("Random Agent " + (playerIndex+1));
                playerNames[playerIndex] = "Random Agent " + (playerIndex+1);
            } else if(choiceIndex == 2) {
                playerTypes[playerIndex] = PlayerType.MCTS;
                nameField.setText("MCTS Agent " + (playerIndex+1));
                playerNames[playerIndex] = "MCTS Agent " + (playerIndex+1);
            } else if(choiceIndex == 3) {
                playerTypes[playerIndex] = PlayerType.MCTSDQN;
                nameField.setText("MCTSDQN Agent " + (playerIndex+1));
                playerNames[playerIndex] = "MCTSDQN Agent " + (playerIndex+1);
            } else {
                playerTypes[playerIndex] = PlayerType.RULE;
                nameField.setText("RuleBased Agent " + (playerIndex+1));
                playerNames[playerIndex] = "RuleBased Agent " + (playerIndex+1);
            }
            checkIfReady();
            
        });
        playerBox.getChildren().addAll(nameField, playerType);

        return playerBox;
    }

    private void checkIfReady() {
        boolean isReady = true;
        boolean isReadyToSimulate = false;
        int humanCounter = 0;
        for(int i = 0; i < playerTypes.length; i++) {
            if(playerTypes[i] == null) {    
                isReady = false;
                return;
            } else if(playerTypes[i] == PlayerType.HUMAN) humanCounter++;
            
        }
        if(humanCounter == 0) {
            isReady = false;
            isReadyToSimulate = true;
        
        }
        
        for(int i = 0; i < playerNames.length; i++) {
            for(int j = 0; j < playerNames.length; j++) {
                if(i != j && playerNames[i] == playerNames[j]) {
                    isReady = false;
                }
            }
        }
        if(isReady) {
            readyToPlay = true;
        } else { 
            readyToPlay = false;
        }
        if(isReadyToSimulate) {
            readyToSimualte = true;
        } else {
            readyToSimualte = false;
        }
    }
      
}
