package Group12.Imperial.gui;

import java.io.Serializable;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HomeScreen extends Application implements Serializable {

    private final int windowWidth;
    private final int windowHeight;

    private Stage stage;

    public HomeScreen() {
        this.windowWidth = MainGUI.WINDOW_STARTING_WIDTH;
        this.windowHeight = MainGUI.WINDOW_STARTING_HEIGHT;
    }

    public HomeScreen(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        Image icon = new Image(MainGUI.class.getResource("/images/logo.png").toString());
        stage.getIcons().add(icon);

        loadHomeScreen();
    }

    private void loadHomeScreen() {
        Button playButton = new Button("Play Game");
        Button quitButton = new Button("Quit Game");

        VBox vBoxButtons = new VBox(30, playButton, quitButton);
        vBoxButtons.setMaxWidth(500);
        vBoxButtons.setMaxHeight(200);

        vBoxButtons.setAlignment(Pos.CENTER);

        Label title = new Label("IMPERIAL");
        title.setPrefHeight(120);
        title.setPrefWidth(400);
        title.setStyle("-fx-font-size: " + 50 + "px;");
        title.setTextFill(Color.LIGHTGRAY);
        title.setBackground(new Background(new BackgroundFill(Color.DARKRED, new CornerRadii(5.0), new Insets(-5.0))));
        title.setAlignment(Pos.CENTER);

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBoxButtons);
        
        borderPane.setTop(titleBox);
        
        borderPane.setPadding(new Insets(50, 0, 0, 0));

        playButton.setPrefWidth(270);
        playButton.setPrefHeight(80);
        quitButton.setPrefWidth(270);
        quitButton.setPrefHeight(80);

        playButton.setStyle("-fx-font-size: " + 19 + "px;");
        quitButton.setStyle("-fx-font-size: " + 19 + "px;");
        
        Scene scene = new Scene(borderPane, windowWidth, windowHeight);
        MainGUI.setUpStage(stage, windowWidth, windowHeight);
        MainGUI.setUpScene(scene);
        stage.setScene(scene);

        stage.show();

        playButton.setOnAction(e -> new GameSettingsScreen(scene, stage).goTo());
        quitButton.setOnAction(e -> MainGUI.quitToDesktopAlert(stage));
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
