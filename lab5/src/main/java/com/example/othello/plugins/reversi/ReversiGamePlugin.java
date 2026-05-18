package com.example.othello.plugins.reversi;

import com.example.othello.Board;
import com.example.othello.CoordinateParser;
import com.example.othello.GameSession;
import com.example.othello.GameType;
import com.example.othello.ModeRules;
import com.example.othello.Position;
import com.example.othello.ReversiRules;
import com.example.othello.plugin.GamePlugin;

import java.util.List;
import java.util.Set;

public class ReversiGamePlugin implements GamePlugin {
    private static final int BOARD_SIZE = 8;

    private final ModeRules rules = new ReversiRules();

    @Override
    public GameType getType() {
        return GameType.REVERSI;
    }

    @Override
    public GameSession createSession() {
        return new GameSession(GameType.REVERSI, new Board(BOARD_SIZE));
    }

    @Override
    public String handleInput(GameSession session, String input, CoordinateParser coordinateParser) {
        if ("pass".equalsIgnoreCase(input)) {
            return rules.applyPass(session);
        }
        Position position = coordinateParser.parse(input);
        return rules.applyMove(session, position);
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
        return "1a/a1 | pass | 1-" + sessionCount
            + " | switch N/s N | peace/reversi/minesweeper/chess | demo | quit > ";
    }

    @Override
    public List<String> demoCommands() {
        return List.of("3d", "3c");
    }
}
