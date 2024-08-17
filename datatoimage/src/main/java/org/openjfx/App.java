package org.openjfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class App extends Application {

    private static final int width = 1920;
    private static final int height = 2560;

    private ImageView imageView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Data to Image Converter");

        // ImageView for displaying the image
        imageView = new ImageView();
        imageView.setFitWidth(800); // Adjust as needed
        imageView.setFitHeight(600); // Adjust as needed
        imageView.setPreserveRatio(true);

        // Buttons for browsing and saving
        Button browseButton = new Button("Browse File");
        browseButton.setOnAction(e -> browseFile(primaryStage));

        Button saveButton = new Button("Save PNG");
        saveButton.setOnAction(e -> savePng());

        BorderPane root = new BorderPane();
        root.setCenter(imageView);
        root.setBottom(browseButton);
        root.setRight(saveButton);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void browseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            convertToImage(file.toPath());
        }
    }

    private void convertToImage(Path filePath) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        try {
            byte[] data = Files.readAllBytes(filePath);

            int r, g, b, a, p, x, y;

            for (int i = 0; i < data.length; i += 4) {
                r = toUnsignedInt(data[i]);
                g = toUnsignedInt(data[i + 1]);
                b = toUnsignedInt(data[i + 2]);
                a = toUnsignedInt(data[i + 3]);

                p = (a << 24) | (r << 16) | (g << 8) | b;
                x = (i / 4) % (width / 5 * 4);
                y = (i / 4) / (width / 5 * 4);

                img.setRGB(x, y, p);
            }

            // Save the image to a temporary file and display in the ImageView
            File tempFile = File.createTempFile("preview", ".png");
            ImageIO.write(img, "png", tempFile);

            Image image = new Image(new FileInputStream(tempFile));
            imageView.setImage(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePng() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PNG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png")); // Ensures saving as PNG
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                // Convert the JavaFX Image to BufferedImage
                Image image = imageView.getImage();
                BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);

                // Draw the JavaFX image onto the BufferedImage
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int argb = image.getPixelReader().getArgb(x, y);
                        bufferedImage.setRGB(x, y, argb);
                    }
                }

                // Save the BufferedImage to the chosen file
                ImageIO.write(bufferedImage, "png", file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }
}
