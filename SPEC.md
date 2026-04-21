# OOPLab Project Specification

## 1. Project Overview

This repository is used for a semester-long Java OOP lab project series based on multiple lab PDFs.

Current workflow:
- ChatGPT web (GPT-5.4) is used for requirement analysis, task decomposition, review, and optimization planning.
- Codex CLI is used for concrete code modification and implementation.
- All implementation must strictly follow the corresponding lab PDF requirements.
- Do not invent features beyond the lab unless they are clearly small, justified, and safe extensions.

The repository is organized by lab stages:
- `lab1/` = completed Lab 1 snapshot
- `lab2/` = Lab 2 snapshot / reference implementation
- `lab3/` = active Lab 3 development project
- future labs should continue as `lab4/`, `lab5/`, etc.

---

## 2. Repository Structure Rules

### Root-level directories
- `lab1/`: completed Lab 1 project, should be treated as archived unless explicitly requested
- `lab2/`: previous-stage Lab 2 snapshot and reference implementation
- `lab3/`: active Lab 3 development project
- `_private/`: personal files, logs, reports, submission archives; must never be modified or included in code changes
- `README.md`: repository overview
- `SPEC.md`: global project specification

### Private directory rules
The `_private/` directory is not part of the development target.
Never modify, move, delete, or rely on files under `_private/` unless the user explicitly asks.

### Build artifacts
Directories like `target/` are build outputs.
Do not edit generated files.
Only modify source files and configuration files.

---

## 3. Development Principles

### 3.1 PDF-first rule
Each lab must strictly follow its corresponding PDF.
When there is ambiguity:
1. Prefer the explicit wording in the current lab PDF
2. Use previous lab PDFs only as background or inheritance context
3. Do not assume extra requirements without evidence

### 3.2 Incremental evolution rule
Later labs may evolve from earlier labs, but earlier lab directories should remain preserved as historical snapshots.
For example:
- `lab1/` should remain a stable completed version
- `lab2/` should remain preserved once `lab3/` becomes the active target
- `lab3/` may evolve from `lab2/`, but should live independently in `lab3/`

### 3.3 Minimal safe changes
When modifying code:
- avoid unnecessary rewrites
- avoid changing package structure unless needed
- avoid renaming many files unless there is clear architectural value
- prefer small, reviewable steps

### 3.4 Explainable structure
The code should remain understandable for report writing.
Prefer clear class responsibilities over clever but opaque logic.

---

## 4. AI Collaboration Rules

### 4.1 Role split
ChatGPT web is mainly used for:
- requirement analysis
- architecture suggestions
- task decomposition
- review and debugging strategy
- report support

Codex CLI is mainly used for:
- source code edits
- file creation
- refactoring
- dependency/config changes
- implementation details

### 4.2 Before coding
Before making major changes, always identify:
- which lab is active
- which PDF governs the task
- whether the work is incremental or a rewrite
- which files are allowed to be changed

### 4.3 After coding
After each meaningful implementation step:
- summarize what changed
- explain which requirement it satisfies
- note any assumptions or remaining gaps

---

## 5. Current Lab Policy

## Lab 1
Lab 1 is considered completed and archived.
Its directory may be used for reference, but should not be modified unless explicitly requested.

Lab 1 core characteristics:
- simplified Othello / Reversi
- no full flip rule required
- no winner determination required
- Maven project
- Lanterna console UI

## Lab 2
Lab 2 is now a previous-stage snapshot and reference implementation.

Lab 2 remains useful as inheritance context for Lab 3, but it is no longer the default active development target.

Lab 2 core requirements include:
- full Othello/Reversi move legality
- piece flipping after legal moves
- pass logic when a player has no legal move
- end conditions including:
  - `quit`
  - full board
  - both players having no legal move
- support for multiple concurrent boards
- current lab uses fixed 3 boards, but implementation should remain extensible
- switching boards must preserve each board’s previous state

## Lab 3
Lab 3 is the current active development target unless otherwise specified.

Lab 3 must be implemented inside `lab3/` as an evolution of Lab 2's multi-game structure, while preserving `lab2/` as a historical reference.

Lab 3 core requirements include:
- treat the original Lab 1 / Lab 2 mode as `peace`
- introduce a new `reversi` mode with full Reversi rules
- initialize exactly 2 games at startup:
  - Game 1 = `peace`
  - Game 2 = `reversi`
- enter Game 1 by default on startup
- preserve the Lab 2 multi-session logic so switching games keeps each session's state
- support dynamically adding new games by inputting `peace` or `reversi`
- show a game list in the UI, including game number and game type
- keep a left / center / right relative layout:
  - left = board with coordinates
  - center = current game info, player info, current turn
  - right = game list
- for `reversi`, also support:
  - showing all legal moves for the current player using `+`
  - showing both players' scores
  - explicitly prompting the user when no legal move exists
  - allowing `pass` only when the current player truly has no legal move
  - ending when the board is full or both players have no legal moves
  - showing the final score and winner / draw result after game end
- keep the structure reasonably extensible for future new game types, but avoid overengineering beyond Lab 3 needs

---

## 6. File Modification Policy

### Allowed to modify
Usually safe to modify:
- `lab3/src/main/java/**`
- `lab3/pom.xml`
- `lab3/README.md`
- `lab3/SPEC_lab3.md`

### Modify with caution
- package names
- entry points
- console UI layout
- shared model classes reused from Lab 1

### Do not modify unless explicitly requested
- `lab1/**`
- `lab2/**` except when explicitly requested or when making clearly justified minimal reference checks
- `_private/**`
- generated build outputs
- submission archives
- report PDFs

---

## 7. Recommended Architecture Direction

For later labs, prefer separation like:
- `Board` = board state and basic board operations
- `Position` = coordinates
- `Cell` / `Piece` = piece representation
- `GameType` = identifies the game mode for a session
- `GameSession` = one independent game state
- mode-specific rule classes / handlers = encapsulate `peace` / `reversi` rule differences
- `GameManager` / `MultiGameController` = manages multiple sessions
- `ConsoleUI` = Lanterna rendering and input/output
- `Main` = startup and wiring

Do not hardcode game1/game2/game3 as separate logic branches.
Prefer list-based or collection-based management for extensibility.
Keep the architecture simple and directly tied to the active lab requirements.

---

## 8. Coding Style Guidelines

- Keep class responsibilities focused
- Prefer readable names
- Avoid giant god classes
- Avoid duplicating similar logic across multiple boards
- Validate user input clearly
- Surface user-facing messages for invalid moves and board switching
- Preserve existing working behavior unless intentionally replacing it

---

## 9. Testing / Verification Expectations

When implementing features, verify:
- project compiles with Maven
- no accidental edits to archived lab directories
- console interaction still works
- game switching preserves state
- `peace` input validation matches the PDF
- `reversi` legal move highlighting and flipping work correctly
- `pass` logic works correctly
- dynamic game addition works correctly
- end-of-game behavior matches the active lab PDF

---

## 10. Reporting Awareness

Code structure should support later lab report writing.
Changes should make it easy to explain:
- what was inherited from previous lab
- what was newly added for the current lab
- what was refactored for multi-game / multi-mode support
- what design tradeoffs were made

---

## 11. Default Working Rule for Codex CLI

Unless the user explicitly says otherwise:
- treat `lab3/` as the active working directory
- read this `SPEC.md` first, then `lab3/SPEC_lab3.md`, then `Lab3.pdf`
- preserve `lab1/`
- treat `lab2/` as a previous-stage reference rather than the main modification target
- do not touch `_private/`
- make incremental, reviewable changes
- align all implementations with the active lab PDF
