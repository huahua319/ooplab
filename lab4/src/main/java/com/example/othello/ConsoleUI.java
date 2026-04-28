package com.example.othello;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConsoleUI implements AutoCloseable {
    private static final int LEFT_WIDTH = 24;
    private static final int CENTER_WIDTH = 38;

    private final Terminal terminal;
    private int inputLine = 0;
    private String inputPrompt = "";

    public ConsoleUI(Terminal terminal) throws IOException {
        this.terminal = terminal;
        terminal.enterPrivateMode();
        terminal.setCursorVisible(false);
    }

    public void render(
        GameSession session,
        int currentGameNumber,
        List<GameSession> sessions,
        Set<Position> legalMoves,
        String hintMessage,
        String promptText
    ) throws IOException {
        terminal.clearScreen();

        List<String> leftColumn = buildBoardColumn(session, legalMoves);
        List<String> centerColumn = buildCenterColumn(session, currentGameNumber, hintMessage);
        List<String> rightColumn = buildGameListColumn(sessions, currentGameNumber);
        List<String> mergedLines = mergeColumns(leftColumn, centerColumn, rightColumn);

        int line = 0;
        for (String mergedLine : mergedLines) {
            putLine(line++, mergedLine);
        }

        putLine(line++, "");
        List<String> statusLines = wrapText("Status: " + safeText(session.getStatusMessage()), LEFT_WIDTH + CENTER_WIDTH + 18);
        for (String statusLine : statusLines) {
            putLine(line++, statusLine);
        }
        putLine(line++, "");
        putLine(line++, "Input Options:");
        inputPrompt = promptText;
        inputLine = line++;
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

    private List<String> buildBoardColumn(GameSession session, Set<Position> legalMoves) {
        List<String> lines = new ArrayList<>();
        if (session.getGameType() == GameType.MINESWEEPER) {
            return buildMinesweeperBoardColumn(session);
        }

        Board board = session.getBoard();
        lines.add(titleCase(session.getGameType().displayName()) + " Board");
        lines.add(buildHeader(board.getSize()));

        for (int row = 0; row < board.getSize(); row++) {
            StringBuilder line = new StringBuilder();
            line.append(row + 1).append(" ");
            for (int col = 0; col < board.getSize(); col++) {
                Position position = new Position(row, col);
                char symbol = board.getCell(position).getSymbol();
                if (symbol == Cell.EMPTY.getSymbol() && legalMoves.contains(position)) {
                    symbol = '+';
                }
                line.append(symbol).append(" ");
            }
            lines.add(line.toString());
        }

        return lines;
    }

    private List<String> buildCenterColumn(GameSession session, int currentGameNumber, String hintMessage) {
        List<String> lines = new ArrayList<>();
        if (session.getGameType() == GameType.MINESWEEPER) {
            return buildMinesweeperCenterColumn(session, currentGameNumber, hintMessage);
        }

        Board board = session.getBoard();
        int blackScore = board.countCells(Cell.BLACK);
        int whiteScore = board.countCells(Cell.WHITE);

        lines.add("Game " + currentGameNumber);
        lines.add("Type: " + session.getGameType().displayName());
        lines.add("Player1 [Tom]" + turnMarker(session, Cell.BLACK));
        lines.add("Player2 [Jerry]" + turnMarker(session, Cell.WHITE));

        if (session.getGameType() == GameType.REVERSI) {
            lines.add("Score: " + blackScore + " - " + whiteScore);
        } else {
            lines.add("");
        }

        if (session.isFinished()) {
            lines.add("Result:");
            appendWrapped(lines, session.getFinishMessage(), CENTER_WIDTH);
        } else {
            lines.add("Current Turn: " + currentPlayerName(session.getCurrentPlayer()));
            lines.add("Hint:");
            appendWrapped(lines, safeText(hintMessage), CENTER_WIDTH);
        }

        return lines;
    }

    private List<String> buildMinesweeperBoardColumn(GameSession session) {
        List<String> lines = new ArrayList<>();
        MinesweeperBoard board = session.getMinesweeperBoard();
        lines.add("Minesweeper Board");
        lines.add(buildHeader(board.getSize()));

        for (int row = 0; row < board.getSize(); row++) {
            StringBuilder line = new StringBuilder();
            line.append(row + 1).append(" ");
            for (int col = 0; col < board.getSize(); col++) {
                line.append(board.displaySymbol(new Position(row, col), session.isFinished())).append(" ");
            }
            lines.add(line.toString());
        }

        lines.add("");
        lines.add("#: hidden  F: flag");
        lines.add("*: mine    .: zero");
        lines.add("1-8: neighboring mines");
        return lines;
    }

    private List<String> buildMinesweeperCenterColumn(
        GameSession session,
        int currentGameNumber,
        String hintMessage
    ) {
        List<String> lines = new ArrayList<>();
        MinesweeperBoard board = session.getMinesweeperBoard();

        lines.add("Game " + currentGameNumber);
        lines.add("Mode: minesweeper");
        lines.add("Single-player mode");
        lines.add("Mines: " + board.getMineCount());
        lines.add("Flags: " + board.getFlagCount());
        lines.add("Safe cells left: " + board.getSafeCellsLeft());
        lines.add("Opened safe cells: " + board.getOpenedSafeCells());
        lines.add("First open is always safe");
        lines.add("Basic mode: no auto-expansion");
        lines.add("for zero cells");
        lines.add("Last message:");
        appendWrapped(lines, safeText(session.getStatusMessage()), CENTER_WIDTH);

        if (session.isFinished()) {
            lines.add("Result:");
            appendWrapped(lines, session.getFinishMessage(), CENTER_WIDTH);
        } else {
            lines.add("Hint:");
            appendWrapped(lines, safeText(hintMessage), CENTER_WIDTH);
        }

        return lines;
    }

    private List<String> buildGameListColumn(List<GameSession> sessions, int currentGameNumber) {
        List<String> lines = new ArrayList<>();
        lines.add("Game List");
        for (int index = 0; index < sessions.size(); index++) {
            String prefix = (index + 1 == currentGameNumber) ? ">" : " ";
            GameSession session = sessions.get(index);
            String status = session.isFinished() ? "finished" : "running";
            lines.add(prefix + " " + (index + 1) + ". " + session.getGameType().displayName() + " [" + status + "]");
        }
        return lines;
    }

    private List<String> mergeColumns(List<String> leftColumn, List<String> centerColumn, List<String> rightColumn) {
        int rowCount = Math.max(leftColumn.size(), Math.max(centerColumn.size(), rightColumn.size()));
        List<String> mergedLines = new ArrayList<>();

        for (int row = 0; row < rowCount; row++) {
            String left = row < leftColumn.size() ? leftColumn.get(row) : "";
            String center = row < centerColumn.size() ? centerColumn.get(row) : "";
            String right = row < rightColumn.size() ? rightColumn.get(row) : "";
            mergedLines.add(pad(left, LEFT_WIDTH) + pad(center, CENTER_WIDTH) + right);
        }

        return mergedLines;
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

    private String pad(String text, int width) {
        String safeText = safeText(text);
        if (safeText.length() >= width) {
            return safeText.substring(0, width);
        }
        return safeText + " ".repeat(width - safeText.length());
    }

    private void appendWrapped(List<String> lines, String text, int width) {
        lines.addAll(wrapText(text, width));
    }

    private List<String> wrapText(String text, int width) {
        List<String> wrappedLines = new ArrayList<>();
        String remaining = safeText(text).trim();
        if (remaining.isEmpty()) {
            wrappedLines.add("");
            return wrappedLines;
        }

        while (remaining.length() > width) {
            int splitAt = remaining.lastIndexOf(' ', width);
            if (splitAt <= 0) {
                splitAt = width;
            }
            wrappedLines.add(remaining.substring(0, splitAt).trim());
            remaining = remaining.substring(splitAt).trim();
        }
        wrappedLines.add(remaining);
        return wrappedLines;
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }

    private String titleCase(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private String turnMarker(GameSession session, Cell player) {
        if (session.isFinished() || session.getCurrentPlayer() != player) {
            return "";
        }
        return player == Cell.BLACK ? "  B" : "  W";
    }

    private String currentPlayerName(Cell player) {
        return player == Cell.BLACK ? "Player1 [Tom]" : "Player2 [Jerry]";
    }
}
