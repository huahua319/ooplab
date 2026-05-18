package com.example.othello;

import com.example.othello.plugin.GamePlugin;
import com.example.othello.plugin.GameRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameManager {
    private static final int BOARD_SIZE = 8;
    private static final long DEMO_STEP_MILLIS = 1500L;

    private final ConsoleUI ui;
    private final GameRegistry gameRegistry;
    private final List<GameSession> sessions;
    private int currentGameIndex;

    public GameManager(ConsoleUI ui) {
        this.ui = ui;
        this.gameRegistry = GameRegistry.createDefault();
        this.sessions = new ArrayList<>();
        this.currentGameIndex = 0;

        sessions.add(createSession(GameType.PEACE));
        sessions.add(createSession(GameType.REVERSI));
        sessions.add(createSession(GameType.MINESWEEPER));
        sessions.add(createSession(GameType.CHESS));
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

    public String runDemoOnly() throws IOException {
        runDemoMode();
        return "Demo finished.";
    }

    private void renderCurrentSession() throws IOException {
        GameSession session = currentSession();
        GamePlugin plugin = pluginFor(session);
        ui.render(
            session,
            getCurrentGameNumber(),
            sessions,
            plugin.getLegalMoves(session),
            plugin.buildHint(session),
            plugin.buildPrompt(sessions.size())
        );
    }

    private void renderDemoSession(
        GameSession session,
        int currentGameNumber,
        List<GameSession> demoSessions,
        String demoInfo
    ) throws IOException {
        GamePlugin plugin = pluginFor(session);
        ui.render(
            session,
            currentGameNumber,
            demoSessions,
            plugin.getLegalMoves(session),
            plugin.buildHint(session),
            plugin.buildPrompt(demoSessions.size()),
            demoInfo
        );
    }

    private String handleInput(String rawInput) throws IOException {
        String normalized = normalize(rawInput);
        GameSession session = currentSession();

        if ("quit".equalsIgnoreCase(normalized)) {
            session.setStatusMessage("Quit received. Program ended.");
            return "Quit received. Program ended.";
        }

        if ("demo".equalsIgnoreCase(normalized)) {
            runDemoMode();
            currentSession().setStatusMessage("Demo finished. Returned to normal mode.");
            return null;
        }

        GamePlugin newGamePlugin = gameRegistry.findByInput(normalized);
        if (newGamePlugin != null) {
            sessions.add(newGamePlugin.createSession());
            session.setStatusMessage("Added game " + sessions.size() + " as "
                + newGamePlugin.getType().displayName() + ".");
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

        try {
            session.setStatusMessage(pluginFor(session).handleInput(session, normalized, this::parsePosition));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            session.setStatusMessage(ex.getMessage());
        }
        return null;
    }

    private void runDemoMode() throws IOException {
        List<GameSession> demoSessions = new ArrayList<>();
        for (GamePlugin plugin : gameRegistry.orderedPlugins()) {
            demoSessions.add(plugin.createSession());
        }

        for (int index = 0; index < demoSessions.size(); index++) {
            GameSession demoSession = demoSessions.get(index);
            GamePlugin plugin = pluginFor(demoSession);
            String demoHeader = "DEMO MODE: Playing " + plugin.getType().displayName() + ".";
            demoSession.setStatusMessage(demoHeader);
            renderDemoSession(demoSession, index + 1, demoSessions, buildDemoInfo(plugin, "ready"));
            pauseDemo();

            for (String command : plugin.demoCommands()) {
                if (demoSession.isFinished()) {
                    break;
                }
                demoSession.setStatusMessage("DEMO MODE: Executing " + command + ".");
                renderDemoSession(demoSession, index + 1, demoSessions, buildDemoInfo(plugin, command));
                pauseDemo();

                try {
                    demoSession.setStatusMessage(plugin.handleInput(demoSession, command, this::parsePosition));
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    demoSession.setStatusMessage(ex.getMessage());
                }

                renderDemoSession(demoSession, index + 1, demoSessions, buildDemoInfo(plugin, command));
                pauseDemo();
            }
        }
    }

    private String buildDemoInfo(GamePlugin plugin, String command) {
        if ("ready".equals(command)) {
            return "Demo running for: " + plugin.getType().displayName()
                + " | Auto executes game commands | Shows gameplay in 1.5s steps";
        }
        return "Demo running for: " + plugin.getType().displayName()
            + " | Executing: " + command + " | Auto executes game commands";
    }

    private void pauseDemo() {
        try {
            Thread.sleep(DEMO_STEP_MILLIS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private GameSession createSession(GameType gameType) {
        return gameRegistry.get(gameType).createSession();
    }

    private int getCurrentGameNumber() {
        return currentGameIndex + 1;
    }

    private GameSession currentSession() {
        return sessions.get(currentGameIndex);
    }

    private GamePlugin pluginFor(GameSession session) {
        return gameRegistry.get(session.getGameType());
    }

    private String normalize(String rawInput) {
        return rawInput == null ? "" : rawInput.trim();
    }

    private Position parsePosition(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new IllegalArgumentException(
                "Invalid input. Enter a coordinate, game number, game type, demo, or quit."
            );
        }

        String normalized = rawInput.toUpperCase(Locale.ROOT);
        if (normalized.length() != 2) {
            throw new IllegalArgumentException("Invalid position format. Enter a coordinate like 1a or a1.");
        }

        char first = normalized.charAt(0);
        char second = normalized.charAt(1);
        char rowChar;
        char colChar;

        if (Character.isDigit(first) && Character.isLetter(second)) {
            rowChar = first;
            colChar = second;
        } else if (Character.isLetter(first) && Character.isDigit(second)) {
            rowChar = second;
            colChar = first;
        } else {
            throw new IllegalArgumentException("Invalid position format. Enter a coordinate like 1a or a1.");
        }

        if (colChar < 'A' || colChar >= 'A' + BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid column. Use A-" + (char) ('A' + BOARD_SIZE - 1) + ".");
        }

        int rowNumber = rowChar - '0';
        if (rowNumber < 1 || rowNumber > BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid row. Use 1-" + BOARD_SIZE + ".");
        }

        return new Position(rowNumber - 1, colChar - 'A');
    }

    private BoardSwitchValidation validateBoardSwitch(String rawInput) {
        if (rawInput.isBlank()) {
            return BoardSwitchValidation.notBoardSwitch();
        }

        String boardNumberInput = rawInput;
        String[] parts = rawInput.trim().split("\\s+");
        if (parts.length == 2 && ("switch".equalsIgnoreCase(parts[0]) || "s".equalsIgnoreCase(parts[0]))) {
            boardNumberInput = parts[1];
        } else if (parts.length == 1 && ("switch".equalsIgnoreCase(parts[0]) || "s".equalsIgnoreCase(parts[0]))) {
            return BoardSwitchValidation.error("Invalid switch format. Enter switch N or s N.");
        } else if (parts.length > 1 && ("switch".equalsIgnoreCase(parts[0]) || "s".equalsIgnoreCase(parts[0]))) {
            return BoardSwitchValidation.error("Invalid switch format. Enter switch N or s N.");
        } else if (parts.length != 1) {
            return BoardSwitchValidation.notBoardSwitch();
        }

        if (!boardNumberInput.chars().allMatch(Character::isDigit)) {
            if (parts.length == 2 && ("switch".equalsIgnoreCase(parts[0]) || "s".equalsIgnoreCase(parts[0]))) {
                return BoardSwitchValidation.error("Invalid game number. Enter 1-" + sessions.size() + ".");
            }
            return BoardSwitchValidation.notBoardSwitch();
        }

        int boardNumber;
        try {
            boardNumber = Integer.parseInt(boardNumberInput);
        } catch (NumberFormatException ex) {
            return BoardSwitchValidation.error("Invalid game number. Enter 1-" + sessions.size() + ".");
        }

        if (boardNumber < 1 || boardNumber > sessions.size()) {
            return BoardSwitchValidation.error("Invalid game number. Enter 1-" + sessions.size() + ".");
        }

        return BoardSwitchValidation.ok(boardNumber - 1);
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
