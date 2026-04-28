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
    private final MinesweeperRules minesweeperRules;
    private int currentGameIndex;

    public GameManager(ConsoleUI ui) {
        this.ui = ui;
        this.sessions = new ArrayList<>();
        this.peaceRules = new PeaceRules();
        this.reversiRules = new ReversiRules();
        this.minesweeperRules = new MinesweeperRules();
        this.currentGameIndex = 0;

        sessions.add(createSession(GameType.PEACE));
        sessions.add(createSession(GameType.REVERSI));
        sessions.add(createSession(GameType.MINESWEEPER));
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

        FlagValidation flag = validateFlagInput(normalized);
        if (flag.flagInput()) {
            if (!flag.valid()) {
                session.setStatusMessage(flag.errorMessage());
                return null;
            }
            if (session.getGameType() != GameType.MINESWEEPER) {
                session.setStatusMessage("Invalid command. Flags are only available in minesweeper mode.");
                return null;
            }

            try {
                session.setStatusMessage(minesweeperRules.applyFlag(session, flag.position()));
            } catch (IllegalArgumentException ex) {
                session.setStatusMessage(ex.getMessage());
            }
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
        if (gameType == GameType.MINESWEEPER) {
            return new GameSession(new MinesweeperBoard());
        }
        return new GameSession(gameType, new Board(BOARD_SIZE));
    }

    private int getCurrentGameNumber() {
        return currentGameIndex + 1;
    }

    private GameSession currentSession() {
        return sessions.get(currentGameIndex);
    }

    private ModeRules rulesFor(GameSession session) {
        return switch (session.getGameType()) {
            case PEACE -> peaceRules;
            case REVERSI -> reversiRules;
            case MINESWEEPER -> minesweeperRules;
        };
    }

    private String normalize(String rawInput) {
        return rawInput == null ? "" : rawInput.trim();
    }

    private String buildPrompt(GameSession session) {
        String basePrompt = "1a/a1 | 1-" + sessions.size() + " | switch N/s N | peace/reversi/minesweeper";
        if (session.getGameType() == GameType.REVERSI) {
            return basePrompt + " | pass | quit > ";
        }
        if (session.getGameType() == GameType.MINESWEEPER) {
            return basePrompt + " | f 1a/flag a1 | quit > ";
        }
        return basePrompt + " | quit > ";
    }

    private MoveValidation validateMoveInput(String rawInput) {
        if (rawInput.isBlank()) {
            return MoveValidation.error("Invalid input. Enter a coordinate, game number, game type, pass, or quit.");
        }

        String normalized = rawInput.toUpperCase(Locale.ROOT);
        if (normalized.length() != 2) {
            return MoveValidation.error("Invalid position format. Enter a coordinate like 1a or a1.");
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
            return MoveValidation.error("Invalid position format. Enter a coordinate like 1a or a1.");
        }

        if (colChar < 'A' || colChar >= 'A' + BOARD_SIZE) {
            return MoveValidation.error("Invalid column. Use A-" + (char) ('A' + BOARD_SIZE - 1) + ".");
        }

        int rowNumber = rowChar - '0';
        if (rowNumber < 1 || rowNumber > BOARD_SIZE) {
            return MoveValidation.error("Invalid row. Use 1-" + BOARD_SIZE + ".");
        }

        return MoveValidation.ok(new Position(rowNumber - 1, colChar - 'A'));
    }

    private FlagValidation validateFlagInput(String rawInput) {
        if (rawInput.isBlank()) {
            return FlagValidation.notFlagInput();
        }

        String[] parts = rawInput.trim().split("\\s+");
        if (parts.length == 0 || !("f".equalsIgnoreCase(parts[0]) || "flag".equalsIgnoreCase(parts[0]))) {
            return FlagValidation.notFlagInput();
        }

        if (parts.length != 2) {
            return FlagValidation.error("Invalid flag format. Enter f 1a or flag a1.");
        }

        MoveValidation move = validateMoveInput(parts[1]);
        if (!move.valid()) {
            return FlagValidation.error(move.errorMessage());
        }
        return FlagValidation.ok(move.position());
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

    private record MoveValidation(boolean valid, Position position, String errorMessage) {
        static MoveValidation ok(Position position) {
            return new MoveValidation(true, position, "");
        }

        static MoveValidation error(String errorMessage) {
            return new MoveValidation(false, null, errorMessage);
        }
    }

    private record FlagValidation(boolean flagInput, boolean valid, Position position, String errorMessage) {
        static FlagValidation ok(Position position) {
            return new FlagValidation(true, true, position, "");
        }

        static FlagValidation error(String errorMessage) {
            return new FlagValidation(true, false, null, errorMessage);
        }

        static FlagValidation notFlagInput() {
            return new FlagValidation(false, false, null, "");
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
