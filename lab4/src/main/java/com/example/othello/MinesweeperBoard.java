package com.example.othello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinesweeperBoard {
    public static final int SIZE = 8;
    public static final int MINE_COUNT = 10;

    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    private final MinesweeperCell[][] cells;
    private boolean minesGenerated;
    private int openedSafeCells;

    public MinesweeperBoard() {
        this.cells = new MinesweeperCell[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new MinesweeperCell();
            }
        }
    }

    public int getSize() {
        return SIZE;
    }

    public int getMineCount() {
        return MINE_COUNT;
    }

    public boolean hasGeneratedMines() {
        return minesGenerated;
    }

    public MinesweeperCell getCell(Position position) {
        return cells[position.row()][position.col()];
    }

    public OpenResult open(Position position) {
        MinesweeperCell cell = getCell(position);
        if (cell.isOpened()) {
            throw new IllegalArgumentException("Invalid open. The cell is already open.");
        }
        if (cell.isFlagged()) {
            throw new IllegalArgumentException("Invalid open. Remove the flag before opening this cell.");
        }

        if (!minesGenerated) {
            generateMines(position);
        }

        cell.open();
        if (cell.isMine()) {
            return OpenResult.MINE;
        }

        openedSafeCells++;
        if (openedSafeCells == safeCellCount()) {
            return OpenResult.WIN;
        }
        return OpenResult.SAFE;
    }

    public boolean toggleFlag(Position position) {
        MinesweeperCell cell = getCell(position);
        if (cell.isOpened()) {
            throw new IllegalArgumentException("Invalid flag. The cell is already open.");
        }

        cell.toggleFlag();
        return cell.isFlagged();
    }

    public int getFlagCount() {
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col].isFlagged()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getOpenedSafeCells() {
        return openedSafeCells;
    }

    public int getSafeCellsLeft() {
        return safeCellCount() - openedSafeCells;
    }

    public char displaySymbol(Position position, boolean gameFinished) {
        MinesweeperCell cell = getCell(position);
        if (cell.isFlagged() && !(gameFinished && cell.isMine())) {
            return 'F';
        }
        if (!cell.isOpened()) {
            if (gameFinished && cell.isMine()) {
                return '*';
            }
            return '#';
        }
        if (cell.isMine()) {
            return '*';
        }
        int neighboringMines = cell.getNeighboringMines();
        return neighboringMines == 0 ? '.' : (char) ('0' + neighboringMines);
    }

    private void generateMines(Position firstOpen) {
        List<Position> candidates = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position position = new Position(row, col);
                if (!position.equals(firstOpen)) {
                    candidates.add(position);
                }
            }
        }

        Collections.shuffle(candidates);
        for (int index = 0; index < MINE_COUNT; index++) {
            getCell(candidates.get(index)).setMine(true);
        }

        calculateNeighborCounts();
        minesGenerated = true;
    }

    private void calculateNeighborCounts() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setNeighboringMines(countNeighboringMines(row, col));
            }
        }
    }

    private int countNeighboringMines(int row, int col) {
        int count = 0;
        for (int[] direction : DIRECTIONS) {
            int nextRow = row + direction[0];
            int nextCol = col + direction[1];
            if (isInside(nextRow, nextCol) && cells[nextRow][nextCol].isMine()) {
                count++;
            }
        }
        return count;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private int safeCellCount() {
        return SIZE * SIZE - MINE_COUNT;
    }

    public enum OpenResult {
        SAFE,
        MINE,
        WIN
    }
}
