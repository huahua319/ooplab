package com.example.othello.plugins.minesweeper;

import com.example.othello.CoordinateParser;
import com.example.othello.GameSession;
import com.example.othello.GameType;
import com.example.othello.MinesweeperBoard;
import com.example.othello.MinesweeperRules;
import com.example.othello.Position;
import com.example.othello.plugin.GamePlugin;

import java.util.List;
import java.util.Set;

public class MinesweeperGamePlugin implements GamePlugin {
    private final MinesweeperRules rules = new MinesweeperRules();

    @Override
    public GameType getType() {
        return GameType.MINESWEEPER;
    }

    @Override
    public GameSession createSession() {
        return new GameSession(new MinesweeperBoard());
    }

    @Override
    public String handleInput(GameSession session, String input, CoordinateParser coordinateParser) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length > 0 && ("f".equalsIgnoreCase(parts[0]) || "flag".equalsIgnoreCase(parts[0]))) {
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid flag format. Enter f 1a or flag a1.");
            }
            return rules.applyFlag(session, coordinateParser.parse(parts[1]));
        }

        if ("pass".equalsIgnoreCase(input)) {
            return rules.applyPass(session);
        }

        return rules.applyMove(session, coordinateParser.parse(input));
    }

    @Override
    public Set<Position> getLegalMoves(GameSession session) {
        return rules.getLegalMoves(session);
    }

    @Override
    public String buildHint(GameSession session) {
        return rules.buildHint(session);
    }

    @Override
    public String buildPrompt(int sessionCount) {
        return "1a/a1 | f 1a/flag a1 | 1-" + sessionCount
            + " | switch N/s N | peace/reversi/minesweeper/chess | demo | quit > ";
    }

    @Override
    public List<String> demoCommands() {
        return List.of("1a", "f 1b", "flag 1b");
    }
}
