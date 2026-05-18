# Lab5 Local Specification

## 1. Document Positioning

This file is the local development specification for Lab 5 inside `lab5/`.

Priority rules:
- the Lab 5 PDF (`LAB5.pdf`) is the highest-priority requirement source for Lab 5
- the root `SPEC.md` provides repository-wide constraints
- this file records Lab 5 local interpretation for later implementation and report writing
- Lab 4, Lab 3, and Lab 2 SPEC files are historical inheritance background only
- if there is a conflict, prefer: Lab 5 PDF > root `SPEC.md` > this Lab 5 local SPEC > Lab 4 / Lab 3 / Lab 2 historical SPEC files

Lab 5 is an incremental evolution of the Lab 4 multi-game platform. It is not a rewrite of the course project from scratch, but the PDF explicitly requires architectural refactoring toward a plugin-style game lobby.

Do not implement Lab 6 behavior or extra game types unless a later lab explicitly requires them.

---

## 2. Lab 5 Goals

Lab 5 extends Lab 4 with three major additions:
- add a playable `chess` mode
- add a `demo` mode that automatically demonstrates game play
- refactor the platform into a plugin-style architecture where the game lobby can host independently packaged games through a shared interface

The concrete Lab 5 goals from the PDF are:
- keep the existing `peace`, `reversi`, and `minesweeper` behavior from Lab 4 without regression
- initialize four games at startup: `peace`, `reversi`, `minesweeper`, and `chess`
- support multiple concurrent sessions and preserve each session state while switching
- support runtime creation of new `peace`, `reversi`, `minesweeper`, and `chess` sessions
- keep the unified left / center / right console layout
- adjust the Commands prompt so each game only shows commands relevant to the current game plus game-switching commands
- allow a complete game of chess to run under the simplified Lab 5 win condition
- implement the required chess movement rules, including special moves required by the PDF
- provide a demo mode that automatically runs through all game modes

Do not invent features that are not in the Lab 5 PDF. In particular, draw rules for chess are explicitly not required.

---

## 3. Functional Requirements Breakdown

### 3.1 Initial game list

At program startup, initialize exactly 4 games:
- Game 1 = `peace`
- Game 2 = `reversi`
- Game 3 = `minesweeper`
- Game 4 = `chess`

The current selected game at startup must be Game 1.

### 3.2 Switching games

The user may switch games by entering:
- a bare game number, such as `2` or `5`
- `switch N`, such as `switch 2`
- `s N`, such as `s 2`

Required behavior:
- switching must preserve the target session's complete previous state
- switching to a finished game is allowed for viewing
- finished games must not accept further in-game operations
- entering a non-existent game number must show a clear error message

### 3.3 Adding games

The user may add games during runtime:
- input `peace` adds a new `peace` session
- input `reversi` adds a new `reversi` session
- input `minesweeper` adds a new `minesweeper` session
- input `chess` adds a new `chess` session

Required behavior:
- new games are appended to the end of the game list
- adding a game must not automatically switch to it
- the user must switch manually by number, `switch N`, or `s N`
- the valid switch number range grows with the game list
- the game list UI must reflect the new game immediately

### 3.4 Peace requirements

`peace` must keep the Lab 4 behavior:
- empty cells may be occupied
- players take turns placing pieces
- the game ends when the board is full
- malformed coordinates, out-of-range coordinates, and occupied targets must be rejected clearly
- do not apply Reversi flipping rules to `peace`

### 3.5 Reversi requirements

`reversi` must keep the Lab 4 behavior:
- move legality follows full Reversi rules
- legal moves flip all required opponent pieces
- legal positions for the current player are shown
- scores for both players are shown
- `pass` is valid only when the current player has no legal move
- if the current player has at least one legal move, inputting `pass` is rejected
- the game ends when the board is full or both players have no legal moves
- after finish, show final score and winner / draw result

### 3.6 Minesweeper requirements

`minesweeper` must keep the Lab 4 behavior:
- fixed 8 x 8 board
- fixed 10 mines
- mines are generated only when the first cell is opened
- the first opened cell must not be a mine
- flagging before the first open is allowed and must not trigger mine generation
- each open reveals only the target cell
- do not implement automatic zero-area expansion
- `f coord` and `flag coord` toggle a flag
- win when all non-mine cells have been opened
- loss when a mine is opened
- ended minesweeper games remain viewable and reject further operations

### 3.7 Chess requirements

`chess` is the new Lab 5 game mode.

Board and initial state:
- board size is fixed at 8 x 8
- the board uses chess's standard initial position
- the PDF legend defines black pieces with uppercase letters: `K`, `Q`, `R`, `B`, `N`, `P`
- the PDF legend defines white pieces with lowercase letters: `k`, `q`, `r`, `b`, `n`, `p`
- empty cells are shown as `.`
- the PDF example moves a white pawn from `7a` to `5a`, then a black knight from `1b` to `3c`; implementation should keep the coordinate orientation consistent with this example
- white moves first, consistent with chess rules and the PDF example

Move commands:
- `m from to`, such as `m 1a 2a`
- `move from to`, such as `move 1a 2a`

Required move behavior:
- only the current player's pieces may be moved
- a source cell must contain a piece
- a destination cell must be in range
- a destination cell may not contain the current player's own piece
- legal captures of opponent pieces are allowed
- turn ownership changes after a legal move
- illegal moves do not change the board or turn
- every illegal chess move must produce a clear user-facing message

Required piece rules:
- king movement must follow chess king movement rules
- queen movement must follow chess queen movement rules
- rook movement must follow chess rook movement rules
- bishop movement must follow chess bishop movement rules
- knight movement must follow chess knight movement rules
- pawn movement must follow chess pawn movement rules, including normal movement and diagonal capture

Required special rules:
- castling must work normally when its chess preconditions are satisfied
- en passant must work normally when its chess preconditions are satisfied
- pawn promotion must work normally when a pawn reaches the final rank
- the PDF does not prescribe a promotion input format; later implementation must provide a clear, minimal, and documented way to choose or resolve the promoted piece
- draw rules are not required

Simplified Lab 5 chess ending:
- the game ends immediately when one side's king is captured
- do not require original chess checkmate detection as the ending condition
- after a king is captured, show the winner and mark the chess session finished
- after finish, the chess session remains viewable but must reject further `m` / `move` operations with a clear "game already ended" style message

### 3.8 Demo mode

Lab 5 requires a demo mode.

Required behavior:
- demo mode automatically runs game commands to demonstrate game content
- demo mode demonstrates all game modes: `peace`, `reversi`, `minesweeper`, and `chess`
- the demo UI still uses the left / center / right layout
- demo mode should show which game mode is currently being demonstrated
- the PDF example shows automatic steps and start / pause / quit controls

The PDF does not define the exact command or launcher used to enter demo mode. Later implementation must provide a directly runnable and discoverable entry mechanism for demo mode, without breaking the required normal-game commands.

---

## 4. Inherited Behavior From Previous Labs

The following Lab 4 behaviors must be preserved unless the Lab 5 PDF explicitly overrides them:
- `peace` behavior
- `reversi` behavior
- `minesweeper` behavior
- multiple independent sessions
- dynamic game creation
- switching sessions while preserving state
- left / center / right relative layout
- finished games remain viewable
- finished games reject further in-game operations
- `quit` exits the whole program from any mode and any game state
- invalid input is rejected clearly without corrupting session state

Lab 5 changes the startup list from 3 games to 4 games by adding `chess`.

Lab 5 expands dynamic creation so `chess` can be created at runtime in addition to the three Lab 4 modes.

Lab 5 expands the architecture expectation by requiring plugin-style game modules and a shared lobby interface.

---

## 5. Recommended Architecture

Keep the implementation incremental, but do not simply add more logic into one large controller or one large UI class. Lab 5 explicitly asks for a plugin-style architecture.

Recommended platform split:
- a game lobby or manager owns the list of sessions, current selected session, switching, creation, and whole-program quit
- each game type is independently packaged as a module or package
- each game module exposes a shared interface for session creation, command handling, rendering data, status text, and demo commands
- the lobby depends on the shared interface, not on hardcoded mode-specific branches for every game rule
- the UI renders common layout areas and delegates mode-specific board/status/command text to the active game module

Existing Lab 4 structures that can guide the migration:
- `GameType`
- `GameSession`
- `GameManager`
- `ModeRules`
- `PeaceRules`
- `ReversiRules`
- `MinesweeperRules`
- `MinesweeperBoard`
- `MinesweeperCell`
- `ConsoleUI`
- `Main`

Possible Lab 5 responsibilities:
- shared game plugin interface for all modes
- game registry or factory for `peace`, `reversi`, `minesweeper`, and `chess`
- lobby/session layer for multi-game management
- `chess` package containing board state, piece model, move validation, special move handling, and chess-specific result state
- demo runner that obtains demo scripts or demo commands from each game module

Suggested chess-specific responsibilities:
- chess board state
- chess piece type and side
- move parser for `m from to` and `move from to`
- piece-specific movement validation
- path-blocking validation for sliding pieces
- pawn special-state tracking for double-step and en passant
- castling-right tracking for king and rooks
- promotion handling
- king-capture win detection

Do not require exact class names unless the implementation shape makes them obvious. The important Lab 5 requirement is the separation of lobby/core code from game-specific modules through a shared interface.

---

## 6. Input Handling Rules

Recommended input parsing order for normal mode:

1. `quit`
2. new game commands: `peace`, `reversi`, `minesweeper`, `chess`
3. `switch N` / `s N`
4. bare game number `N`
5. mode-specific command:
   - `minesweeper`: `f coord` / `flag coord`
   - `chess`: `m from to` / `move from to`
6. `pass`
7. coordinate command
8. otherwise invalid input

Coordinate interpretation:
- in `peace`, a coordinate means placing a piece
- in `reversi`, a coordinate means placing a piece according to Reversi rules
- in `minesweeper`, a coordinate means opening exactly one cell
- in `chess`, coordinates appear inside `m from to` or `move from to`

Accepted coordinate style should remain compatible with Lab 4 examples such as:
- `1a`
- `8h`
- `a1`
- `h8`

Input is case-insensitive where appropriate.

Invalid input must produce a clear error and allow the user to continue:
- malformed input
- out-of-range coordinates
- invalid game numbers
- invalid or unavailable commands for the active mode
- `pass` in modes where it is not legal
- repeated or forbidden operations from inherited modes
- any in-game operation on a finished game
- any illegal chess move

Commands shown in the UI should be mode-specific. The right-side Commands prompt should show only commands available to the current game plus game switching / creation / quit commands.

---

## 7. UI / Output Rules

Keep the left / center / right relative layout. The output does not need to match the PDF screenshots pixel-for-pixel, but the information must be clear.

Left area:
- render the current game's board
- show coordinates
- for `chess`, show the 8 x 8 chess board and use the PDF piece legend
- for `minesweeper`, keep the Lab 4 hidden / flag / open display behavior

Center area:
- show current game number
- show game mode
- show player / turn information for two-player games
- show single-player information for `minesweeper`
- show mode-specific status
- show the most recent message or error
- for `chess`, show current side to move and game result when finished
- for demo mode, show which mode is being demonstrated and demo progress information

Right area:
- show the full game list
- each item must include game number and game type
- clearly mark the current game
- show whether each game is running or finished when available
- include a Commands prompt area that is specific to the active game

Chess legend:
- `K` = black king
- `Q` = black queen
- `R` = black rook
- `B` = black bishop
- `N` = black knight
- `P` = black pawn
- `k` = white king
- `q` = white queen
- `r` = white rook
- `b` = white bishop
- `n` = white knight
- `p` = white pawn
- `.` = empty cell

Finished sessions:
- remain renderable
- show win/loss or final result
- reject further in-game operations with a clear message

---

## 8. File Scope

Usually allowed for Lab 5 maintenance when explicitly requested:
- `lab5/src/main/java/**`
- `lab5/pom.xml`
- `lab5/README.md`
- `lab5/SPEC_lab5.md`

Lab 5 code development is complete. Future work in `lab5/` should normally be limited to bug fixes, documentation cleanup, report support, or user-requested refinements.

Do not modify unless explicitly requested:
- `lab1/**`
- `lab2/**`
- `lab3/**`
- `lab4/**`
- `_private/**`
- `target/**`
- report PDFs
- submission zip archives
- Java source code
- `pom.xml`
- `README.md`

Lab PDFs are read-only requirement references.
Do not modify Lab PDFs.
Do not commit or submit Lab PDFs as source changes unless the user explicitly requests it or the course submission format requires it.

---

## 9. Acceptance Checklist

Before considering Lab 5 implementation complete, verify:

- Maven build succeeds
- startup initializes exactly 4 games
- Game 1 is `peace`
- Game 2 is `reversi`
- Game 3 is `minesweeper`
- Game 4 is `chess`
- startup enters Game 1 by default
- entering `4`, `switch 4`, or `s 4` switches to chess
- switching games preserves every session's state
- finished games remain viewable
- finished games reject further in-game operations
- input `peace` adds a new peace session
- input `reversi` adds a new reversi session
- input `minesweeper` adds a new minesweeper session
- input `chess` adds a new chess session
- adding a game does not automatically switch to it
- `peace` behavior does not regress from Lab 4
- `reversi` behavior does not regress from Lab 4
- `minesweeper` behavior does not regress from Lab 4
- chess board is 8 x 8
- chess initial position is correct and matches the PDF coordinate examples
- chess piece letters follow the PDF legend
- `m from to` moves a chess piece when legal
- `move from to` moves a chess piece when legal
- chess rejects moving from an empty source cell
- chess rejects moving an opponent piece
- chess rejects moving onto a current player's own piece
- chess rejects out-of-range and malformed move coordinates
- chess validates king moves
- chess validates queen moves
- chess validates rook moves
- chess validates bishop moves
- chess validates knight moves
- chess validates pawn moves
- chess supports castling
- chess supports en passant
- chess supports pawn promotion
- chess ends when a king is captured
- chess displays the winner after a king is captured
- chess does not require draw-rule implementation
- chess does not require original checkmate-based ending
- illegal chess moves show clear messages and do not change state
- right-side Commands prompt changes according to the current game
- demo mode can be launched or entered through a clear mechanism
- demo mode automatically demonstrates all four game modes
- demo mode displays demo status and progress
- invalid game numbers show clear errors
- malformed coordinates show clear errors
- unavailable commands for a mode are rejected clearly
- `quit` exits the program from any mode and any state
- plugin-style architecture is present: game modules are independently packaged and connected to the lobby through a shared interface

---

## 10. Report Awareness

The code structure should support later Lab 5 report writing.

The report should be able to explain:
- predicted changes versus AI's actual modifications
- incremental design relative to Lab 4
- which Lab 4 features were preserved
- newly added or modified key classes and responsibilities
- plugin-style architecture and how games connect to the lobby
- chess piece movement rules
- chess special rules: castling, en passant, and promotion
- Lab 5 simplified chess win condition: king capture
- multi-game switching and dynamic creation flow
- demo mode flow
- test cases and results
- screenshots for multi-game state, chess interactions, and demo mode

Keep implementation choices clear enough to describe in a report or UML diagram.

---

## 11. Implementation Status

Lab 5 implementation is complete as of the current repository state.

Completed work:
- `lab5/` is an independent Maven project
- Lab 4 code was used as the baseline without modifying `lab4/`
- startup initializes exactly 4 games: `peace`, `reversi`, `minesweeper`, and `chess`
- dynamic game creation supports all 4 modes
- game switching preserves session state
- finished games remain viewable and reject further in-game operations
- `peace`, `reversi`, and `minesweeper` behavior has been preserved from Lab 4
- `chess` mode has been implemented with basic movement, captures, castling, en passant, pawn promotion, and king-capture ending
- demo mode has been implemented
- plugin-style registration is implemented through `GamePlugin` and `GameRegistry`
- Maven compile verification has passed
- manual acceptance testing was completed by the user
- Lab 5 report and submission packaging have been completed by the user

Future lab work should start in a new independent directory such as `lab6/`, while `lab5/` should be preserved as the completed Lab 5 snapshot unless maintenance is explicitly requested.
