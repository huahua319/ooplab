package com.example.othello;

import java.io.IOException;
import java.util.Locale;

public class Game {
    private final Board board;
    private final ConsoleUI ui;
    private Cell currentPlayer;

    public Game(Board board, ConsoleUI ui) {
        this.board = board;
        this.ui = ui;
        this.currentPlayer = Cell.BLACK;
    }

    public String run() throws IOException {
        String message = "Enter a coordinate and press Enter.";

        while (true) {
            if (board.isFull()) {
                String endMessage = "Game over: board is full.";
                ui.render(board, currentPlayer, endMessage);
                return endMessage;
            }

            ui.render(board, currentPlayer, message);
            String rawInput = ui.readInputLine();

            if ("quit".equalsIgnoreCase(rawInput)) {
                String endMessage = "Game exited.";
                ui.render(board, currentPlayer, endMessage);
                return endMessage;
            }

            MoveValidation validation = validateInput(rawInput);
            if (!validation.valid()) {
                message = validation.errorMessage();
                continue;
            }

            Position position = validation.position();
            if (!board.isEmpty(position)) {
                message = "Target cell is not empty. Try again.";
                continue;
            }

            // This lab only places pieces on empty cells; no flipping logic yet.
            board.place(position, currentPlayer);
            currentPlayer = currentPlayer.oppositePlayer();
            message = "Move accepted.";
        }
    }

    private MoveValidation validateInput(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return MoveValidation.error("Input cannot be empty. Use format like D4.");
        }

        String normalized = rawInput.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() < 2 || normalized.length() > 3) {
            return MoveValidation.error("Invalid format. Use format like D4.");
        }

        char colChar = normalized.charAt(0);
        if (colChar < 'A' || colChar >= 'A' + board.getSize()) {
            return MoveValidation.error("Column out of range. Use A to "
                + (char) ('A' + board.getSize() - 1) + ".");
        }

        String rowPart = normalized.substring(1);
        int rowNumber;
        try {
            rowNumber = Integer.parseInt(rowPart);
        } catch (NumberFormatException ex) {
            return MoveValidation.error("Row must be a number. Use format like D4.");
        }

        if (rowNumber < 1 || rowNumber > board.getSize()) {
            return MoveValidation.error("Row out of range. Use 1 to " + board.getSize() + ".");
        }

        int col = colChar - 'A';
        int row = rowNumber - 1;
        return MoveValidation.ok(new Position(row, col));
    }

    private record MoveValidation(boolean valid, Position position, String errorMessage) {
        static MoveValidation ok(Position position) {
            return new MoveValidation(true, position, "");
        }

        static MoveValidation error(String errorMessage) {
            return new MoveValidation(false, null, errorMessage);
        }
    }
}
