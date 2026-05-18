package com.example.othello.chess;

import com.example.othello.Cell;
import com.example.othello.GameSession;
import com.example.othello.Position;

public class ChessRules {
    public String applyMove(GameSession session, Position from, Position to, Character promotionChoice) {
        ChessBoard board = session.getChessBoard();
        ChessSide currentSide = sideFromCell(session.getCurrentPlayer());

        validateInside(board, from, "source");
        validateInside(board, to, "target");

        ChessPiece movingPiece = board.getPiece(from);
        if (movingPiece == null) {
            throw new IllegalArgumentException("Invalid chess move. The source cell is empty.");
        }
        if (movingPiece.getSide() != currentSide) {
            throw new IllegalArgumentException("Invalid chess move. Move the current player's piece.");
        }

        ChessPiece targetPiece = board.getPiece(to);
        if (targetPiece != null && targetPiece.getSide() == currentSide) {
            throw new IllegalArgumentException("Invalid chess move. The target cell has your own piece.");
        }

        MovePlan plan = validatePieceMove(board, movingPiece, from, to, promotionChoice);
        ChessPiece capturedPiece = executeMove(board, movingPiece, from, to, plan);

        if (capturedPiece != null && capturedPiece.getType() == ChessPieceType.KING) {
            String winner = currentSide.displayName();
            session.markFinished("Game over: " + capturedPiece.getSide().displayName()
                + " king was captured. Winner: " + winner + ".");
            return "Move accepted. " + winner + " wins by capturing the king.";
        }

        session.setCurrentPlayer(cellFromSide(currentSide.opposite()));
        String message = plan.castling() ? "Castling accepted." : "Move accepted.";
        if (plan.enPassant()) {
            message = "En passant accepted.";
        }
        if (plan.promotionType() != null) {
            message += " Pawn promoted to " + plan.promotionType().name().toLowerCase() + ".";
        }
        return message;
    }

    public String buildHint(GameSession session) {
        if (session.isFinished()) {
            return "This chess game is finished. You may switch, add a game, or quit.";
        }
        return "Move with m 7a 5a or move 7a 5a. Capture a king to win.";
    }

    public static ChessSide sideFromCell(Cell cell) {
        return cell == Cell.BLACK ? ChessSide.BLACK : ChessSide.WHITE;
    }

    public static Cell cellFromSide(ChessSide side) {
        return side == ChessSide.BLACK ? Cell.BLACK : Cell.WHITE;
    }

    private MovePlan validatePieceMove(
        ChessBoard board,
        ChessPiece piece,
        Position from,
        Position to,
        Character promotionChoice
    ) {
        int rowDelta = to.row() - from.row();
        int colDelta = to.col() - from.col();
        return switch (piece.getType()) {
            case KING -> validateKingMove(board, piece, from, to, rowDelta, colDelta, promotionChoice);
            case QUEEN -> validateSlidingMove(
                board,
                from,
                to,
                rowDelta == 0 || colDelta == 0 || Math.abs(rowDelta) == Math.abs(colDelta),
                promotionChoice,
                "queen"
            );
            case ROOK -> validateSlidingMove(
                board,
                from,
                to,
                rowDelta == 0 || colDelta == 0,
                promotionChoice,
                "rook"
            );
            case BISHOP -> validateSlidingMove(
                board,
                from,
                to,
                Math.abs(rowDelta) == Math.abs(colDelta),
                promotionChoice,
                "bishop"
            );
            case KNIGHT -> validateKnightMove(rowDelta, colDelta, promotionChoice);
            case PAWN -> validatePawnMove(board, piece, from, to, rowDelta, colDelta, promotionChoice);
        };
    }

    private MovePlan validateKingMove(
        ChessBoard board,
        ChessPiece piece,
        Position from,
        Position to,
        int rowDelta,
        int colDelta,
        Character promotionChoice
    ) {
        rejectPromotionChoice(promotionChoice, "king");
        if (Math.abs(rowDelta) <= 1 && Math.abs(colDelta) <= 1) {
            return MovePlan.normal();
        }

        if (rowDelta == 0 && Math.abs(colDelta) == 2) {
            return validateCastling(board, piece.getSide(), from, to);
        }

        throw new IllegalArgumentException("Invalid chess move. A king moves one square or castles.");
    }

    private MovePlan validateSlidingMove(
        ChessBoard board,
        Position from,
        Position to,
        boolean directionAllowed,
        Character promotionChoice,
        String pieceName
    ) {
        rejectPromotionChoice(promotionChoice, pieceName);
        if (!directionAllowed || from.equals(to)) {
            throw new IllegalArgumentException("Invalid chess move. The " + pieceName + " cannot move that way.");
        }
        if (!isPathClear(board, from, to)) {
            throw new IllegalArgumentException("Invalid chess move. The path is blocked.");
        }
        return MovePlan.normal();
    }

    private MovePlan validateKnightMove(int rowDelta, int colDelta, Character promotionChoice) {
        rejectPromotionChoice(promotionChoice, "knight");
        boolean valid = (Math.abs(rowDelta) == 2 && Math.abs(colDelta) == 1)
            || (Math.abs(rowDelta) == 1 && Math.abs(colDelta) == 2);
        if (!valid) {
            throw new IllegalArgumentException("Invalid chess move. A knight moves in an L shape.");
        }
        return MovePlan.normal();
    }

    private MovePlan validatePawnMove(
        ChessBoard board,
        ChessPiece piece,
        Position from,
        Position to,
        int rowDelta,
        int colDelta,
        Character promotionChoice
    ) {
        ChessSide side = piece.getSide();
        int direction = side == ChessSide.WHITE ? -1 : 1;
        int startRow = side == ChessSide.WHITE ? 6 : 1;
        int promotionRow = side == ChessSide.WHITE ? 0 : ChessBoard.SIZE - 1;
        ChessPiece targetPiece = board.getPiece(to);
        boolean reachesPromotion = to.row() == promotionRow;
        ChessPieceType promotionType = reachesPromotion ? resolvePromotionType(promotionChoice) : null;

        if (!reachesPromotion && promotionChoice != null) {
            throw new IllegalArgumentException("Invalid chess move. Promotion choice is only valid on final rank.");
        }

        if (colDelta == 0 && rowDelta == direction && targetPiece == null) {
            return MovePlan.normalPromotion(promotionType);
        }

        if (colDelta == 0 && rowDelta == 2 * direction && from.row() == startRow && targetPiece == null) {
            Position skipped = new Position(from.row() + direction, from.col());
            if (board.getPiece(skipped) != null) {
                throw new IllegalArgumentException("Invalid chess move. The pawn path is blocked.");
            }
            return MovePlan.doublePawn(skipped, to);
        }

        if (Math.abs(colDelta) == 1 && rowDelta == direction) {
            if (targetPiece != null && targetPiece.getSide() != side) {
                return MovePlan.normalPromotion(promotionType);
            }

            Position enPassantTarget = board.getEnPassantTarget();
            Position capturedPawn = board.getEnPassantCapturedPawn();
            if (enPassantTarget != null && enPassantTarget.equals(to) && capturedPawn != null) {
                ChessPiece capturedPiece = board.getPiece(capturedPawn);
                if (capturedPiece != null
                    && capturedPiece.getType() == ChessPieceType.PAWN
                    && capturedPiece.getSide() == side.opposite()) {
                    return MovePlan.enPassant(capturedPawn);
                }
            }
        }

        throw new IllegalArgumentException("Invalid chess move. The pawn cannot move that way.");
    }

    private MovePlan validateCastling(ChessBoard board, ChessSide side, Position from, Position to) {
        int homeRow = side == ChessSide.BLACK ? 0 : ChessBoard.SIZE - 1;
        if (from.row() != homeRow || from.col() != 4) {
            throw new IllegalArgumentException("Invalid castling. The king is not on its starting square.");
        }
        if (board.hasKingMoved(side)) {
            throw new IllegalArgumentException("Invalid castling. The king has already moved.");
        }

        boolean kingSide = to.col() == 6;
        boolean queenSide = to.col() == 2;
        if (!kingSide && !queenSide) {
            throw new IllegalArgumentException("Invalid castling. Move the king two squares left or right.");
        }
        if (board.hasRookMoved(side, kingSide)) {
            throw new IllegalArgumentException("Invalid castling. The rook has already moved.");
        }

        int rookCol = kingSide ? 7 : 0;
        ChessPiece rook = board.getPiece(homeRow, rookCol);
        if (rook == null || rook.getSide() != side || rook.getType() != ChessPieceType.ROOK) {
            throw new IllegalArgumentException("Invalid castling. Required rook is missing.");
        }

        int start = Math.min(from.col(), rookCol) + 1;
        int end = Math.max(from.col(), rookCol) - 1;
        for (int col = start; col <= end; col++) {
            if (board.getPiece(homeRow, col) != null) {
                throw new IllegalArgumentException("Invalid castling. The path is blocked.");
            }
        }

        ChessSide opponent = side.opposite();
        int passCol = kingSide ? 5 : 3;
        if (isSquareAttacked(board, from, opponent)
            || isSquareAttacked(board, new Position(homeRow, passCol), opponent)
            || isSquareAttacked(board, to, opponent)) {
            throw new IllegalArgumentException("Invalid castling. The king is in check or crosses an attacked square.");
        }

        return MovePlan.castling(kingSide);
    }

    private ChessPiece executeMove(
        ChessBoard board,
        ChessPiece movingPiece,
        Position from,
        Position to,
        MovePlan plan
    ) {
        ChessPiece capturedPiece = plan.enPassant()
            ? board.getPiece(plan.enPassantCapture())
            : board.getPiece(to);

        board.setPiece(from, null);
        if (plan.enPassant()) {
            board.setPiece(plan.enPassantCapture(), null);
        }

        if (movingPiece.getType() == ChessPieceType.KING) {
            board.markKingMoved(movingPiece.getSide());
        }
        if (movingPiece.getType() == ChessPieceType.ROOK) {
            board.markRookMovedFrom(from);
        }

        ChessPiece placedPiece = movingPiece;
        if (plan.promotionType() != null) {
            placedPiece = new ChessPiece(plan.promotionType(), movingPiece.getSide());
        }
        board.setPiece(to, placedPiece);

        if (plan.castling()) {
            int row = from.row();
            int rookFromCol = plan.kingSideCastle() ? 7 : 0;
            int rookToCol = plan.kingSideCastle() ? 5 : 3;
            Position rookFrom = new Position(row, rookFromCol);
            Position rookTo = new Position(row, rookToCol);
            ChessPiece rook = board.getPiece(rookFrom);
            board.setPiece(rookFrom, null);
            board.setPiece(rookTo, rook);
            board.markRookMovedFrom(rookFrom);
        }

        board.clearEnPassant();
        if (plan.nextEnPassantTarget() != null) {
            board.setEnPassant(plan.nextEnPassantTarget(), plan.nextEnPassantCapturedPawn());
        }
        return capturedPiece;
    }

    private boolean isPathClear(ChessBoard board, Position from, Position to) {
        int rowStep = Integer.compare(to.row(), from.row());
        int colStep = Integer.compare(to.col(), from.col());
        int row = from.row() + rowStep;
        int col = from.col() + colStep;

        while (row != to.row() || col != to.col()) {
            if (board.getPiece(row, col) != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    private boolean isSquareAttacked(ChessBoard board, Position target, ChessSide attackerSide) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position attacker = new Position(row, col);
                ChessPiece piece = board.getPiece(attacker);
                if (piece != null && piece.getSide() == attackerSide && attacksSquare(board, piece, attacker, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean attacksSquare(ChessBoard board, ChessPiece piece, Position from, Position target) {
        int rowDelta = target.row() - from.row();
        int colDelta = target.col() - from.col();
        return switch (piece.getType()) {
            case KING -> Math.abs(rowDelta) <= 1 && Math.abs(colDelta) <= 1 && !from.equals(target);
            case QUEEN -> (rowDelta == 0 || colDelta == 0 || Math.abs(rowDelta) == Math.abs(colDelta))
                && isPathClear(board, from, target);
            case ROOK -> (rowDelta == 0 || colDelta == 0) && isPathClear(board, from, target);
            case BISHOP -> Math.abs(rowDelta) == Math.abs(colDelta) && isPathClear(board, from, target);
            case KNIGHT -> (Math.abs(rowDelta) == 2 && Math.abs(colDelta) == 1)
                || (Math.abs(rowDelta) == 1 && Math.abs(colDelta) == 2);
            case PAWN -> rowDelta == (piece.getSide() == ChessSide.WHITE ? -1 : 1) && Math.abs(colDelta) == 1;
        };
    }

    private ChessPieceType resolvePromotionType(Character promotionChoice) {
        if (promotionChoice == null) {
            return ChessPieceType.QUEEN;
        }

        return switch (Character.toUpperCase(promotionChoice)) {
            case 'Q' -> ChessPieceType.QUEEN;
            case 'R' -> ChessPieceType.ROOK;
            case 'B' -> ChessPieceType.BISHOP;
            case 'N' -> ChessPieceType.KNIGHT;
            default -> throw new IllegalArgumentException("Invalid promotion choice. Use q, r, b, or n.");
        };
    }

    private void rejectPromotionChoice(Character promotionChoice, String pieceName) {
        if (promotionChoice != null) {
            throw new IllegalArgumentException(
                "Invalid chess move. Promotion choice is not valid for a " + pieceName + "."
            );
        }
    }

    private void validateInside(ChessBoard board, Position position, String label) {
        if (!board.isInside(position)) {
            throw new IllegalArgumentException("Invalid chess move. The " + label + " coordinate is out of range.");
        }
    }

    private record MovePlan(
        boolean castling,
        boolean kingSideCastle,
        boolean enPassant,
        Position enPassantCapture,
        ChessPieceType promotionType,
        Position nextEnPassantTarget,
        Position nextEnPassantCapturedPawn
    ) {
        static MovePlan normal() {
            return new MovePlan(false, false, false, null, null, null, null);
        }

        static MovePlan normalPromotion(ChessPieceType promotionType) {
            return new MovePlan(false, false, false, null, promotionType, null, null);
        }

        static MovePlan castling(boolean kingSide) {
            return new MovePlan(true, kingSide, false, null, null, null, null);
        }

        static MovePlan enPassant(Position capturedPawn) {
            return new MovePlan(false, false, true, capturedPawn, null, null, null);
        }

        static MovePlan doublePawn(Position target, Position capturedPawn) {
            return new MovePlan(false, false, false, null, null, target, capturedPawn);
        }
    }
}
