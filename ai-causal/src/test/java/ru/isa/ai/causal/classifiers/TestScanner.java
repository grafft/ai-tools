package ru.isa.ai.causal.classifiers;

import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Author: Aleksandr Panov
 * Date: 30.07.13
 * Time: 16:36
 */
public class TestScanner {
    public static void main(String[] args) {
        String s = "[attr=4.3..12.000]";
        Scanner scanner = new Scanner(s);
        scanner.useLocale(Locale.US);
        scanner.useDelimiter(Pattern.compile("\\s|=|(\\.\\.)"));
        while (scanner.hasNext()) {
            System.out.println(scanner.next());
        }
    }
}
