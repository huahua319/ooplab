package com.example.othello;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class ConsoleUI implements AutoCloseable {
    private final Terminal terminal;
    private int inputLine = 0;
    private String inputPrompt = "Enter move position (e.g. D3), board number (e.g. 2), or quit: ";

    public ConsoleUI(Terminal terminal) throws IOException {
        this.terminal = terminal;
        terminal.enterPrivateMode();
        terminal.setCursorVisible(false);
    }

    public void render(GameSession session, int currentBoardNumber, int totalBoards) throws IOException {
        Board board = session.getBoard();
        Cell currentPlayer = session.getCurrentPlayer();
        String message = session.getStatusMessage();

        terminal.clearScreen();
        int blackCount = board.countCells(Cell.BLACK);
        int whiteCount = board.countCells(Cell.WHITE);

        int line = 0;
        putLine(line++, "Othello");
        putLine(line++, "Current Board: " + currentBoardNumber);
        putLine(line++, buildHeader(board.getSize()));

        for (int row = 0; row < board.getSize(); row++) {
            StringBuilder rowText = new StringBuilder();
            rowText.append(row + 1).append(" ");
            for (int col = 0; col < board.getSize(); col++) {
                rowText.append(board.getCell(row, col).getSymbol()).append(" ");
            }
            putLine(line++, rowText.toString());
        }

        putLine(line++, "");
        putLine(line++, "Current Player: " + playerName(currentPlayer));
        putLine(line++, "Black: " + blackCount + "  White: " + whiteCount);
        putLine(line++, message);
        inputPrompt = "Enter move position (e.g. D3), board number 1-" + totalBoards + " (e.g. 2), or quit: ";
        inputLine = line;
        drawInput("");
    }

    public String readInputLine() throws IOException {
        StringBuilder input = new StringBuilder();

        while (true) {
            KeyStroke keyStroke = terminal.readInput();
            if (keyStroke == null) {
                continue;
            }

            if (keyStroke.getKeyType() == KeyType.Enter) {
                return input.toString().trim();
            }

            if (keyStroke.getKeyType() == KeyType.EOF) {
                return "quit";
            }

            if (keyStroke.getKeyType() == KeyType.Backspace) {
                if (!input.isEmpty()) {
                    input.deleteCharAt(input.length() - 1);
                    drawInput(input.toString());
                }
                continue;
            }

            if (keyStroke.getKeyType() == KeyType.Character) {
                Character character = keyStroke.getCharacter();
                if (character != null && !Character.isISOControl(character)) {
                    input.append(character);
                    drawInput(input.toString());
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        terminal.setCursorVisible(true);
        terminal.exitPrivateMode();
    }

    private void drawInput(String currentInput) throws IOException {
        putLine(inputLine, inputPrompt + currentInput);
        terminal.flush();
    }

    private void putLine(int row, String text) throws IOException {
        TerminalSize terminalSize = terminal.getTerminalSize();
        int width = Math.max(1, terminalSize.getColumns());
        String clipped = text.length() > width ? text.substring(0, width) : text;
        String padded = clipped + " ".repeat(Math.max(0, width - clipped.length()));

        terminal.setCursorPosition(0, row);
        terminal.putString(padded);
    }

    private String buildHeader(int size) {
        StringBuilder header = new StringBuilder("  ");
        for (int col = 0; col < size; col++) {
            header.append((char) ('A' + col)).append(" ");
        }
        return header.toString();
    }

    private String playerName(Cell player) {
        return player.name();
    }
}
