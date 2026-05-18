package com.example.othello.chess;

import com.example.othello.Position;

public class ChessBoard {
    public static final int SIZE = 8;

    private final ChessPiece[][] pieces;
    private boolean blackKingMoved;
    private boolean whiteKingMoved;
    private boolean blackKingRookMoved;
    private boolean blackQueenRookMoved;
    private boolean whiteKingRookMoved;
    private boolean whiteQueenRookMoved;
    private Position enPassantTarget;
    private Position enPassantCapturedPawn;

    public ChessBoard() {
        this.pieces = new ChessPiece[SIZE][SIZE];
        initializeStartingPosition();
    }

    public int getSize() {
        return SIZE;
    }

    public ChessPiece getPiece(Position position) {
        return pieces[position.row()][position.col()];
    }

    public ChessPiece getPiece(int row, int col) {
        return pieces[row][col];
    }

    public void setPiece(Position position, ChessPiece piece) {
        pieces[position.row()][position.col()] = piece;
    }

    public boolean isInside(Position position) {
        return isInside(position.row(), position.col());
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public char displaySymbol(Position position) {
        ChessPiece piece = getPiece(position);
        return piece == null ? '.' : piece.getSymbol();
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public Position getEnPassantCapturedPawn() {
        return enPassantCapturedPawn;
    }

    public void clearEnPassant() {
        enPassantTarget = null;
        enPassantCapturedPawn = null;
    }

    public void setEnPassant(Position target, Position capturedPawn) {
        enPassantTarget = target;
        enPassantCapturedPawn = capturedPawn;
    }

    public boolean hasKingMoved(ChessSide side) {
        return side == ChessSide.BLACK ? blackKingMoved : whiteKingMoved;
    }

    public boolean hasRookMoved(ChessSide side, boolean kingSide) {
        if (side == ChessSide.BLACK) {
            return kingSide ? blackKingRookMoved : blackQueenRookMoved;
        }
        return kingSide ? whiteKingRookMoved : whiteQueenRookMoved;
    }

    public void markKingMoved(ChessSide side) {
        if (side == ChessSide.BLACK) {
            blackKingMoved = true;
        } else {
            whiteKingMoved = true;
        }
    }

    public void markRookMovedFrom(Position from) {
        if (from.row() == 0 && from.col() == 0) {
            blackQueenRookMoved = true;
        } else if (from.row() == 0 && from.col() == SIZE - 1) {
            blackKingRookMoved = true;
        } else if (from.row() == SIZE - 1 && from.col() == 0) {
            whiteQueenRookMoved = true;
        } else if (from.row() == SIZE - 1 && from.col() == SIZE - 1) {
            whiteKingRookMoved = true;
        }
    }

    private void initializeStartingPosition() {
        initializeBackRank(0, ChessSide.BLACK);
        initializePawns(1, ChessSide.BLACK);
        initializePawns(6, ChessSide.WHITE);
        initializeBackRank(7, ChessSide.WHITE);
    }

    private void initializeBackRank(int row, ChessSide side) {
        pieces[row][0] = new ChessPiece(ChessPieceType.ROOK, side);
        pieces[row][1] = new ChessPiece(ChessPieceType.KNIGHT, side);
        pieces[row][2] = new ChessPiece(ChessPieceType.BISHOP, side);
        pieces[row][3] = new ChessPiece(ChessPieceType.QUEEN, side);
        pieces[row][4] = new ChessPiece(ChessPieceType.KING, side);
        pieces[row][5] = new ChessPiece(ChessPieceType.BISHOP, side);
        pieces[row][6] = new ChessPiece(ChessPieceType.KNIGHT, side);
        pieces[row][7] = new ChessPiece(ChessPieceType.ROOK, side);
    }

    private void initializePawns(int row, ChessSide side) {
        for (int col = 0; col < SIZE; col++) {
            pieces[row][col] = new ChessPiece(ChessPieceType.PAWN, side);
        }
    }
}
