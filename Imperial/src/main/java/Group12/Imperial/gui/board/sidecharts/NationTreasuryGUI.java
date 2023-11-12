package Group12.Imperial.gui.board.sidecharts;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NationTreasuryGUI implements Serializable{

    private VBox layout;
    private Text[] treasuries;

    public NationTreasuryGUI() {
        init();
    }

    public VBox getLayout() { return layout; }

    private void init() {
        treasuries = new Text[6];
        layout = new VBox(20);
        layout.setPrefWidth(200);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(0, 5, 0, 5));
        layout.setId("sidechart");

        Text title = new Text("Nation Treasuries");
        title.setStyle("-fx-font-size: " + 22 + "px;");
        title.setFill(Color.BLACK);
        title.setUnderline(true);

        VBox AU = createNationTreasury(0, "Austria Hungary", "/images/AU-flag.png", Color.YELLOW);
        VBox IT = createNationTreasury(1, "Italy", "/images/IT-flag.png", Color.GREEN);
        VBox FR = createNationTreasury(2, "France", "/images/FR-flag.png", Color.BLUE);
        VBox GB = createNationTreasury(3, "Great Britain", "/images/GB-flag.png", Color.ORANGERED);
        VBox GE = createNationTreasury(4, "Germany", "/images/GE-flag.png", Color.DARKGRAY);
        VBox RU = createNationTreasury(5, "Russia", "/images/RU-flag.png", Color.PURPLE);

        layout.getChildren().addAll(title, AU, IT, FR, GB, GE, RU);
    }

    private VBox createNationTreasury(int index, String titleText, String imagePath, Color color) {
        VBox nationTreasury = new VBox(6);
        nationTreasury.setAlignment(Pos.CENTER);

        Label title = new Label();
        title.setText(titleText);
        title.setTextFill(Color.BLACK);
        title.setBackground(new Background(new BackgroundFill(color, new CornerRadii(5.0), new Insets(-5.0))));
        title.setStyle("-fx-font-size: " + 19 + "px;");
        title.getStyleClass().add("outlined");
        title.setAlignment(Pos.CENTER);
        title.setPrefHeight(15);
        title.setPrefWidth(185);
        title.setId("nations_title");

        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toString());
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(90);
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(false);
        imageView.setId("nation_image");

        Text treasury = new Text();
        treasury.setText("0");
        treasury.setFill(Color.BLACK);
        treasury.setId("treasury_amount");
        treasuries[index] = treasury;

        Text treasuryEnding = new Text();
        treasuryEnding.setText("Million");
        treasuryEnding.setFill(Color.BLACK);
        treasuryEnding.setId("treasury_amount");

        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(imageView, treasury, treasuryEnding);

        nationTreasury.getChildren().addAll(title, box);

        return nationTreasury;
    }
    
    public void updateNationTreasury(int nation, int money) {
        treasuries[nation].setText(Integer.toString(money));
    }
}
