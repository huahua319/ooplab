package com.example.othello.chess;

public enum ChessPieceType {
    KING('K'),
    QUEEN('Q'),
    ROOK('R'),
    BISHOP('B'),
    KNIGHT('N'),
    PAWN('P');

    private final char symbol;

    ChessPieceType(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }
}
