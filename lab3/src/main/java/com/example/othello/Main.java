package com.example.othello;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String finalMessage = "Program ended.";

        try (Terminal terminal = createTerminalWithWindowsFallback();
             ConsoleUI ui = new ConsoleUI(terminal)) {
            GameManager gameManager = new GameManager(ui);
            finalMessage = gameManager.run();
        } catch (IOException ex) {
            printStartupError(ex);
            return;
        }

        System.out.println(finalMessage);
    }

    private static Terminal createTerminalWithWindowsFallback() throws IOException {
        DefaultTerminalFactory textFactory = new DefaultTerminalFactory()
            .setForceTextTerminal(true)
            .setPreferTerminalEmulator(false);

        try {
            return textFactory.createTerminal();
        } catch (IOException ex) {
            // Lanterna on Windows may fail to create text terminal and suggest javaw.
            // In that case, fallback to Swing/AWT terminal emulator.
            if (isWindows() && safeMessage(ex).contains("use javaw")) {
                return new DefaultTerminalFactory()
                    .setForceTextTerminal(false)
                    .setPreferTerminalEmulator(true)
                    .setTerminalEmulatorTitle("OOP Lab 3")
                    .createTerminal();
            }
            throw ex;
        }
    }

    private static void printStartupError(IOException ex) {
        System.err.println("Program failed while initializing the terminal UI.");
        System.err.println("Reason: " + safeMessage(ex));

        if (isWindows()) {
            System.err.println("Diagnostics for Windows:");
            System.err.println("- This project requires JNA + JNA Platform for Lanterna terminal backend.");
            System.err.println("- Re-run: mvn clean compile exec:java");
            System.err.println("- If it still fails, run in Windows PowerShell/Command Prompt (not IDE output console).");
            if (!isClassPresent("com.sun.jna.Native") || !isClassPresent("com.sun.jna.platform.win32.Kernel32")) {
                System.err.println("- JNA classes are missing at runtime. Run 'mvn clean compile' to download dependencies.");
            }
        }
    }

    private static String safeMessage(Exception ex) {
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return ex.getMessage();
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        return osName.contains("win");
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
