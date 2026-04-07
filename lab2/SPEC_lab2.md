# Lab2 Specification

## 1. Scope

This directory contains the Lab 2 implementation for the OOP lab project.

Lab 2 is an evolution of Lab 1, not a brand new unrelated project.
The implementation may reuse Lab 1 structure and code, but must satisfy the new Lab 2 requirements.

Primary source of truth:
- `lab2.pdf` in this directory, or the currently provided Lab 2 PDF
- If a detail conflicts with older Lab 1 behavior, prefer Lab 2 wording
- Lab 1 is background and inheritance context, not the final authority for Lab 2 behavior

---

## 2. Lab 2 Required Features

Lab 2 must implement two major upgrades:

### 2.1 Full Othello / Reversi rules
The game must now support full move legality and piece flipping.

Required behavior:
- legal moves must follow Othello rules
- a move is only legal if it flips at least one opponent piece
- after a legal move, all flanked opponent pieces in all valid directions must be flipped
- illegal moves must show a user-facing prompt/message
- if the current player has no legal move, that player must pass
- if the current player has at least one legal move, they must move and may not voluntarily pass

### 2.2 Multiple concurrent boards
The program must support multiple game boards running at the same time.

Required behavior:
- before the game begins, initialize multiple boards
- for this lab, the board count is fixed to 3
- the code structure should still be extensible for future dynamic add/remove behavior
- when the program starts, the default selected board is board 1
- during the game, user input may represent either:
  - a move position (example: `A1`)
  - a board number (example: `2`) for switching boards
  - `quit`
- switching boards must preserve each board’s previous state

---

## 3. End Conditions

There are two levels of ending behavior.

### 3.1 Whole program exit
The whole program exits when:
- the user inputs `quit`

### 3.2 Single-board finish
A single board is finished when:
- the board is full
- or both players have no legal moves on that board

When one board finishes:
- show a prompt/message for that board
- do not terminate the whole program automatically unless explicitly designed and justified
- other boards should still remain available

---

## 4. Implementation Direction

Lab 2 should be developed inside `lab2/`.
Do not continue main development in `lab1/`.

Lab 2 may be initialized from Lab 1 code, but the Lab 2 directory should become an independent working project.

Preferred design direction:

- `Board`
  - owns a single board state
  - handles move legality
  - computes flippable pieces
  - applies moves
  - flips pieces
  - counts black/white pieces
  - checks whether a player has legal moves
  - checks whether the board is full

- `Position`
  - stores board coordinates

- `Cell` / `Piece`
  - represents board cell status or piece color

- `GameSession`
  - represents one independent board game
  - stores board-specific current player
  - stores board-specific finished state
  - may store board-specific status messages

- `GameManager` / `MultiGameController`
  - manages all board sessions
  - stores current selected board index
  - handles switching boards
  - dispatches move input to the active board
  - coordinates quit / board-switch / move logic

- `ConsoleUI`
  - renders the current selected board
  - shows current board number
  - shows current player
  - shows black/white counts
  - shows prompts and error messages

- `Main`
  - startup and wiring

---

## 5. Extensibility Rule

Although this lab uses exactly 3 boards, the implementation must not hardcode three separate logic branches like:
- `board1`, `board2`, `board3`
- duplicated board-specific code blocks
- duplicated switch cases with different state variables

Preferred approach:
- use a collection such as `List<GameSession>`
- track current board by index or id
- make session creation logic reusable

Reason:
future labs may require dynamic board addition/removal.

---

## 6. Input Handling Rules

Each interaction should interpret user input as one of the following:

### 6.1 Move input
Examples:
- `A1`
- `D4`

Expected behavior:
- parse to a board position
- validate against Othello rules on the current selected board
- reject illegal moves with a clear message
- apply legal move and flip all required pieces

### 6.2 Board-switch input
Examples:
- `1`
- `2`
- `3`

Expected behavior:
- switch current selected board
- keep that board’s previous state intact
- show a status message confirming the switch if useful

### 6.3 Quit input
Example:
- `quit`

Expected behavior:
- terminate the whole program

---

## 7. UI Expectations

The program should remain a console-based Lanterna application unless explicitly changed later.

The current UI should clearly display:
- the active board
- the current player for that board
- black piece count
- white piece count
- move / switch / quit prompt
- feedback for invalid moves
- feedback for switching boards
- feedback when a board has finished

The interface should align with the Lab 2 examples that show:
- `Current Board: ...`
- `Current Player: ...`
- `Black: ... White: ...`
- prompt for move, board number, or quit

---

## 8. Behavioral Rules That Must Not Be Violated

Do not keep Lab 1 simplified move logic.
For Lab 2:
- empty-cell-only placement is not sufficient
- flipping is mandatory
- pass logic is mandatory
- both-players-no-legal-move ending is mandatory

Do not use a single global current player for all boards.
Each board/session must preserve its own turn state.

Do not end the whole program just because one board is finished.

Do not overwrite or modify `lab1/` unless the user explicitly requests it.

---

## 9. File Modification Rules

Usually allowed to modify:
- `src/main/java/**`
- `pom.xml`
- `README.md`
- this `SPEC.md`

Do not modify unless explicitly requested:
- anything in `../lab1/`
- anything in `../_private/`
- generated files under `target/`

Do not edit compiled outputs.
Only edit source and configuration files.

---

## 10. Development Strategy

Preferred order of work:

### Phase 1
Upgrade single-board logic to full Othello rules:
- legal move detection
- flipping
- pass logic
- full single-board end detection

### Phase 2
Introduce multi-board/session abstraction:
- multiple sessions
- active board index
- board switching
- per-board independent state

### Phase 3
Update UI and prompts:
- show active board
- show counts and current player
- support move / board number / quit input
- show single-board finished messages

### Phase 4
Polish and verify:
- compile with Maven
- run through multi-board switching scenarios
- verify state preservation
- verify illegal/legal move behavior
- verify end-of-board behavior

Do not attempt a giant rewrite unless necessary.
Prefer controlled, reviewable steps.

---

## 11. Verification Checklist

Before considering a Lab 2 implementation step complete, verify:

- Maven build succeeds
- Lanterna console flow still works
- legal move detection follows Othello rules
- legal moves flip all required pieces
- illegal moves are rejected with a message
- pass behavior works when no legal move exists
- board switching works
- switching boards preserves previous board state
- each board keeps its own current player and progress
- single-board completion is detected correctly
- `quit` exits the whole program

---

## 12. Report Awareness

Code changes should support later report writing.

The report for Lab 2 should be able to explain:
- what was inherited from Lab 1
- what was added for full Othello rules
- what was refactored for multi-board support
- how the code structure changed after supporting multiple boards
- incremental design changes, especially around session/manager structure

Keep the implementation understandable enough that it can be described in a report or UML diagram.

---

## 13. Default Rule for AI Coding in This Directory

When working in `lab2/`, unless the user explicitly says otherwise:

- read root `SPEC.md` first
- read this local `SPEC.md`
- treat Lab 2 PDF requirements as authoritative
- modify only files inside `lab2/`
- preserve `lab1/` as archived reference
- do not touch `_private/`
- prefer small, explainable changes
- do not invent extra features beyond the lab without user approval
