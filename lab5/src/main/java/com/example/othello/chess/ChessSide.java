package com.example.othello.chess;

public enum ChessSide {
    BLACK("Black"),
    WHITE("White");

    private final String displayName;

    ChessSide(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public ChessSide opposite() {
        return this == BLACK ? WHITE : BLACK;
    }
}
