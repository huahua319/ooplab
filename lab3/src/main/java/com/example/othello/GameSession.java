package com.example.othello;

public class GameSession {
    private final GameType gameType;
    private final Board board;
    private Cell currentPlayer;
    private boolean finished;
    private String statusMessage;
    private String finishMessage;

    public GameSession(GameType gameType, Board board) {
        this.gameType = gameType;
        this.board = board;
        this.currentPlayer = Cell.BLACK;
        this.finished = false;
        this.statusMessage = "";
        this.finishMessage = "";
    }

    public GameType getGameType() {
        return gameType;
    }

    public Board getBoard() {
        return board;
    }

    public Cell getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Cell currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinished(String finishMessage) {
        this.finished = true;
        this.finishMessage = finishMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getFinishMessage() {
        return finishMessage;
    }
}
