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
- `lab2/` = Lab 2 development branch in directory form
- future labs should continue as `lab3/`, `lab4/`, etc.

---

## 2. Repository Structure Rules

### Root-level directories
- `lab1/`: completed Lab 1 project, should be treated as archived unless explicitly requested
- `lab2/`: active Lab 2 development project
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
- `lab2/` should evolve independently even if initialized from `lab1/`

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
Lab 2 is the current active development target unless otherwise specified.

Lab 2 must be implemented based on Lab 1 evolution, but should live in `lab2/`.

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

---

## 6. File Modification Policy

### Allowed to modify
Usually safe to modify:
- `lab2/src/main/java/**`
- `lab2/pom.xml`
- `lab2/README.md`
- `lab2/SPEC.md` if it exists

### Modify with caution
- package names
- entry points
- console UI layout
- shared model classes reused from Lab 1

### Do not modify unless explicitly requested
- `lab1/**`
- `_private/**`
- generated build outputs
- submission archives
- report PDFs

---

## 7. Recommended Architecture Direction

For later labs, prefer separation like:
- `Board` = single board state and rule logic
- `Position` = coordinates
- `Cell` / `Piece` = piece representation
- `GameSession` = one independent board game state
- `GameManager` / `MultiGameController` = manages multiple sessions
- `ConsoleUI` = Lanterna rendering and input/output
- `Main` = startup and wiring

Do not hardcode board1/board2/board3 as separate logic branches.
Prefer list-based or collection-based management for extensibility.

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
- board switching preserves state
- illegal moves are rejected correctly
- legal moves flip all required pieces
- pass logic works correctly
- end-of-game behavior matches the PDF

---

## 10. Reporting Awareness

Code structure should support later lab report writing.
Changes should make it easy to explain:
- what was inherited from previous lab
- what was newly added
- what was refactored for multi-board support
- what design tradeoffs were made

---

## 11. Default Working Rule for Codex CLI

Unless the user explicitly says otherwise:
- treat `lab2/` as the active working directory
- read this `SPEC.md` first
- preserve `lab1/`
- do not touch `_private/`
- make incremental, reviewable changes
- align all implementations with the active lab PDF
