package io.kerosenelabs.atcintegrationtest;

public class TerminalUtil {
    public static void clear() {
        System.out.print("\033[H\033[2J");
    }

    public static void clearCurrentLine() {
        System.out.print("\033[2K");
    }

    public static void selectLine(int line) {
        System.out.print("\033[" + line + ";1H");
    }

    public static void printOnLine(String text, int line) {
        System.out.flush();
        selectLine(line);
        clearCurrentLine();
        System.out.print(text);
    }
}
