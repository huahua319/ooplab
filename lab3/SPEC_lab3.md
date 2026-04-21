# Lab3 Local Specification

## 1. Document Positioning

This file is the local development specification for Lab 3 inside `lab3/`.

Priority rules:
- the current Lab 3 PDF is the primary requirement source
- the root `SPEC.md` provides repository-wide constraints
- this file refines local implementation rules for Lab 3 work
- if any statement here conflicts with the Lab 3 PDF or root `SPEC.md`, prefer the Lab 3 PDF first, then the root `SPEC.md`

---

## 2. Lab 3 Goals

Lab 3 turns the previous Othello project into a small multi-game platform.

The concrete Lab 3 goals are:
- support two game modes:
  - `peace`
  - `reversi`
- keep multiple game sessions alive at the same time
- allow adding new `peace` or `reversi` sessions during runtime
- keep each session's state when switching between games
- provide a console UI with left / center / right relative layout
- leave reasonable room for future labs to add more games, but do not implement Lab 4 behavior in advance

---

## 3. Functional Requirements Breakdown

### 3.1 Initial game list
- initialize exactly 2 games at startup
- Game 1 must be `peace`
- Game 2 must be `reversi`
- the current selected game at startup must be Game 1

### 3.2 Switching games
- user may enter a game number to switch the current active session
- switching must not reset the target session
- finished games must still remain viewable after switching back

### 3.3 Adding games
- input `peace` adds a new `peace` session at the end of the game list
- input `reversi` adds a new `reversi` session at the end of the game list
- adding a game must not automatically switch to it
- the valid switch number range must grow with the list

### 3.4 Peace rules
- initial board contains only the center four pieces
- black moves first
- players alternate placing pieces on empty cells
- there is no score display requirement for `peace`
- illegal input must be rejected clearly:
  - malformed coordinates
  - out-of-range coordinates
  - non-empty target cell
- a `peace` game finishes only when the board is full

### 3.5 Reversi rules
- initial board contains only the center four pieces
- black moves first
- full Reversi rules should inherit from and follow the rule definitions established in the previous lab documents, within the scope referenced by the Lab 3 PDF
- move legality must follow full Reversi rules
- a legal move must flip all required opponent pieces
- all legal moves for the current player must be shown on the board using `+`
- scores for both players must be shown
- if the current player has no legal move, the UI must clearly tell the user
- `pass` is valid only when the current player truly has no legal move
- if legal moves exist, inputting `pass` must be rejected
- a `reversi` game finishes when:
  - the board is full
  - or both players have no legal moves
- after finish, the UI must show:
  - both scores
  - winner or draw result

### 3.6 Program exit
- input `quit` exits the whole program

---

## 4. Recommended Architecture

Keep the design minimal and directly useful for Lab 3.

### 4.1 Platform-level responsibilities
Platform-level code should handle:
- the list of game sessions
- current selected game id
- switching games
- adding new sessions by type
- dispatching user input to the active session
- whole-program quit behavior

### 4.2 Single-session responsibilities
Each game session should own:
- its game type
- its board state
- its current player
- its finished state
- its status / finish message

### 4.3 Mode-specific rule responsibilities
Mode-specific logic should decide:
- whether a move is legal
- what `pass` means
- how turns advance
- how a session ends
- whether scores are shown
- whether legal moves are highlighted

### 4.4 UI responsibilities
UI code should focus on:
- left / center / right layout composition
- board rendering with coordinates
- center info rendering for current game and players
- score display for `reversi`
- game list rendering on the right
- prompt, error, and finish messages

### 4.5 Anti-pattern to avoid
Do not place all `peace` and `reversi` logic inside one giant controller or one giant UI class with large unrelated branches.

Small conditional differences are acceptable, but rule execution should stay readable and mode-local.

---

## 5. Input Handling Rules

Input should be interpreted in this order:

1. `quit`
2. `peace` / `reversi`
3. game number
4. `pass`
5. board coordinate such as `D4`
6. otherwise invalid input

### 5.1 Coordinate parsing
- accept letter + number form such as `D4`
- column maps to `A-H`
- row maps to `1-8`
- parsing should be case-insensitive
- malformed and out-of-range coordinates must show a clear error

### 5.2 Number switching
- entering a valid existing game number switches to that game
- entering an out-of-range number must show a clear error

### 5.3 New game commands
- `peace` and `reversi` create new sessions at the end of the list
- after creation, remain on the current game until the user explicitly switches

### 5.4 Pass validation
- only `reversi` uses `pass`
- `pass` is legal only when the current player has no legal moves
- when the current `reversi` player has no legal move, the turn must not be skipped automatically
- the user must explicitly input `pass` to skip that turn and hand control to the next player
- while waiting for that explicit `pass`, platform-level commands such as switching games, creating a new game, and quitting the program must still remain available
- `pass` on `peace` must be rejected
- if the active `reversi` player still has legal moves, `pass` must be rejected

### 5.5 Quit
- `quit` always exits the whole program

---

## 6. UI / Output Rules

### 6.1 Layout
- left area: board with top and left coordinates
- center area: current game number, player info, current turn
- right area: game list with game id and type
- exact spacing does not need to match the PDF character-by-character

### 6.2 Game list
- always show all current games
- each item must include game number and game type

### 6.3 Reversi-only display
- show legal moves with `+`
- show both players' scores in the center area
- when no legal move exists, show a prompt telling the user to input `pass`

### 6.4 Messages
- keep prompts explicit about valid input categories
- show invalid-input feedback clearly
- show switch confirmation when useful
- show game-add confirmation when useful
- show finish message when a session ends

### 6.5 Finished sessions
- a finished session remains renderable
- switching back to a finished session must still show its result

---

## 7. Acceptance Checklist

Before considering Lab 3 implementation complete, verify:

- project compiles successfully
- startup enters Game 1
- Game 1 is `peace`
- Game 2 is `reversi`
- the right-side game list is correct
- `peace` accepts legal empty-cell moves and reaches board-full finish
- `peace` rejects malformed, out-of-range, and occupied-cell input
- `reversi` shows legal move `+` markers correctly
- `reversi` flips pieces correctly after legal moves
- `reversi` shows scores correctly
- `pass` is accepted only when legal
- `reversi` ends correctly and shows score plus winner / draw
- adding `peace` updates the game list correctly
- adding `reversi` updates the game list correctly
- switching games preserves previous state
- finished games remain viewable after switching
- `quit` exits the program correctly

---

## 8. Local File Scope

Normally allowed to modify for Lab 3:
- `src/main/java/**`
- `pom.xml`
- `SPEC_lab3.md`

Do not modify unless explicitly requested:
- anything in `../lab1/`
- anything in `../lab2/` except for reference reading
- anything in `../_private/`
- generated files under `target/`

---

## 9. Local Development Rule

When working in `lab3/`:
- read root `SPEC.md` first
- read this `SPEC_lab3.md` second
- read `Lab3.pdf` as the active requirement source
- inherit from `lab2/` only where that helps Lab 3 directly
- keep refactoring scope minimal and explainable
- avoid overengineering for hypothetical future labs
