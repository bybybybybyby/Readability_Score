package readability;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = "";
        StringBuilder sb = new StringBuilder();
        int syllables = 0;
        int polySyllables = 0;
        int charCount = 0;

        // read in file argument, as String
        try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {
            stream.forEach(sb::append);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            input = sb.toString();
        }

        // Regex that I hope splits after the end of sentence punctuation, and the spaces after
        String sentenceRegex = "[.!?]\\s+?";
        // Regex that should split after a word (any whitespace).
        String wordRegex = "\\s+";
        // Count characters; any visible symbol (not whitespace)
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != ' ') {
                charCount++;
            }
        }

        // split input into sentences
        String[] sentences = input.split(sentenceRegex);
        // create array to count words per sentence
        int[] wordsPerSent = new int[sentences.length];


        // fill how many words per sentence array
        for (int i = 0; i < sentences.length; i++) {
            String[] wordsArr = sentences[i].split(wordRegex);
            wordsPerSent[i] = wordsArr.length;

            // Count sum of syllables in sentence & count polySyllable words (more than 2 syllables)
            for (String word : wordsArr) {
                int currentWordSyllables = syllableCount(word);
                syllables += currentWordSyllables;
                if (currentWordSyllables > 2) {
                    polySyllables++;
                }
            }
        }

        // count words per sentence
        int words = 0;
        for (int count : wordsPerSent) {
            words += count;
        }

        System.out.println("Words: " + words + "\n" +
                "Sentences: " + sentences.length + "\n" +
                "Characters: " + charCount + "\n" +
                "Syllables: " + syllables + "\n" +
                "Polysyllables: " + polySyllables);

        double arIndex = arIndex(charCount, words, sentences.length);
        double fk = fleschKincaid(words, sentences.length, syllables);
        double smog = smogIndex(polySyllables, sentences.length);
        double cl = colemanLiauIndex(charCount, words, sentences.length);

        // Ask user
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String answer = sc.nextLine();
        System.out.println();

        switch (answer) {
            case "ARI":
                System.out.println("Automated Readability Index: " + arIndex +
                        " (about " + getAgeLevel((int) Math.ceil(arIndex)) + " year olds).");
                break;
            case "FK":

                System.out.println("Flesch–Kincaid readability tests: " + fk +
                        " (about " + getAgeLevel(fk) + " year olds).");
                break;
            case "SMOG":
                System.out.println("Simple Measure of Gobbledygook: " + smog + " (about " +
                        getAgeLevel(smog) + " year olds).");
                break;
            case "CL":
                System.out.println("Coleman-Liau index: " + cl + " (about " +
                        getAgeLevel(cl) + " year olds).");
                break;
            case "all":
            default:
                System.out.println("Automated Readability Index: " + arIndex +
                        " (about " + getAgeLevel((int) Math.ceil(arIndex)) + " year olds).");
                System.out.println("Flesch–Kincaid readability tests: " + fk +
                        " (about " + getAgeLevel(fk) + " year olds).");
                System.out.println("Simple Measure of Gobbledygook: " + smog + " (about " +
                        getAgeLevel(smog) + " year olds).");
                System.out.println("Coleman–Liau index: " + cl + " (about " +
                        getAgeLevel(cl) + " year olds).");
                break;
        }

        System.out.println();
        System.out.println("This text should be understood by " + (arIndex + fk + smog + cl) / 4 + " year olds.");

    }


    public static String getAgeLevel(double score) {
        String ageLevel = "";
        int intScore = (int) Math.ceil(score);

        switch (intScore) {
            case 1:
                ageLevel = "6";
            break;
            case 2:
                ageLevel = "7";
            break;
            case 3:
                ageLevel = "9";
                break;
            case 4:
                ageLevel = "10";
                break;
            case 5:
                ageLevel = "11";
                break;
            case 6:
                ageLevel = "12";
                break;
            case 7:
                ageLevel = "13";
                break;
            case 8:
                ageLevel = "14";
                break;
            case 9:
                ageLevel = "15";
                break;
            case 10:
                ageLevel = "16";
                break;
            case 11:
                ageLevel = "17";
                break;
            case 12:
                ageLevel = "18";
                break;
            case 13:
                ageLevel = "24";
                break;
            case 14:
                ageLevel = "24+";
                break;
            default:
                ageLevel = "unknown";
        }
        return  ageLevel;
    }

    public static int syllableCount(String word) {
        int count = 0;
        String regex = "[aeiouyAEIOUY]{1,3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(word);

        while (matcher.find()) {
            count++;
        }

        if (word.substring(word.length() - 1).matches("[eE]")) {
            count--;
        }
        if (count == 0) {
            count = 1;
        }

        return count;
    }

    public static double arIndex(int charCount, int words, int sentences) {
        return (4.71 * charCount / words + 0.5 * words / sentences - 21.43);
    }

    public static double fleschKincaid(int words, int sentences, int syllables) {
        return (0.39 * ((double) words / sentences)) + (11.8 * ((double) syllables / words)) - 15.59;
    }

    public static double smogIndex(int polysyllables, int sentences) {
        return 1.043 * Math.sqrt((polysyllables * (30 / (double) sentences))) + 3.1291;
    }

    public static double colemanLiauIndex(int characters, int words, int sentences) {
        double L = (double) characters / words * 100;
        double S = (double) sentences / words * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }
}

