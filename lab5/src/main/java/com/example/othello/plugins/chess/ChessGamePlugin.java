package com.example.othello.plugins.chess;

import com.example.othello.CoordinateParser;
import com.example.othello.GameSession;
import com.example.othello.GameType;
import com.example.othello.Position;
import com.example.othello.chess.ChessBoard;
import com.example.othello.chess.ChessRules;
import com.example.othello.plugin.GamePlugin;

import java.util.List;
import java.util.Set;

public class ChessGamePlugin implements GamePlugin {
    private final ChessRules rules = new ChessRules();

    @Override
    public GameType getType() {
        return GameType.CHESS;
    }

    @Override
    public GameSession createSession() {
        return new GameSession(new ChessBoard());
    }

    @Override
    public String handleInput(GameSession session, String input, CoordinateParser coordinateParser) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0 || !("m".equalsIgnoreCase(parts[0]) || "move".equalsIgnoreCase(parts[0]))) {
            throw new IllegalArgumentException("Invalid chess command. Enter m 1a 2a or move 1a 2a.");
        }
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("Invalid chess move format. Enter m 1a 2a.");
        }

        Position from = coordinateParser.parse(parts[1]);
        Position to = coordinateParser.parse(parts[2]);
        Character promotionChoice = null;
        if (parts.length == 4) {
            if (parts[3].length() != 1) {
                throw new IllegalArgumentException("Invalid promotion choice. Use q, r, b, or n.");
            }
            promotionChoice = parts[3].charAt(0);
        }

        return rules.applyMove(session, from, to, promotionChoice);
    }

    @Override
    public Set<Position> getLegalMoves(GameSession session) {
        return Set.of();
    }

    @Override
    public String buildHint(GameSession session) {
        return rules.buildHint(session);
    }

    @Override
    public String buildPrompt(int sessionCount) {
        return "m 7a 5a/move 7a 5a | promotion: q/r/b/n | 1-" + sessionCount
            + " | switch N/s N | peace/reversi/minesweeper/chess | demo | quit > ";
    }

    @Override
    public List<String> demoCommands() {
        return List.of("m 7a 5a", "m 1b 3c", "move 7b 6b");
    }
}
