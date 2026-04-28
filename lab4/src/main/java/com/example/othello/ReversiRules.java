package com.example.othello;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ReversiRules implements ModeRules {
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    @Override
    public String applyMove(GameSession session, Position position) {
        Board board = session.getBoard();
        if (!board.isEmpty(position)) {
            throw new IllegalArgumentException("Invalid move. The cell is not empty.");
        }

        List<Position> flippablePositions = findFlippablePositions(board, position, session.getCurrentPlayer());
        if (flippablePositions.isEmpty()) {
            throw new IllegalArgumentException("Invalid move. Choose a highlighted '+' position.");
        }

        board.setCell(position, session.getCurrentPlayer());
        for (Position flippablePosition : flippablePositions) {
            board.setCell(flippablePosition, session.getCurrentPlayer());
        }

        session.setCurrentPlayer(session.getCurrentPlayer().oppositePlayer());
        if (updateFinishedState(session)) {
            return "Move accepted. The reversi game has finished.";
        }

        return "Move accepted.";
    }

    @Override
    public String applyPass(GameSession session) {
        if (!getLegalMoves(session).isEmpty()) {
            throw new IllegalStateException("Invalid pass. Legal moves still exist.");
        }

        session.setCurrentPlayer(session.getCurrentPlayer().oppositePlayer());
        if (updateFinishedState(session)) {
            return "Pass accepted. The reversi game has finished.";
        }

        return "Pass accepted.";
    }

    @Override
    public Set<Position> getLegalMoves(GameSession session) {
        if (session.isFinished()) {
            return Set.of();
        }

        Board board = session.getBoard();
        Set<Position> legalMoves = new LinkedHashSet<>();
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position position = new Position(row, col);
                if (!findFlippablePositions(board, position, session.getCurrentPlayer()).isEmpty()) {
                    legalMoves.add(position);
                }
            }
        }
        return legalMoves;
    }

    @Override
    public String buildHint(GameSession session) {
        if (session.isFinished()) {
            return "This reversi game is finished. You may switch, add a game, or quit.";
        }

        Set<Position> legalMoves = getLegalMoves(session);
        if (legalMoves.isEmpty()) {
            return playerName(session.getCurrentPlayer()) + " has no legal move. Enter pass to continue.";
        }

        return "Play on a highlighted '+' position.";
    }

    private boolean updateFinishedState(GameSession session) {
        Board board = session.getBoard();
        if (board.isFull()) {
            session.markFinished(buildFinishMessage(board, "Game over: the board is full."));
            return true;
        }

        if (!hasLegalMove(board, Cell.BLACK) && !hasLegalMove(board, Cell.WHITE)) {
            session.markFinished(buildFinishMessage(board, "Game over: neither player has a legal move."));
            return true;
        }

        return false;
    }

    private boolean hasLegalMove(Board board, Cell player) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (!findFlippablePositions(board, new Position(row, col), player).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Position> findFlippablePositions(Board board, Position position, Cell player) {
        List<Position> flippablePositions = new ArrayList<>();
        if (player == Cell.EMPTY || !board.isInside(position) || !board.isEmpty(position)) {
            return flippablePositions;
        }

        for (int[] direction : DIRECTIONS) {
            flippablePositions.addAll(findFlippableInDirection(board, position, player, direction[0], direction[1]));
        }
        return flippablePositions;
    }

    private List<Position> findFlippableInDirection(
        Board board,
        Position position,
        Cell player,
        int rowDelta,
        int colDelta
    ) {
        List<Position> capturedPositions = new ArrayList<>();
        int row = position.row() + rowDelta;
        int col = position.col() + colDelta;

        while (board.isInside(row, col) && board.getCell(row, col) == player.oppositePlayer()) {
            capturedPositions.add(new Position(row, col));
            row += rowDelta;
            col += colDelta;
        }

        if (capturedPositions.isEmpty() || !board.isInside(row, col) || board.getCell(row, col) != player) {
            return List.of();
        }
        return capturedPositions;
    }

    private String buildFinishMessage(Board board, String reason) {
        int blackScore = board.countCells(Cell.BLACK);
        int whiteScore = board.countCells(Cell.WHITE);

        if (blackScore > whiteScore) {
            return reason + " Winner: " + playerName(Cell.BLACK) + ".";
        }
        if (whiteScore > blackScore) {
            return reason + " Winner: " + playerName(Cell.WHITE) + ".";
        }
        return reason + " Result: draw.";
    }

    private String playerName(Cell player) {
        return player == Cell.BLACK ? "Player1 [Tom]" : "Player2 [Jerry]";
    }
}
