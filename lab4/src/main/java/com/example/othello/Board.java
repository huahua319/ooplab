package com.example.othello;

public class Board {
    private final int size;
    private final Cell[][] cells;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        initializeEmptyCells();
        initializeStartingPieces();
    }

    private void initializeEmptyCells() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = Cell.EMPTY;
            }
        }
    }

    private void initializeStartingPieces() {
        int upperCenter = size / 2 - 1;
        int lowerCenter = size / 2;
        cells[upperCenter][upperCenter] = Cell.WHITE;
        cells[upperCenter][lowerCenter] = Cell.BLACK;
        cells[lowerCenter][upperCenter] = Cell.BLACK;
        cells[lowerCenter][lowerCenter] = Cell.WHITE;
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public Cell getCell(Position position) {
        return getCell(position.row(), position.col());
    }

    public void setCell(Position position, Cell cell) {
        cells[position.row()][position.col()] = cell;
    }

    public boolean isInside(Position position) {
        return isInside(position.row(), position.col());
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isEmpty(Position position) {
        return getCell(position) == Cell.EMPTY;
    }

    public int countCells(Cell target) {
        int count = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col] == target) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isFull() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col] == Cell.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
