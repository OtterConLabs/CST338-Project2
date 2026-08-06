# Part 04 Video — Brent's segment (Slice 2: Courses & Enrollment)

**Target: 2 minutes.** The team video is capped at 8 minutes total for four people, so budget roughly 2 minutes each and leave a little room for the intro and handoffs.

Before recording: run the app once and delete `app.db` so you start clean, then re-register your demo accounts. A first-run demo is far more convincing than one with mystery leftover data.

---

## Setup (do this before you hit record)

Have ready in separate windows:
1. The running app, sitting on the Dashboard, logged in as a TEACHER
2. A terminal in the project root
3. IntelliJ with `CourseValidator.java` and `CourseSceneUiTest.java` open in tabs

Pre-seed: one TEACHER account (Morgan Larkin) and two STUDENT accounts (Ava Sinclair, Marcus Bellamy). Do **not** pre-seed courses; you want to create one live.

---

## Script

### 0:00–0:15 — What the slice is

> "I own the Courses and Enrollment slice. It adds two tables, `courses` and an `enrollment` junction table, a DAO with full CRUD, and three scenes. The enhancement I applied is live data binding, so every list on screen is backed by an ObservableList that reflects what is actually in the database."

*Click "Courses & Enrollment" from the Dashboard.*

### 0:15–0:50 — Create, read, update

> "The table is empty on a fresh database, so let me add a course."

*Add → fill in CST338 / Software Design → pick Morgan Larkin → Save.*

> "The row appears immediately because the TableView is bound to an ObservableList that reloads from the database after every save. The Teacher column comes from a LEFT JOIN in the DAO, not from a second query."

*Select the row → Edit → change the name → Save.*

> "That is the same scene handling both add and edit. If the factory hands it a selected course it updates, and if it does not, it inserts."

**Now show a rule failing, deliberately:**

*Add → enter `cst338` again → Save.*

> "The course code is unique and case-insensitive, so the database rejects the duplicate and the form reports it inline instead of crashing. That is one of my negative test cases."

### 0:50–1:20 — Enrollment and the domain rule

*Select CST338 → Manage Enrollment.*

> "Available students on the left, enrolled on the right. Both are ObservableList-backed."

*Select Ava → Enroll.*

> "She moves across, and notice she is gone from the available list. That is not a UI trick. The available query excludes anyone already enrolled with a NOT IN subquery, so a student can never be enrolled twice. That rule was the alternate flow in my use case."

*Select Ava on the right → Unenroll → she reappears on the left.*

### 1:20–1:40 — Delete and the cascade

*Enroll both students, go Back, select the course, click Delete.*

> "Deleting a course also deletes its enrollment rows through ON DELETE CASCADE, so I confirm first."

*Confirm.*

> "Worth noting that SQLite leaves foreign keys off by default, so I run `PRAGMA foreign_keys = ON` when the schema is created. Without it the cascade silently does nothing, and that is a bug that would not show up until data went missing."

### 1:40–2:00 — Tests

*Switch to the terminal.*

```bash
./gradlew test
```

> "Twenty-nine tests. Eleven cover CourseDao CRUD, six cover the enrollment junction table, six cover the validation logic with no database at all, and six are TestFX tests that drive the Enrollment scene."

*While it runs, switch to `CourseSceneUiTest.java`.*

> "This one clicks Enroll and then asserts the student is actually persisted, not just added to the list. My AI-drafted version only checked the list size, which would have passed even if the write never happened. That fix and three others are documented in TESTING.md."

*Show the green result.*

---

## If you have spare time in the team's budget

Add 15 seconds on the H2-to-SQLite decision. It is the strongest judgment call in your slice and it maps directly to the testing rubric:

> "My plan said H2 for the tests. I switched to in-memory SQLite because the schema uses AUTOINCREMENT and COLLATE NOCASE, which H2 does not support. On H2 I would have been testing a rewritten schema instead of the one the app runs, and my duplicate-code test would have passed for the wrong reason."

---

## Recording notes

- **Let the test run finish on camera.** Cutting away from a test run reads as hiding something.
- **Do not narrate what is obviously on screen.** Say *why*, not *what*.
- **Say "I" for your own work and name teammates for theirs.** Each student is graded on their own slice, and the rubric expects you to speak to yours specifically.
- If something breaks live, say what you expected and move on. Do not restart the recording over a small stumble; a recovered mistake looks more competent than a flawless take.
