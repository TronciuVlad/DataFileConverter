package org.openjfx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private int width = 1536;
    private int height = 2048;

    private final int MAX_RESOLUTION = 5000;  // Maximum allowed resolution
    private final int DEFAULT_WIDTH = 1536;   // Default width
    private final int DEFAULT_HEIGHT = 2048;  // Default height

    private ImageView imageView;
    private ImageProcessor imageProcessor;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SketchToolX");

        // Create a stack pane to hold the image view and apply border and background
        imageView = new ImageView();
        imageView.setFitWidth(800);
        imageView.setFitHeight(600);
        imageView.setPreserveRatio(true);

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(imageView);
        imageContainer.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: lightgray;");
        imageContainer.setPadding(new Insets(10)); // Add padding around the image container

        imageProcessor = new ImageProcessor(imageView, width, height);

        TextField widthField = new TextField(Integer.toString(width));
        TextField heightField = new TextField(Integer.toString(height));
        widthField.setPrefWidth(80);
        heightField.setPrefWidth(80);

        Label widthLabel = new Label("Width:");
        Label heightLabel = new Label("Height:");

        ChangeListener<String> resolutionChangeListener = (observable, oldValue, newValue) -> {
            try {
                int newWidth = Integer.parseInt(widthField.getText());
                int newHeight = Integer.parseInt(heightField.getText());

                if (newWidth > MAX_RESOLUTION) {
                    widthField.setText(Integer.toString(MAX_RESOLUTION));
                    newWidth = MAX_RESOLUTION;
                }

                if (newHeight > MAX_RESOLUTION) {
                    heightField.setText(Integer.toString(MAX_RESOLUTION));
                    newHeight = MAX_RESOLUTION;
                }

                width = newWidth;
                height = newHeight;
                imageProcessor.setResolution(width, height);
            } catch (NumberFormatException e) {
                widthField.setText(oldValue);
                heightField.setText(oldValue);
            }
        };

        widthField.textProperty().addListener(resolutionChangeListener);
        heightField.textProperty().addListener(resolutionChangeListener);

        Button browseButton = new Button("Browse File");
        browseButton.setOnAction(e -> imageProcessor.browseFile(primaryStage));

        Button saveButton = new Button("Save PNG");
        saveButton.setOnAction(e -> imageProcessor.savePng());

        Button rotateLeftButton = new Button("Rotate Left");
        rotateLeftButton.setOnAction(e -> imageProcessor.rotateLeft());

        Button rotateRightButton = new Button("Rotate Right");
        rotateRightButton.setOnAction(e -> imageProcessor.rotateRight());

        Button flipHorizontalButton = new Button("Flip Horizontal");
        flipHorizontalButton.setOnAction(e -> imageProcessor.flipHorizontal());

        Button flipVerticalButton = new Button("Flip Vertical");
        flipVerticalButton.setOnAction(e -> imageProcessor.flipVertical());

        // Reset Size Button
        Button resetSizeButton = new Button("Reset Size");
        resetSizeButton.setOnAction(e -> {
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
            widthField.setText(Integer.toString(DEFAULT_WIDTH));
            heightField.setText(Integer.toString(DEFAULT_HEIGHT));
            imageProcessor.setResolution(width, height);
        });

        // Layout for the top left (Browse and Save)
        HBox topBox = new HBox(10, browseButton, saveButton);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(10));

        // Layout for the left bottom side (Rotate and Flip) in a 2x2 grid
        VBox rotateFlipBox = new VBox(10, 
            new HBox(10, rotateLeftButton, rotateRightButton), 
            new HBox(10, flipHorizontalButton, flipVerticalButton)
        );
        rotateFlipBox.setPadding(new Insets(10));
        rotateFlipBox.setAlignment(Pos.TOP_LEFT);

        // Layout for the right side (Width, Height, and Reset)
        HBox sizeFields = new HBox(10, widthLabel, widthField, heightLabel, heightField);
        sizeFields.setAlignment(Pos.CENTER_RIGHT);

        VBox rightBox = new VBox(10, sizeFields, resetSizeButton);
        rightBox.setAlignment(Pos.TOP_RIGHT);
        resetSizeButton.setAlignment(Pos.TOP_LEFT);
        rightBox.setPadding(new Insets(10));

        // Add a spacer (Region) to increase space between the left and right panels
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomBox = new HBox(10, rotateFlipBox, spacer, rightBox);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.BOTTOM_CENTER);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(imageContainer);
        root.setBottom(bottomBox);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
