package com.example.othello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GameManager {
    private static final int BOARD_SIZE = 8;

    private final ConsoleUI ui;
    private final List<GameSession> sessions;
    private final ModeRules peaceRules;
    private final ModeRules reversiRules;
    private int currentGameIndex;

    public GameManager(ConsoleUI ui) {
        this.ui = ui;
        this.sessions = new ArrayList<>();
        this.peaceRules = new PeaceRules();
        this.reversiRules = new ReversiRules();
        this.currentGameIndex = 0;

        sessions.add(createSession(GameType.PEACE));
        sessions.add(createSession(GameType.REVERSI));
        currentSession().setStatusMessage("Game 1 selected.");
    }

    public String run() throws IOException {
        while (true) {
            renderCurrentSession();
            String rawInput = ui.readInputLine();
            String exitMessage = handleInput(rawInput);
            if (exitMessage != null) {
                renderCurrentSession();
                return exitMessage;
            }
        }
    }

    private void renderCurrentSession() throws IOException {
        GameSession session = currentSession();
        ui.render(
            session,
            getCurrentGameNumber(),
            sessions,
            rulesFor(session).getLegalMoves(session),
            rulesFor(session).buildHint(session),
            buildPrompt(session)
        );
    }

    private String handleInput(String rawInput) {
        String normalized = normalize(rawInput);
        GameSession session = currentSession();

        if ("quit".equalsIgnoreCase(normalized)) {
            session.setStatusMessage("Quit received. Program ended.");
            return "Quit received. Program ended.";
        }

        GameType gameType = GameType.fromInput(normalized);
        if (gameType != null) {
            sessions.add(createSession(gameType));
            session.setStatusMessage("Added game " + sessions.size() + " as " + gameType.displayName() + ".");
            return null;
        }

        BoardSwitchValidation boardSwitch = validateBoardSwitch(normalized);
        if (boardSwitch.boardSwitchInput()) {
            if (!boardSwitch.valid()) {
                session.setStatusMessage(boardSwitch.errorMessage());
                return null;
            }

            currentGameIndex = boardSwitch.boardIndex();
            currentSession().setStatusMessage("Switched to game " + getCurrentGameNumber() + ".");
            return null;
        }

        if (session.isFinished()) {
            session.setStatusMessage(
                "Game " + getCurrentGameNumber() + " is finished. Switch, add a new game, or quit."
            );
            return null;
        }

        if ("pass".equalsIgnoreCase(normalized)) {
            try {
                session.setStatusMessage(rulesFor(session).applyPass(session));
            } catch (IllegalStateException ex) {
                session.setStatusMessage(ex.getMessage());
            }
            return null;
        }

        MoveValidation move = validateMoveInput(normalized);
        if (!move.valid()) {
            session.setStatusMessage(move.errorMessage());
            return null;
        }

        try {
            session.setStatusMessage(rulesFor(session).applyMove(session, move.position()));
        } catch (IllegalArgumentException ex) {
            session.setStatusMessage(ex.getMessage());
        }
        return null;
    }

    private GameSession createSession(GameType gameType) {
        return new GameSession(gameType, new Board(BOARD_SIZE));
    }

    private int getCurrentGameNumber() {
        return currentGameIndex + 1;
    }

    private GameSession currentSession() {
        return sessions.get(currentGameIndex);
    }

    private ModeRules rulesFor(GameSession session) {
        return session.getGameType() == GameType.PEACE ? peaceRules : reversiRules;
    }

    private String normalize(String rawInput) {
        return rawInput == null ? "" : rawInput.trim();
    }

    private String buildPrompt(GameSession session) {
        String basePrompt = "D4 | 1-" + sessions.size() + " | peace/reversi";
        if (session.getGameType() == GameType.REVERSI) {
            return basePrompt + " | pass | quit > ";
        }
        return basePrompt + " | quit > ";
    }

    private MoveValidation validateMoveInput(String rawInput) {
        if (rawInput.isBlank()) {
            return MoveValidation.error("Invalid input. Enter D4, a game number, peace, reversi, pass, or quit.");
        }

        String normalized = rawInput.toUpperCase(Locale.ROOT);
        if (normalized.length() < 2 || normalized.length() > 3) {
            return MoveValidation.error("Invalid position format. Enter a coordinate like D4.");
        }

        char colChar = normalized.charAt(0);
        if (colChar < 'A' || colChar >= 'A' + BOARD_SIZE) {
            return MoveValidation.error("Invalid column. Use A-" + (char) ('A' + BOARD_SIZE - 1) + ".");
        }

        String rowPart = normalized.substring(1);
        int rowNumber;
        try {
            rowNumber = Integer.parseInt(rowPart);
        } catch (NumberFormatException ex) {
            return MoveValidation.error("Invalid row. Use a coordinate like D4.");
        }

        if (rowNumber < 1 || rowNumber > BOARD_SIZE) {
            return MoveValidation.error("Invalid row. Use 1-" + BOARD_SIZE + ".");
        }

        return MoveValidation.ok(new Position(rowNumber - 1, colChar - 'A'));
    }

    private BoardSwitchValidation validateBoardSwitch(String rawInput) {
        if (rawInput.isBlank()) {
            return BoardSwitchValidation.notBoardSwitch();
        }

        if (!rawInput.chars().allMatch(Character::isDigit)) {
            return BoardSwitchValidation.notBoardSwitch();
        }

        int boardNumber;
        try {
            boardNumber = Integer.parseInt(rawInput);
        } catch (NumberFormatException ex) {
            return BoardSwitchValidation.error("Invalid game number. Enter 1-" + sessions.size() + ".");
        }

        if (boardNumber < 1 || boardNumber > sessions.size()) {
            return BoardSwitchValidation.error("Invalid game number. Enter 1-" + sessions.size() + ".");
        }

        return BoardSwitchValidation.ok(boardNumber - 1);
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
