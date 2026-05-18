package com.example.othello;

import java.util.Set;

public class PeaceRules implements ModeRules {
    @Override
    public String applyMove(GameSession session, Position position) {
        Board board = session.getBoard();
        if (!board.isEmpty(position)) {
            throw new IllegalArgumentException("Invalid move. The cell is not empty.");
        }

        board.setCell(position, session.getCurrentPlayer());
        if (board.isFull()) {
            session.markFinished("Game over: the board is full.");
            return "Move accepted. The peace game has finished.";
        }

        session.setCurrentPlayer(session.getCurrentPlayer().oppositePlayer());
        return "Move accepted.";
    }

    @Override
    public String applyPass(GameSession session) {
        throw new IllegalStateException("Invalid command. Pass is not available in peace mode.");
    }

    @Override
    public Set<Position> getLegalMoves(GameSession session) {
        return Set.of();
    }

    @Override
    public String buildHint(GameSession session) {
        if (session.isFinished()) {
            return "This peace game is finished. You may switch, add a game, or quit.";
        }
        return "Place on any empty cell.";
    }
}
