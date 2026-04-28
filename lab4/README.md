# Lab 4

Lab 4 is an incremental evolution of the Lab 3 multi-game platform.

The code implementation is complete and follows `LAB4.pdf`:
- keep `peace` and `reversi` behavior from Lab 3
- add `minesweeper`
- keep multiple sessions alive at the same time
- support runtime game creation and switching
- keep the left / center / right console layout

## Requirements

- JDK 17
- Maven

## Run

```bash
mvn clean compile exec:java
```

Compile only:

```bash
mvn -q -DskipTests compile
```

## Startup State

The program starts with exactly 3 games:

- Game 1 = `peace`
- Game 2 = `reversi`
- Game 3 = `minesweeper`

The default selected game is Game 1.

## Commands

Global commands:

- `quit`: exit the whole program
- `peace`: add a new peace game
- `reversi`: add a new reversi game
- `minesweeper`: add a new minesweeper game
- `2`, `3`, `5`: switch by game number
- `switch 2`, `s 2`: switch by explicit command

Coordinates are case-insensitive and support both forms:

- `1a`
- `a1`
- `8h`
- `h8`

## Peace

- coordinate input places a piece on an empty cell
- black and white alternate turns
- no Reversi flipping is applied
- the game ends when the board is full

## Reversi

- coordinate input places a piece only on a legal Reversi move
- legal moves are shown with `+`
- pieces are flipped according to Reversi rules
- scores are shown
- `pass` is valid only when the current player has no legal move
- the game ends when the board is full or both players have no legal moves

## Minesweeper

- board size: 8 x 8
- mines: 10
- mines are generated on the first actual open
- the first actual open is always safe
- flagging before the first open is allowed and does not generate mines
- each open reveals only the target cell
- zero cells do not auto-expand, as required by the Lab 4 PDF
- opening a mine loses the game
- opening all non-mine cells wins the game

Commands:

- `1a` / `a1`: open one cell
- `f 1a`: toggle flag
- `flag a1`: toggle flag

Display:

- `#`: hidden
- `F`: flag
- `*`: mine, shown after game end
- `1-8`: neighboring mine count
- `.`: zero neighboring mines

## Notes

Finished games remain viewable and switchable, but no longer accept in-game operations.

`LAB4.pdf` is a read-only requirement reference. Do not modify it as source code.
