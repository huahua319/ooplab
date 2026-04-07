package com.example.othello;

public class GameSession {
    private final Board board;
    private Cell currentPlayer;
    private boolean finished;
    private String statusMessage;
    private String finishMessage;

    public GameSession(Board board) {
        this.board = board;
        this.currentPlayer = Cell.BLACK;
        this.finished = false;
        this.statusMessage = "BLACK moves first.";
        this.finishMessage = "";
    }

    public Board getBoard() {
        return board;
    }

    public Cell getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getFinishMessage() {
        return finishMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void prepareTurnIfNeeded() {
        if (finished) {
            return;
        }

        if (board.isFull()) {
            finish("Board finished: board is full.");
            return;
        }

        if (!board.hasLegalMove(Cell.BLACK) && !board.hasLegalMove(Cell.WHITE)) {
            finish("Board finished: neither player has a legal move.");
            return;
        }

        if (!board.hasLegalMove(currentPlayer)) {
            Cell passedPlayer = currentPlayer;
            currentPlayer = currentPlayer.oppositePlayer();
            statusMessage = playerName(passedPlayer) + " has no legal move. Turn passes to " + playerName(currentPlayer) + ".";
        }
    }

    public int applyMove(Position position) {
        int flippedCount = board.applyMove(position, currentPlayer);
        String moveMessage = "Move accepted. Flipped " + flippedCount + " piece(s).";
        currentPlayer = currentPlayer.oppositePlayer();

        if (board.isFull()) {
            finish("Board finished: board is full.");
            return flippedCount;
        }

        boolean currentPlayerHasMove = board.hasLegalMove(currentPlayer);
        boolean otherPlayerHasMove = board.hasLegalMove(currentPlayer.oppositePlayer());
        if (!currentPlayerHasMove && !otherPlayerHasMove) {
            finish("Board finished: neither player has a legal move.");
            return flippedCount;
        }

        if (!currentPlayerHasMove) {
            Cell passedPlayer = currentPlayer;
            currentPlayer = currentPlayer.oppositePlayer();
            statusMessage = moveMessage + " " + playerName(passedPlayer) + " has no legal move. Turn passes to " + playerName(currentPlayer) + ".";
            return flippedCount;
        }

        statusMessage = moveMessage;
        return flippedCount;
    }

    private void finish(String message) {
        finished = true;
        finishMessage = message;
        statusMessage = message;
    }

    private String playerName(Cell player) {
        return player.name();
    }
}
