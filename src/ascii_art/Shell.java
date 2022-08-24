package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Shell {
    private enum Output { HTML, CONSOLE }

    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final String INITIAL_CHARS_RANGE = "0-9";
    private static final String CMD_EXIT = "exit";
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";

    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;

    private Output output = Output.HTML;
    private final HtmlAsciiOutput htmlOut = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
    private final ConsoleAsciiOutput consoleOut = new ConsoleAsciiOutput();

    private final Set<Character> chars = new HashSet<>();
    private final BrightnessImgCharMatcher charMatcher;

    /**
     * The class contains the logic for an interactive shell
     * for users, to configure the Ascii image
     * @param img an image we want to create Ascii
     */
    public Shell(Image img) {
        addChars(INITIAL_CHARS_RANGE);

        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);

        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
    }

    /**
     * Runs and manages the interactive shell.
     * Stops when the user input is `CMD_EXIT`
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> ");
        String cmd = scanner.nextLine().trim();
        String[] words = cmd.split("\\s+");

        while(!words[0].equals(CMD_EXIT)) {
            try {
                if (!words[0].equals("")) {
                    String param = "";
                    if (words.length > 1) {
                        param = words[1];
                    }
                    switch (words[0]) {
                        case "chars":
                            showChars();
                            break;
                        case "add":
                            addChars(param);
                            break;
                        case "remove":
                            removeChars(param);
                            break;
                        case "res":
                            resChange(param);
                            break;
                        case "console":
                            output = Output.CONSOLE;
                            break;
                        case "render":
                            render();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid input!");
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.print(">>> ");
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }

    /**
     * prints the chars currently available for use
     */
    private void showChars() {
        chars.stream().sorted().forEach(c-> System.out.print(c + " "));
        System.out.println();
    }

    /**
     * calculates a range between to chars to add to the char Set.
     * @param s the user input after requesting to add or remove
     *          characters.
     * @return a range of two chars to insert all
     * the chars between them
     */
    private static char[] parseCharRange(String s) {
        if(s.length() == 1) {
            char c = s.charAt(0);
            return new char[]{c, c};
        }
        if(s.equals("all")) {
            return new char[]{' ', '~'};
        }
        if(s.equals("space")) {
            return new char[]{' ', ' '};
        }
        if(s.length() == 3 && s.indexOf('-') == 1) {
            char firstChar = s.charAt(0);
            char secondChar = s.charAt(2);
            if (firstChar < secondChar) {
                return new char[]{firstChar, secondChar};
            }
            else {
                return new char[]{secondChar, firstChar};
            }
        }
        throw new IllegalArgumentException("Invalid char range! Couldn't Parse");
    }

    /**
     * adds chars upon user's request
     * @param s user input for adding chars to use
     */
    private void addChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null){
            for (int i = range[0]; i <= range[1]; i++) {
                chars.add((char) i);
            }
        }
    }

    /**
     * removes chars upon user's request
     * @param s user input for removing chars to use
     */
    private void removeChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null){
            for (int i = range[0]; i <= range[1]; i++) {
                chars.remove((char) i);
            }
        }
    }

    /**
     * Change the resolution of the Ascii image that will be created
     * @param s the required action to perform on the
     *          Ascii image resolution
     */
    private void resChange(String s) {
        if(s.equals("up") || s.equals("down")) {
            if(s.equals("up")) {
                if(charsInRow * 2 > maxCharsInRow) {
                    System.out.println("Max Resolution Reached");
                }
                else {
                    charsInRow *= 2;
                    System.out.println(("Width set to " + charsInRow)
                            .replace("\r",""));
                }
            }
            if(s.equals("down")) {
                if(charsInRow / 2 < minCharsInRow) {
                    System.out.println("Min Resolution Reached");
                }
                else {
                    charsInRow /= 2;
                    System.out.println(("Width set to " + charsInRow)
                            .replace("\r",""));
                }
            }
        }
        else {
            throw new IllegalArgumentException("Invalid Command for resolution change");
        }
    }

    /**
     * calls the charMatcher and renders the Ascii image to the
     * current chosen output method.
     */
    private void render() {
        if(chars.size() > 0) {
            // cast the chars to array of type Character[] to pass the submission tests
            Character[] charSet = chars.toArray(new Character[0]);
            char[][] asciiArt = charMatcher.chooseChars(charsInRow, charSet);

            if(output == Output.HTML) {
                htmlOut.output(asciiArt);
            }
            if(output == Output.CONSOLE) {
                consoleOut.output(asciiArt);
            }
        }
    }
}
