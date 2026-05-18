package com.example.othello.chess;

public class ChessPiece {
    private final ChessPieceType type;
    private final ChessSide side;

    public ChessPiece(ChessPieceType type, ChessSide side) {
        this.type = type;
        this.side = side;
    }

    public ChessPieceType getType() {
        return type;
    }

    public ChessSide getSide() {
        return side;
    }

    public char getSymbol() {
        char symbol = type.symbol();
        return side == ChessSide.BLACK ? symbol : Character.toLowerCase(symbol);
    }
}
