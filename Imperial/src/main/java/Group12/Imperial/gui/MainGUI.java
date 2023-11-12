package Group12.Imperial.gui;

import java.io.Serializable;
import java.util.Objects;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class MainGUI implements Serializable{

    public static final int WINDOW_STARTING_WIDTH = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static final int WINDOW_STARTING_HEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public static void setUpStage(Stage stage, double width, double height) {
        stage.setTitle("Imperial");
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setMaximized(true);

        stage.setOnCloseRequest(e -> System.exit(0));
    }
    
    public static void setUpScene(Scene scene) {
        scene.getStylesheets().add(getStyleSheet());
    }

    public static void quitToDesktopAlert(Stage stage) {
        Alert alert = getDefaultAlert(Alert.AlertType.CONFIRMATION);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Quit");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(0)).setText("Quit to desktop");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(1)).setText("Are you sure you want to quit to the desktop? " + "\nAny unsaved progress will be lost");
        alert.initOwner(stage);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    public static void backToMainMenuAlert(Stage stage) {
        Alert alert = getDefaultAlert(Alert.AlertType.CONFIRMATION);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Back to main menu");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(0)).setText("Back to main menu");
        ((Label)((GridPane)alert.getDialogPane().getContent()).getChildren().get(1)).setText("Are you sure you want to go back to the main menu? " +
                "\nAny unsaved progress will be lost");
        alert.initOwner(stage);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                
                stage.close();
                try {
                    new HomeScreen().start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Alert getDefaultAlert(AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setGraphic(null);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setId("dialog_ok_button");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setPrefWidth(150);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setPrefHeight(30);
        alert.getDialogPane().setPrefWidth(425);
        alert.getDialogPane().setPrefHeight(175);

        GridPane gridPane = new GridPane();
        Label label1 = new Label();
        label1.setId("dialog_content_header");
        Label label2 = new Label();
        label2.setId("dialog_content");
        gridPane.add(label1, 0, 0);
        gridPane.add(label2, 0, 1);
        GridPane.setMargin(label1, new Insets(7, 10, 0, 10));
        GridPane.setMargin(label2, new Insets(15, 10, 0, 10));
        alert.getDialogPane().setContent(gridPane);

        return alert;
    }

    private static String getStyleSheet() {
        return Objects.requireNonNull(MainGUI.class.getResource("/style.css")).toString();
    }
}
