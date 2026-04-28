# Lab4 Local Specification

## 1. Document Positioning

This file is the local development specification for Lab 4 inside `lab4/`.

Priority rules:
- the Lab 4 PDF (`LAB4.pdf`) is the highest-priority requirement source for Lab 4
- the root `SPEC.md` provides repository-wide constraints
- this file refines local implementation rules for Lab 4 work
- Lab 2 and Lab 3 local SPEC files are historical inheritance background only
- if there is a conflict, prefer: Lab 4 PDF > root `SPEC.md` > this Lab 4 local SPEC > Lab 3 / Lab 2 historical SPEC files

Lab 4 is an incremental evolution of the existing Lab 3 multi-game platform. It is not a rewrite and should not break existing `peace` or `reversi` behavior.

Current implementation status:
- Lab 4 code implementation is complete in `lab4/`
- the project compiles with Maven
- manual interaction testing has reported no known bugs
- remaining work is expected to be documentation, report writing, submission packaging, or bug fixes if discovered

---

## 2. Lab 4 Goals

Lab 4 extends the Lab 3 platform by adding a playable `minesweeper` mode.

The concrete Lab 4 goals are:
- add `minesweeper` as a third game mode in the existing platform
- keep multiple game sessions alive at the same time
- allow adding new `peace`, `reversi`, or `minesweeper` sessions during runtime
- keep each session's state when switching between games
- preserve the unified input style across modes
- preserve the left / center / right console layout:
  - left = board area
  - center = current game state and recent message
  - right = game list
- keep Lab 3 `peace` and `reversi` logic working without regression

Do not implement extra game types or future-lab behavior unless a later lab explicitly requires it.

---

## 3. Functional Requirements Breakdown

### 3.1 Initial game list

At program startup, initialize exactly 3 games:
- Game 1 = `peace`
- Game 2 = `reversi`
- Game 3 = `minesweeper`

The current selected game at startup must be Game 1.

### 3.2 Switching games

The user may switch games by entering:
- a bare game number, such as `2` or `5`
- `switch N`, such as `switch 2`
- `s N`, such as `s 2`

Required behavior:
- switching must preserve the target session's previous state
- switching to a finished game is allowed for viewing
- finished games must not accept further in-game operations
- entering a non-existent game number must show a clear error message

### 3.3 Adding games

The user may add games during runtime:
- input `peace` adds a new `peace` session
- input `reversi` adds a new `reversi` session
- input `minesweeper` adds a new `minesweeper` session

Required behavior:
- new games are appended to the end of the game list
- adding a game must not automatically switch to it
- the valid switch number range grows with the game list
- the game list UI must reflect the new game immediately

### 3.4 Peace rules

`peace` keeps the Lab 3 behavior:
- empty cells may be occupied
- players take turns placing pieces
- the game ends when the board is full
- malformed coordinates, out-of-range coordinates, and occupied targets must be rejected clearly
- do not apply Reversi flipping rules to `peace`

### 3.5 Reversi rules

`reversi` keeps the Lab 3 behavior:
- move legality must follow full Reversi rules
- legal moves must flip all required opponent pieces
- legal positions for the current player must be shown
- scores for both players must be shown
- `pass` is valid only when the current player has no legal move
- if the current player has at least one legal move, inputting `pass` must be rejected
- the game ends when the board is full or both players have no legal moves
- after finish, show final score and winner / draw result

### 3.6 Minesweeper rules

`minesweeper` must be fully playable for one game.

Board and mine setup:
- board size is fixed at 8 x 8
- mine count is fixed at 10
- mines are generated only when the first cell is opened
- the first opened cell must not be a mine
- flagging before the first open is allowed and must not trigger mine generation
- mines are generated only by the first actual open operation
- first-open safety applies to the first actual open coordinate
- coordinate input must accept both `1a` / `8h` and `a1` / `h8`
- coordinate parsing must be case-insensitive

Open behavior:
- coordinate input opens one cell in `minesweeper`
- this version opens only the target cell
- do not implement automatic zero-area expansion
- only hidden and unflagged cells may be opened
- opening an already open cell must show an error
- opening a flagged cell must show an error and require unflagging first
- a flagged cell still cannot be opened until it is unflagged
- if the opened cell is a mine, the game immediately fails and ends
- if the opened cell is safe, show the neighboring mine count from the adjacent 8 cells
- counts `1` through `8` are shown as digits
- count `0` is shown as `.`

Flag behavior:
- `f coord` toggles a flag on the target cell
- `flag coord` toggles a flag on the target cell
- flags may only be placed on hidden cells
- flagging an already open cell must show an error
- toggling a flagged hidden cell removes the flag

Win / loss behavior:
- when all non-mine cells have been opened, the game wins and ends
- when a mine is opened, the game loses and ends
- after the game ends, it remains viewable but must reject further in-game operations

### 3.7 Program exit

Input `quit` exits the whole program from any mode and any game state.

---

## 4. Recommended Architecture

Keep the Lab 4 implementation incremental and aligned with the existing Lab 3 structure.

Recommended direction:
- extend `GameType` or the equivalent type identifier with `MINESWEEPER`
- keep session management list-based instead of hardcoding Game 1 / Game 2 / Game 3 branches
- platform-level code should handle:
  - game list ownership
  - current selected game id
  - switching games
  - adding sessions by type
  - whole-program `quit`
  - dispatching input to the active mode
- mode-level code should handle each mode's own rules:
  - `peace` placement and board-full ending
  - `reversi` legality, flipping, pass, scoring, and ending
  - `minesweeper` opening, flagging, first-open mine generation, and win/loss detection
- UI code should render state and messages, not contain the core rule logic

If the existing `GameSession` structure can naturally hold all mode states, keep using it. If it becomes awkward for `minesweeper`, introduce mode-specific state/session/rule classes.

Possible responsibilities, subject to existing code shape:
- `MinesweeperBoard` or equivalent board/state object
- `MinesweeperCell` or equivalent cell state with hidden/open/flagged/mine/neighbor-count data
- `MinesweeperSession` or equivalent mode-specific session state
- `MinesweeperRules` or equivalent rule helper
- `CommandParser` for shared input parsing
- `GameFactory` or equivalent creation logic for `peace`, `reversi`, and `minesweeper`

Avoid one giant controller or UI class that embeds all `peace`, `reversi`, and `minesweeper` rules in a long unrelated branch.

---

## 5. Input Handling Rules

Recommended input parsing order:

1. `quit`
2. `peace` / `reversi` / `minesweeper`
3. `switch N` / `s N`
4. bare game number `N`
5. flag command: `f coord` / `flag coord`
6. `pass`
7. coordinate command
8. otherwise invalid input

Coordinate interpretation:
- in `peace`, a coordinate means placing a piece
- in `reversi`, a coordinate means placing a piece according to Reversi rules
- in `minesweeper`, a coordinate means opening exactly one cell

Accepted coordinate examples:
- `1a`
- `8h`
- `a1`
- `h8`

Input is case-insensitive where appropriate.

Invalid input must produce a clear error and allow the user to continue:
- malformed input such as `abc` or `@3`
- out-of-range coordinates such as `9a` or `1k`
- repeated open on an already opened cell
- flagging an already opened cell
- opening a flagged cell
- non-existent game number
- `pass` in `reversi` when legal moves still exist
- `pass` in `peace` or `minesweeper`
- any in-game operation on a finished game

---

## 6. UI / Output Rules

Keep the left / center / right relative layout. The output does not need to match the PDF examples pixel-for-pixel, but the information must be clear.

Left area:
- render the current game's board
- show coordinates
- for `minesweeper`, initially show all hidden cells as `#`

Center area:
- show current game number
- show mode
- show player / turn information for `peace` and `reversi`
- show single-player mode information for `minesweeper`
- show mode-specific status
- show the most recent message or error

Right area:
- show the full game list
- each item must include game number, game type, and state
- clearly mark the current game

Minesweeper display:
- hidden cell = `#`
- flagged cell = `F`
- mine = `*` after game end or when showing the result
- neighboring mine counts = `1` through `8`
- zero neighboring mines = `.`
- mines should not be directly exposed before game end

Suggested minesweeper status fields:
- `Mode: minesweeper`
- `Mines: 10`
- `Flags: <current flag count>`
- `Safe cells left: <count>` or opened safe cell count
- `First open is always safe`
- `Basic mode: no auto-expansion for zero cells`

Finished sessions:
- remain renderable
- show win/loss or final result
- reject further in-game operations with a clear message

---

## 7. File Scope

Normally allowed to modify for Lab 4 maintenance, bug fixes, and documentation:
- `lab4/src/main/java/**`
- `lab4/pom.xml`
- `lab4/README.md`
- `lab4/SPEC_lab4.md`

Do not modify unless explicitly requested:
- `lab1/**`
- `lab2/**`
- `lab3/**`
- `_private/**`
- `target/**`
- report PDFs
- submission zip archives
- Lab PDFs are read-only requirement references
- do not modify Lab PDFs
- do not commit or submit Lab PDFs as source changes unless the user explicitly requests it or the course submission format requires it
- Java source code
- `pom.xml`
- `README.md`

---

## 8. Acceptance Checklist

Before considering Lab 4 implementation complete, verify:

- Maven build succeeds
- startup initializes exactly 3 games
- Game 1 is `peace`
- Game 2 is `reversi`
- Game 3 is `minesweeper`
- startup enters Game 1 by default
- entering `3`, `switch 3`, or `s 3` switches to minesweeper
- minesweeper initial board shows all cells as `#`
- minesweeper board is 8 x 8
- minesweeper uses exactly 10 mines
- mines are generated on first open
- first open is always safe
- each minesweeper open reveals only one cell
- zero cells do not auto-expand
- `f 1a` toggles a flag
- `flag a1` toggles a flag
- flagged cells cannot be opened directly
- opened cells cannot be opened again
- opened cells cannot be flagged
- opening a mine fails and ends that minesweeper game
- opening all non-mine cells wins and ends that minesweeper game
- ended games remain viewable
- ended games reject further in-game operations
- input `minesweeper` adds a new minesweeper session
- input `peace` adds a new peace session without regression
- input `reversi` adds a new reversi session without regression
- adding a game does not automatically switch to it
- switching games preserves each session's state
- `peace` still allows empty-cell placement and alternating turns
- `peace` still ends when the board is full
- `reversi` still detects legal moves
- `reversi` still flips pieces
- `reversi` still shows legal move hints
- `reversi` still shows scores
- `reversi` still validates `pass`
- `reversi` still detects game end
- invalid game numbers show clear errors
- malformed coordinates show clear errors
- out-of-range coordinates show clear errors
- `pass` in `peace` or `minesweeper` is rejected
- `pass` in `reversi` is rejected when legal moves exist
- `quit` exits the program from any state

---

## 9. Report Awareness

The code structure should support later Lab 4 report writing.

The report should be able to explain:
- the student's predicted changes versus the AI's actual modifications
- the incremental design relative to Lab 3
- newly added or modified key classes and responsibilities
- command parsing and game creation flow
- minesweeper core rules:
  - first-open safety
  - flag toggling
  - win/loss detection
  - no automatic zero-area expansion
- multi-game switching and dynamic creation flow
- screenshots for multi-game state and minesweeper interactions
- test cases and results

Keep implementation choices clear enough to describe in a report or UML diagram.
