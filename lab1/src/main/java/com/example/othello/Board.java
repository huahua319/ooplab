package com.example.othello;

public class Board {
    private final int size;
    private final Cell[][] cells;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = Cell.EMPTY;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public boolean isInside(Position position) {
        return position.row() >= 0
            && position.row() < size
            && position.col() >= 0
            && position.col() < size;
    }

    public boolean isEmpty(Position position) {
        return cells[position.row()][position.col()] == Cell.EMPTY;
    }

    public void place(Position position, Cell player) {
        cells[position.row()][position.col()] = player;
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
