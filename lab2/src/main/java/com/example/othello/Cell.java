package com.example.othello;

public enum Cell {
    EMPTY('.'),
    BLACK('B'),
    WHITE('W');

    private final char symbol;

    Cell(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public Cell oppositePlayer() {
        if (this == BLACK) {
            return WHITE;
        }
        if (this == WHITE) {
            return BLACK;
        }
        return EMPTY;
    }
}
