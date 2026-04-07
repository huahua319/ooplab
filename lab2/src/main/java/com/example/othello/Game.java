package com.example.othello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Game {
    private final ConsoleUI ui;
    private final List<GameSession> sessions;
    private int currentBoardIndex;

    public Game(int boardCount, int boardSize, ConsoleUI ui) {
        this.ui = ui;
        this.sessions = createSessions(boardCount, boardSize);
        this.currentBoardIndex = 0;
        currentSession().setStatusMessage("Board 1 selected. BLACK moves first.");
    }

    public String run() throws IOException {
        while (true) {
            prepareCurrentSession();
            ui.render(currentSession(), getCurrentBoardNumber(), sessions.size());
            String rawInput = ui.readInputLine();
            String endMessage = handleInput(rawInput);

            if (endMessage != null) {
                ui.render(currentSession(), getCurrentBoardNumber(), sessions.size());
                return endMessage;
            }
        }
    }

    void prepareCurrentSession() {
        currentSession().prepareTurnIfNeeded();
    }

    String handleInput(String rawInput) {
        GameSession session = currentSession();
        if ("quit".equalsIgnoreCase(rawInput)) {
            session.setStatusMessage("Quit received. Program ended.");
            return "Quit received. Program ended.";
        }

        BoardSwitchValidation boardSwitch = validateBoardSwitch(rawInput);
        if (boardSwitch.boardSwitchInput()) {
            if (!boardSwitch.valid()) {
                session.setStatusMessage(boardSwitch.errorMessage());
                return null;
            }

            currentBoardIndex = boardSwitch.boardIndex();
            GameSession switchedSession = currentSession();
            if (switchedSession.isFinished()) {
                switchedSession.setStatusMessage(
                    "Switched to board " + getCurrentBoardNumber() + ". " + switchedSession.getFinishMessage()
                );
            } else {
                switchedSession.setStatusMessage("Switched to board " + getCurrentBoardNumber() + ".");
            }
            return null;
        }

        if (session.isFinished()) {
            session.setStatusMessage(
                "Board " + getCurrentBoardNumber() + " is finished. Enter a board number or quit."
            );
            return null;
        }

        MoveValidation validation = validateMoveInput(rawInput);
        if (!validation.valid()) {
            session.setStatusMessage(validation.errorMessage());
            return null;
        }

        Position position = validation.position();
        Board board = session.getBoard();
        if (!board.isLegalMove(position, session.getCurrentPlayer())) {
            if (!board.isEmpty(position)) {
                session.setStatusMessage("Invalid move. The cell is not empty.");
            } else {
                session.setStatusMessage("Invalid move. The move must flip at least one opponent piece.");
            }
            return null;
        }

        session.applyMove(position);
        return null;
    }

    int getCurrentBoardNumber() {
        return currentBoardIndex + 1;
    }

    GameSession getSession(int boardNumber) {
        return sessions.get(boardNumber - 1);
    }

    private MoveValidation validateMoveInput(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return MoveValidation.error("Invalid input. Enter a move position like D3, a board number, or quit.");
        }

        int boardSize = currentSession().getBoard().getSize();
        String normalized = rawInput.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() < 2 || normalized.length() > 3) {
            return MoveValidation.error("Invalid input. Enter a move position like D3, a board number, or quit.");
        }

        char colChar = normalized.charAt(0);
        if (colChar < 'A' || colChar >= 'A' + boardSize) {
            return MoveValidation.error("Invalid move position. Column must be A-"
                + (char) ('A' + boardSize - 1) + ".");
        }

        String rowPart = normalized.substring(1);
        int rowNumber;
        try {
            rowNumber = Integer.parseInt(rowPart);
        } catch (NumberFormatException ex) {
            return MoveValidation.error("Invalid move position. Enter a row number like D3.");
        }

        if (rowNumber < 1 || rowNumber > boardSize) {
            return MoveValidation.error("Invalid move position. Row must be 1-" + boardSize + ".");
        }

        int col = colChar - 'A';
        int row = rowNumber - 1;
        return MoveValidation.ok(new Position(row, col));
    }

    private BoardSwitchValidation validateBoardSwitch(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return BoardSwitchValidation.notBoardSwitch();
        }

        String normalized = rawInput.trim();
        if (!normalized.chars().allMatch(Character::isDigit)) {
            return BoardSwitchValidation.notBoardSwitch();
        }

        int boardNumber;
        try {
            boardNumber = Integer.parseInt(normalized);
        } catch (NumberFormatException ex) {
            return BoardSwitchValidation.error("Invalid board number. Enter 1-" + sessions.size() + ".");
        }

        if (boardNumber < 1 || boardNumber > sessions.size()) {
            return BoardSwitchValidation.error("Invalid board number. Enter 1-" + sessions.size() + ".");
        }

        return BoardSwitchValidation.ok(boardNumber - 1);
    }

    private GameSession currentSession() {
        return sessions.get(currentBoardIndex);
    }

    private List<GameSession> createSessions(int boardCount, int boardSize) {
        List<GameSession> createdSessions = new ArrayList<>();
        for (int index = 0; index < boardCount; index++) {
            createdSessions.add(new GameSession(new Board(boardSize)));
        }
        return createdSessions;
    }

    private record MoveValidation(boolean valid, Position position, String errorMessage) {
        static MoveValidation ok(Position position) {
            return new MoveValidation(true, position, "");
        }

        static MoveValidation error(String errorMessage) {
            return new MoveValidation(false, null, errorMessage);
        }
    }

    private record BoardSwitchValidation(boolean boardSwitchInput, boolean valid, int boardIndex, String errorMessage) {
        static BoardSwitchValidation ok(int boardIndex) {
            return new BoardSwitchValidation(true, true, boardIndex, "");
        }

        static BoardSwitchValidation error(String errorMessage) {
            return new BoardSwitchValidation(true, false, -1, errorMessage);
        }

        static BoardSwitchValidation notBoardSwitch() {
            return new BoardSwitchValidation(false, false, -1, "");
        }
    }
}
