package raf.sk_schedule.engine;

import java.util.Scanner;

public class InputScanner {
    public static final String CLOSING_KEYWORD = "exit";
    Scanner scanner;

    public boolean isOpen;

    public InputScanner() {
        this.scanner = new Scanner(System.in);
        isOpen = true;
    }

    public String nextLine() {
        String nextLine = scanner.nextLine();
        if (nextLine.equalsIgnoreCase(CLOSING_KEYWORD)) {
            isOpen = false;
            throw new RuntimeException("Console reader closed!");
        }

        return nextLine;
    }
}
