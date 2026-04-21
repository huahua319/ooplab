package com.example.othello;

import java.util.Set;

public interface ModeRules {
    String applyMove(GameSession session, Position position);

    String applyPass(GameSession session);

    Set<Position> getLegalMoves(GameSession session);

    String buildHint(GameSession session);
}
