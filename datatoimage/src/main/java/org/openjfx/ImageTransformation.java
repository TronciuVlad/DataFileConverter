package org.openjfx;

import java.awt.image.BufferedImage;

/**
 * The {@code ImageTransformation} class provides static methods for performing
 * common image transformations such as rotation and flipping. These operations 
 * are applied to a {@code BufferedImage} and the transformed image is returned.
 */
public class ImageTransformation {

    /**
     * Applies the specified transformations (rotation and flipping) to the given image.
     *
     * @param image         the {@code BufferedImage} to be transformed.
     * @param rotationAngle the rotation angle in degrees. Valid values are typically 0, 90, 180, or 270.
     * @param flipVertical  whether to flip the image vertically.
     * @param flipHorizontal whether to flip the image horizontally.
     * @return the transformed {@code BufferedImage} after applying the specified transformations.
     */
    public static BufferedImage applyTransformations(BufferedImage image, int rotationAngle, boolean flipVertical, boolean flipHorizontal) {
        BufferedImage transformedImage = image;

        int angle = (rotationAngle % 360 + 360) % 360;
        if (angle == 90 || angle == 270) {
            transformedImage = rotate90Degrees(transformedImage, angle == 90);
        } else if (angle == 180) {
            transformedImage = rotate180Degrees(transformedImage);
        }

        if (flipVertical) {
            transformedImage = flipImageVertically(transformedImage);
        }
        if (flipHorizontal) {
            transformedImage = flipImageHorizontally(transformedImage);
        }

        return transformedImage;
    }

    /**
     * Rotates the given image by 90 degrees either clockwise or counterclockwise.
     *
     * @param img       the {@code BufferedImage} to be rotated.
     * @param clockwise whether to rotate the image clockwise. If {@code false}, the image is rotated counterclockwise.
     * @return the rotated {@code BufferedImage}.
     */
    private static BufferedImage rotate90Degrees(BufferedImage img, boolean clockwise) {
        int newWidth = img.getHeight();
        int newHeight = img.getWidth();
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int newX = clockwise ? y : img.getHeight() - 1 - y;
                int newY = clockwise ? img.getWidth() - 1 - x : x;
                rotatedImage.setRGB(newX, newY, img.getRGB(x, y));
            }
        }

        return rotatedImage;
    }

    /**
     * Rotates the given image by 180 degrees.
     *
     * @param img the {@code BufferedImage} to be rotated.
     * @return the rotated {@code BufferedImage}.
     */
    private static BufferedImage rotate180Degrees(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedImage.setRGB(width - 1 - x, height - 1 - y, img.getRGB(x, y));
            }
        }

        return rotatedImage;
    }

    /**
     * Flips the given image vertically.
     *
     * @param img the {@code BufferedImage} to be flipped vertically.
     * @return the vertically flipped {@code BufferedImage}.
     */
    private static BufferedImage flipImageVertically(BufferedImage img) {
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

    /**
     * Flips the given image horizontally.
     *
     * @param img the {@code BufferedImage} to be flipped horizontally.
     * @return the horizontally flipped {@code BufferedImage}.
     */
    private static BufferedImage flipImageHorizontally(BufferedImage img) {
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
}
