package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.*;

public class BrightnessImgCharMatcher {
    private final Image img;
    private final String font;
    private final HashMap<Image, Double> cache = new HashMap<>();

    /**
     * The class contains all the logic that transposes
     * a color image to ASCII image.
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
    }

    /**
     * Receives the possible chars to use in the ascii image,
     * and a number of chars per row, and returns an ASCII image
     * represented by a nested array of chars.
     * @param numCharsInRow number of chars per row
     * @param charSet Set of possible chars to use in the image
     * @return ASCII image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        HashMap<Character, Double> brightnessLevels = new HashMap<>();

        for (char c: charSet) {
            boolean[][] charImg = CharRenderer.getImg(c, 16, font);
            brightnessLevels.put(c, countTrueValues(charImg) / (numCharsInRow * numCharsInRow));
        }

        linearTransformation(brightnessLevels);
        return convertImageToAscii(brightnessLevels, numCharsInRow);
    }

    /**
     * Receives an array of chars and their brightness values, and
     * calculates a linear stretch for all the values.
     * @param hashMap hashMap with representation of all the
     *                brightness levels for all the chars available.
     */
    private void linearTransformation(HashMap<Character, Double> hashMap) {
        double maxCharBrightness  = (Collections.max(hashMap.values()));
        double minCharBrightness  = (Collections.min(hashMap.values()));

        for (Map.Entry<Character, Double>  entry: hashMap.entrySet()) {
            double newCharBrightness = (entry.getValue()-minCharBrightness)/
                    (maxCharBrightness-minCharBrightness);
            hashMap.put(entry.getKey(), newCharBrightness);
        }
    }

    /**
     * counts the number of `true` values in the array
     * @param arr array of boolean values
     * @return number of true values in the array
     */
    private double countTrueValues(boolean[][] arr) {
        double trueSum = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                if (arr[i][j]) {
                    trueSum++;
                }
            }
        }
        return trueSum;
    }

    /**
     * Receives a color image and calculates the greyscale value for
     * each pixel, sums all the values and returns an average
     * greyscale value for the image.
     * @param image color image
     * @return average greyscale value of image
     */
    private double calculateAverageGreyscale(Image image) {
        int count = 0;
        double sum = 0;
        if(cache.containsKey(image)) {
            return cache.get(image);
        }

        for (Color pixel: image.pixels()){
            double greyPixel = pixel.getRed() * 0.2126 +
                    pixel.getGreen() * 0.7152 + pixel.getBlue() * 0.0722;
            sum += greyPixel;
            count++;
        }

        double averageGreyscale = (sum / 255) / count;

        cache.put(image, averageGreyscale);
        return averageGreyscale;
    }

    /**
     * Receives an image and brightness values of chars, and calculates
     * a char for each subImage, and returns an ASCII image.
     * @param brightnessLevels hashMap with representation of all the
     *                brightness levels for all the chars available.
     * @param numCharsInRow number of chars per row
     * @return ASCII image
     */
    private char[][] convertImageToAscii(HashMap<Character, Double> brightnessLevels,
                                         int numCharsInRow) {
        int pixels = img.getWidth()/numCharsInRow;
        char[][] asciiArt =
                new char[img.getHeight()/pixels][img.getWidth()/pixels];
        int row = 0, col = 0;

        for (Image subImage: img.squareSubImagesOfSize(pixels)) {
            double brightness = calculateAverageGreyscale(subImage);
            asciiArt[row][col] = getClosestCharByBrightness(
                    brightnessLevels, brightness);

            if (col == img.getWidth()/pixels - 1) {
                col = 0;
                row++;
            }
            else {
                col++;
            }
        }

        return asciiArt;
    }

    /**
     * calculates the minimal distance between all the possible brightness
     * levels to the one received, and returns the char that represents
     * that brightness level.
     * @param brightnessLevels hashMap with representation of all the
     *                         brightness levels for all the chars available.
     * @param brightness a double that represents a brightness level
     * @return char from the possible chars to use, that has the
     *         closest value to brightnessLevel
     */
    private char getClosestCharByBrightness(
            HashMap<Character, Double> brightnessLevels, double brightness) {

        Map.Entry<Character, Double> randEntry = brightnessLevels.entrySet().iterator().next();
        char closestChar = (char) randEntry.getKey();
        double minDistance  = Math.abs((double) randEntry.getValue() - brightness);

        for (Map.Entry<Character, Double>  entry: brightnessLevels.entrySet()) {
            double distance = Math.abs((double) entry.getValue() - brightness);
            if(distance < minDistance) {
                minDistance = distance;
                closestChar = entry.getKey();
            }
        }

        return closestChar;
    }
}
