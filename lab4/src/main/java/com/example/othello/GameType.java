package com.example.othello;

public enum GameType {
    PEACE("peace"),
    REVERSI("reversi"),
    MINESWEEPER("minesweeper");

    private final String displayName;

    GameType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static GameType fromInput(String input) {
        for (GameType gameType : values()) {
            if (gameType.displayName.equalsIgnoreCase(input)) {
                return gameType;
            }
        }
        return null;
    }
}
