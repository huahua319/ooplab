package com.example.othello.plugin;

import com.example.othello.CoordinateParser;
import com.example.othello.GameSession;
import com.example.othello.GameType;
import com.example.othello.Position;

import java.util.List;
import java.util.Set;

public interface GamePlugin {
    GameType getType();

    GameSession createSession();

    String handleInput(GameSession session, String input, CoordinateParser coordinateParser);

    Set<Position> getLegalMoves(GameSession session);

    String buildHint(GameSession session);

    String buildPrompt(int sessionCount);

    List<String> demoCommands();
}
