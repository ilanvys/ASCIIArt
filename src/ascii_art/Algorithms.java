package ascii_art;

import java.util.HashSet;
import java.util.Set;

public class Algorithms {
    /**
     * Receives a list of numbers with length n+1,
     * containing all the numbers from 1 to n, with 1
     * number appearing more than once and finds that
     * number.
     * @param numList a list of numbers from 1-n
     *                with 1 number appearing more than
     *                once.
     * @return the number that appears more than once
     *         in the list.
     */
    public static int findDuplicate(int[] numList){
        int slow = numList[0];
        int fast = numList[numList[0]];

        while (fast != slow) {
            slow = numList[slow];
            fast = numList[numList[fast]];
        }

        slow = 0;
        while (fast != slow) {
            slow = numList[slow];
            fast = numList[fast];
        }
        return slow;
    }

    /**
     * Receives a list of words and counts how many unique
     * combinations of those words in morse appear in the list.
     * @param words list of words
     * @return the number of unique morse code combinations after
     *         translating all the words to morse.
     */
    public static int uniqueMorseRepresentations(String[] words) {
        String[] morseCodes = {".-","-...","-.-.","-..",".","..-.","--.","....","..",
            ".---","-.",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-","...-",".--",
            "-..-","-.--","--.."};
        String morseWord = "";
        Set<String> wordsInMorse = new HashSet<String>(){};

        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                morseWord += morseCodes[words[i].charAt(j) - 'a'];
            }
            wordsInMorse.add(morseWord);
            morseWord = "";
        }

        return wordsInMorse.size();
    }
}

