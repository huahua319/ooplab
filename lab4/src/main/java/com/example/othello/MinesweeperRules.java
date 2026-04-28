package com.example.othello;

import java.util.Set;

public class MinesweeperRules implements ModeRules {
    @Override
    public String applyMove(GameSession session, Position position) {
        MinesweeperBoard board = session.getMinesweeperBoard();
        MinesweeperBoard.OpenResult result = board.open(position);
        if (result == MinesweeperBoard.OpenResult.MINE) {
            session.markFinished("Game over: you opened a mine. Result: loss.");
            return "Boom. You opened a mine and lost this minesweeper game.";
        }
        if (result == MinesweeperBoard.OpenResult.WIN) {
            session.markFinished("Game over: all safe cells opened. Result: win.");
            return "Safe cell opened. You cleared all safe cells and won this minesweeper game.";
        }

        int neighboringMines = board.getCell(position).getNeighboringMines();
        if (neighboringMines == 0) {
            return "Safe cell opened. No neighboring mines.";
        }
        return "Safe cell opened. Neighboring mines: " + neighboringMines + ".";
    }

    public String applyFlag(GameSession session, Position position) {
        boolean flagged = session.getMinesweeperBoard().toggleFlag(position);
        return flagged ? "Flag placed." : "Flag removed.";
    }

    @Override
    public String applyPass(GameSession session) {
        throw new IllegalStateException("Invalid command. Pass is not available in minesweeper mode.");
    }

    @Override
    public Set<Position> getLegalMoves(GameSession session) {
        return Set.of();
    }

    @Override
    public String buildHint(GameSession session) {
        if (session.isFinished()) {
            return "This minesweeper game is finished. You may switch, add a game, or quit.";
        }
        return "Open one cell with 1a/a1. Toggle a flag with f 1a or flag a1.";
    }
}
