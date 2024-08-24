package org.openjfx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The {@code ImageProcessor} class provides functionality for loading, saving, 
 * and manipulating images within a JavaFX application. This includes operations 
 * such as rotating and flipping images, as well as setting custom resolutions.
 */
public class ImageProcessor {

    private ImageView imageView;
    private BufferedImage currentImage;
    private byte[] originalImageData;

    private int width;
    private int height;
    private int rotationAngle = 0;
    private boolean flipVertical = false;
    private boolean flipHorizontal = false;

    /**
     * Constructs an {@code ImageProcessor} with a given {@code ImageView} and initial width and height.
     *
     * @param imageView the {@code ImageView} where the processed image will be displayed.
     * @param width     the initial width of the image.
     * @param height    the initial height of the image.
     */
    public ImageProcessor(ImageView imageView, int width, int height) {
        this.imageView = imageView;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the resolution (width and height) of the image. If an image is already loaded,
     * it will be recalculated with the new resolution and transformations will be reapplied.
     *
     * @param width  the new width of the image.
     * @param height the new height of the image.
     */
    public void setResolution(int width, int height) {
        this.width = width;
        this.height = height;
        if (originalImageData != null) {
            convertToImage(originalImageData);
            applyTransformations();
        }
    }

    /**
     * Opens a file chooser to allow the user to select an image file. The selected image
     * is loaded and displayed in the {@code ImageView}, with any transformations applied.
     *
     * @param stage the primary stage used to display the file chooser dialog.
     */
    public void browseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                originalImageData = Files.readAllBytes(file.toPath());
                convertToImage(originalImageData);
                applyTransformations();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a file chooser to allow the user to save the current image as a PNG file.
     */
    public void savePng() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PNG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(currentImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Rotates the image 90 degrees counterclockwise and applies the transformation.
     */
    public void rotateLeft() {
        rotationAngle -= 90;
        applyTransformations();
    }

    /**
     * Rotates the image 90 degrees clockwise and applies the transformation.
     */
    public void rotateRight() {
        rotationAngle += 90;
        applyTransformations();
    }

    /**
     * Flips the image vertically and applies the transformation.
     */
    public void flipVertical() {
        flipVertical = !flipVertical;
        applyTransformations();
    }

    /**
     * Flips the image horizontally and applies the transformation.
     */
    public void flipHorizontal() {
        flipHorizontal = !flipHorizontal;
        applyTransformations();
    }

    /**
     * Converts the raw image data into a {@code BufferedImage} with the specified width and height.
     * The image is then displayed in the {@code ImageView}.
     *
     * @param data the raw image data as a byte array.
     */
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

        try {
            File tempFile = File.createTempFile("preview", ".png");
            ImageIO.write(img, "png", tempFile);

            Image image = new Image(new FileInputStream(tempFile));
            imageView.setImage(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the current rotation and flip transformations to the image and updates the display.
     */
    private void applyTransformations() {
        if (currentImage != null) {
            BufferedImage transformedImage = currentImage;

            // Apply rotation and flipping logic
            transformedImage = ImageTransformation.applyTransformations(transformedImage, rotationAngle, flipVertical, flipHorizontal);

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

    /**
     * Converts a byte value to an unsigned integer.
     *
     * @param x the byte value to convert.
     * @return the unsigned integer representation of the byte.
     */
    private int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }
}
