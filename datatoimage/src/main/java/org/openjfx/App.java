package org.openjfx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class App extends Application {

    private int width = 1536;
    private int height = 2048;

    private ImageView imageView;
    private BufferedImage currentImage;
    private byte[] originalImageData;

    private int rotationAngle = 0;  // To store the current rotation angle
    private boolean flipVertical = false;  // To store the vertical flip state
    private boolean flipHorizontal = false;  // To store the horizontal flip state

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SketchToolX");

        // ImageView for displaying the image
        imageView = new ImageView();
        imageView.setFitWidth(800); // Adjust as needed
        imageView.setFitHeight(600); // Adjust as needed
        imageView.setPreserveRatio(true);

        // TextFields for custom resolution
        TextField widthField = new TextField(Integer.toString(width));
        TextField heightField = new TextField(Integer.toString(height));

        widthField.setPrefWidth(80);
        heightField.setPrefWidth(80);

        Label widthLabel = new Label("Width:");
        Label heightLabel = new Label("Height:");

        // Add listeners to update the resolution
        ChangeListener<String> resolutionChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    width = Integer.parseInt(widthField.getText());
                    height = Integer.parseInt(heightField.getText());
                    // Recalculate the image with the new resolution if data exists
                    if (originalImageData != null) {
                        convertToImage(originalImageData);
                        applyTransformations();  // Reapply the transformations after recalculation
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    widthField.setText(oldValue);
                    heightField.setText(oldValue);
                }
            }
        };

        widthField.textProperty().addListener(resolutionChangeListener);
        heightField.textProperty().addListener(resolutionChangeListener);

        // Buttons for browsing, saving, rotating, and flipping
        Button browseButton = new Button("Browse File");
        browseButton.setOnAction(e -> browseFile(primaryStage));

        Button saveButton = new Button("Save PNG");
        saveButton.setOnAction(e -> savePng());

        Button rotateLeftButton = new Button("Rotate Left");
        rotateLeftButton.setOnAction(e -> {
            rotationAngle -= 90;
            applyTransformations();
        });

        Button rotateRightButton = new Button("Rotate Right");
        rotateRightButton.setOnAction(e -> {
            rotationAngle += 90;
            applyTransformations();
        });

        Button flipVerticalButton = new Button("Flip Vertical");
        flipVerticalButton.setOnAction(e -> {
            flipVertical = !flipVertical;
            applyTransformations();
        });

        Button flipHorizontalButton = new Button("Flip Horizontal");
        flipHorizontalButton.setOnAction(e -> {
            flipHorizontal = !flipHorizontal;
            applyTransformations();
        });

        HBox buttonBox = new HBox(10, browseButton, saveButton, rotateLeftButton, rotateRightButton, flipVerticalButton, flipHorizontalButton, widthLabel, widthField, heightLabel, heightField);
        buttonBox.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setCenter(imageView);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void browseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                originalImageData = Files.readAllBytes(file.toPath());
                convertToImage(originalImageData);
                applyTransformations();  // Apply transformations after loading a new image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertToImage(byte[] data) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int r, g, b, a, p, x, y;

        for (int i = 0; i < data.length; i += 4) {
            r = toUnsignedInt(data[i]);
            g = toUnsignedInt(data[i + 1]);
            b = toUnsignedInt(data[i + 2]);
            a = toUnsignedInt(data[i + 3]);

            p = (a << 24) | (r << 16) | (g << 8) | b;
            x = (i / 4) % width;
            y = (i / 4) / width;

            if (x < width && y < height) {
                img.setRGB(x, y, p);
            }
        }

        currentImage = img;

        // Save the image to a temporary file and display in the ImageView
        try {
            File tempFile = File.createTempFile("preview", ".png");
            ImageIO.write(img, "png", tempFile);

            Image image = new Image(new FileInputStream(tempFile));
            imageView.setImage(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyTransformations() {
        if (currentImage != null) {
            BufferedImage transformedImage = currentImage;

            // Apply rotation
            int angle = (rotationAngle % 360 + 360) % 360;  // Normalize the angle
            if (angle == 90 || angle == 270) {
                int newWidth = transformedImage.getHeight();
                int newHeight = transformedImage.getWidth();
                BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < transformedImage.getHeight(); y++) {
                    for (int x = 0; x < transformedImage.getWidth(); x++) {
                        int newX = angle == 90 ? y : transformedImage.getHeight() - 1 - y;
                        int newY = angle == 90 ? transformedImage.getWidth() - 1 - x : x;
                        rotatedImage.setRGB(newX, newY, transformedImage.getRGB(x, y));
                    }
                }

                transformedImage = rotatedImage;
            } else if (angle == 180) {
                int width = transformedImage.getWidth();
                int height = transformedImage.getHeight();
                BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        rotatedImage.setRGB(width - 1 - x, height - 1 - y, transformedImage.getRGB(x, y));
                    }
                }

                transformedImage = rotatedImage;
            }

            // Apply flipping
            if (flipVertical) {
                transformedImage = flipImageVertically(transformedImage);
            }
            if (flipHorizontal) {
                transformedImage = flipImageHorizontally(transformedImage);
            }

            // Display the transformed image
            try {
                File tempFile = File.createTempFile("transformedPreview", ".png");
                ImageIO.write(transformedImage, "png", tempFile);
                Image image = new Image(new FileInputStream(tempFile));
                imageView.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage flipImageVertically(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                flippedImage.setRGB(x, height - 1 - y, img.getRGB(x, y));
            }
        }

        return flippedImage;
    }

    private BufferedImage flipImageHorizontally(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                flippedImage.setRGB(width - 1 - x, y, img.getRGB(x, y));
            }
        }

        return flippedImage;
    }

    private void savePng() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PNG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png")); // Ensures saving as PNG
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(currentImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }
}
