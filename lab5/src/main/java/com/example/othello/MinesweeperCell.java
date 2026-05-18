package com.example.othello;

public class MinesweeperCell {
    private boolean mine;
    private boolean opened;
    private boolean flagged;
    private int neighboringMines;

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isOpened() {
        return opened;
    }

    public void open() {
        this.opened = true;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void toggleFlag() {
        this.flagged = !this.flagged;
    }

    public int getNeighboringMines() {
        return neighboringMines;
    }

    public void setNeighboringMines(int neighboringMines) {
        this.neighboringMines = neighboringMines;
    }
}
