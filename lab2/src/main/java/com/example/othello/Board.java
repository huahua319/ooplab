package com.example.othello;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

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

    public boolean isInside(Position position) {
        return position.row() >= 0
            && position.row() < size
            && position.col() >= 0
            && position.col() < size;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isEmpty(Position position) {
        return cells[position.row()][position.col()] == Cell.EMPTY;
    }

    public void place(Position position, Cell player) {
        cells[position.row()][position.col()] = player;
    }

    public List<Position> findFlippablePositions(Position position, Cell player) {
        List<Position> flippablePositions = new ArrayList<>();
        if (player == Cell.EMPTY || !isInside(position) || !isEmpty(position)) {
            return flippablePositions;
        }

        for (int[] direction : DIRECTIONS) {
            flippablePositions.addAll(findFlippableInDirection(position, player, direction[0], direction[1]));
        }
        return flippablePositions;
    }

    public boolean isLegalMove(Position position, Cell player) {
        return !findFlippablePositions(position, player).isEmpty();
    }

    public boolean hasLegalMove(Cell player) {
        if (player == Cell.EMPTY) {
            return false;
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isLegalMove(new Position(row, col), player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int applyMove(Position position, Cell player) {
        List<Position> flippablePositions = findFlippablePositions(position, player);
        if (flippablePositions.isEmpty()) {
            throw new IllegalArgumentException("Move must flip at least one opponent piece.");
        }

        place(position, player);
        for (Position flippablePosition : flippablePositions) {
            place(flippablePosition, player);
        }
        return flippablePositions.size();
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

    private List<Position> findFlippableInDirection(Position position, Cell player, int rowDelta, int colDelta) {
        List<Position> capturedPositions = new ArrayList<>();
        int row = position.row() + rowDelta;
        int col = position.col() + colDelta;

        while (isInside(row, col) && cells[row][col] == player.oppositePlayer()) {
            capturedPositions.add(new Position(row, col));
            row += rowDelta;
            col += colDelta;
        }

        if (capturedPositions.isEmpty() || !isInside(row, col) || cells[row][col] != player) {
            return List.of();
        }
        return capturedPositions;
    }
}
