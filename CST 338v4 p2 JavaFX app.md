# JavaFX Application (Project 2)

You will work on a team of **3–4 students** to build a JavaFX desktop application — but you
are graded as an **individual**. Each of you owns one complete **feature slice** of the app
end-to-end, and your grade is driven mostly by *how you work*: your GitHub contributions,
your code reviews, and your teamwork, with concrete code milestones along the way.

This project is **5 assignments** (Part 0 through Part 4) that together total **100 points**
in a weighted Canvas category. The final assignment (Part 4) is worth **50 points** — half
the project — because that is where your finished work comes together.

---

## How Grading Works

There is **no menu, no points-target, and no penalty math**. Each of the 5 assignments is
graded **per student** using its own rubric. Your score reflects your own slice and your own
process — a teammate's shortfall does not lower your grade, and you cannot inherit their
points.

### Points per assignment

| Assignment | Focus | Points |
| :---- | :---- | :----: |
| **Part 0 — Team & Slices** | planning / team building | 10 |
| **Part 1 — The Plan** | design + issues + scope | 10 |
| **Part 2 — Data & Scenes** | Milestone 1 | 15 |
| **Part 3 — Reviews** | code review | 15 |
| **Part 4 — Final Application** | Milestone 2 + integration | 50 |
| | **Total** | **100** |

### What the 100 points measure

Across the whole project, your points come from five areas:

| Area | Points | What it measures |
| :---- | :----: | :---- |
| **Feature slice** | 30 | Your scenes work, CRUD runs through your DAO, your logic is correct, ≥1 enhancement applied |
| **Testing** | 15 | Your unit + UI tests, plus AI-drafted-then-curated tests with a reflection |
| **GitHub contributions** | 20 | `name/feature` branches, 15+ commits, 3+ issues linked to merged PRs |
| **Code review** | 20 | Substantive reviews of teammates' PRs + one AI code review you adjudicate + one cross-team review |
| **Teamwork / integration** | 15 | The assembled app builds & runs; scene factory wires all slices; you keep the README + issues current |

These areas are spread across the 5 assignments as follows (this table drives each rubric):

| Area ↓ / Assignment → | P0 | P1 | P2 | P3 | P4 | **Total** |
| :---- | :--: | :--: | :--: | :--: | :--: | :--: |
| Feature slice | — | 4 | 6 | — | 20 | **30** |
| Testing | — | 2 | 3 | — | 10 | **15** |
| GitHub contributions | 5 | 4 | 3 | — | 8 | **20** |
| Code review | — | — | 3 | 12 | 5 | **20** |
| Teamwork / integration | 5 | — | — | 3 | 7 | **15** |
| **Assignment total** | **10** | **10** | **15** | **15** | **50** | **100** |

### Extra credit (up to +10)

You may earn **up to 10 extra-credit points** (10% of the project) for going **beyond your
declared scope** — building an extra slice, completing an item from your **WILL NOT DO**
list, adding extra enhancements, or extra test coverage / an additional documented AI
experiment. Extra credit is credited to the individual who did the work and is applied on
Part 4.

---

## The Vertical Feature Slice

Every student owns **one feature, built top to bottom** — not a horizontal layer ("the UI
person", "the database person"). A *slice* is a complete deliverable that can be graded on
its own:

| Your slice includes | Requirement |
| :---- | :---- |
| **Database** | 1 table (plus its relationships) added to the shared schema |
| **Data access** | 1 DAO with full CRUD **and** at least one query/filter method |
| **UI** | 1–2 FXML scenes with controllers |
| **Logic** | 1+ domain/service class holding the feature's rules |
| **Tests** | unit tests (DAO CRUD + logic) **and** ≥1 TestFX UI test |
| **Enhancement** | at least 1 chosen enhancement (see the menu) applied to this slice |
| **Design artifacts** | 2 use cases (main + alternate flow), the ERD fragment for your table, and a mockup of your scene |
| **GitHub trail** | 3+ SMART issues, `name/feature` branches, reviewed + merged PRs |

**Slice 1 is always "Accounts"** — login, registration, and the `users` table. Everyone
else's slice builds on it, so it is always built and owned by one student.

### Choosing your scope — 5 candidate slices

Each example app offers **5 candidate slices**, but a team of 3–4 only *builds one per
member*:

- **4 students** → build 4 of the 5 (Accounts required); **1 slice is cut**.
- **3 students** → build 3 of the 5 (Accounts required); **2 slices are cut**.

Whatever you cut goes on your team's **WILL NOT DO list**. You may build an extra slice for
**extra credit** (see above).

### Beyond-scope items — how deep is your slice?

Each slice also lists **beyond-scope** stretch items. The depth of your slice is a dial, not
a switch: decide how far to build, and put what you skip on the WILL NOT DO list. Completing
a beyond-scope item is eligible for extra credit.

### The WILL NOT DO list (required)

Your team publishes a **WILL NOT DO list** in the repo README during planning and keeps it
current. It names the slices you are **not** building and the beyond-scope items you are
**not** pursuing. This turns "we ran out of time" into a deliberate, documented engineering
decision — a real software-design skill — and it becomes your menu of extra-credit
opportunities.

---

## Example Slice Breakdowns

Adopt one of these directly, or model your own on it. Format: **core** (what the slice
minimally is) · *beyond-scope* (candidates to cut or pursue for extra credit). All four apps
start with **Slice 1 Accounts** (`users` table with `userId`, `username`, `password`, `role`).

### Trivia / Study Guide
1. **Accounts** *(required)* — `users`; login/register. *Beyond:* password reset, roles.
2. **Question Bank** — `questions`, `categories`; admin CRUD. *Beyond:* CSV/REST import, difficulty levels, media questions.
3. **Quiz Engine** — `attempts`; quiz + result scenes; scoring. *Beyond:* timed quizzes, randomized order, hints.
4. **Leaderboard & History** — personal history + global leaderboard; aggregation. *Beyond:* per-category boards, streaks, badges.
5. **Study Mode & Tagging** — flashcard/study mode; tag/favorite questions. *Beyond:* spaced repetition, study-set sharing.

### E-Commerce
1. **Accounts** *(required)* — `users` (+ role). *Beyond:* addresses, extra roles.
2. **Catalog** — `items`; browse/search + admin add/remove. *Beyond:* categories/filters, image handling, pagination.
3. **Cart & Checkout** — `carts`, `orders`; checkout logic. *Beyond:* coupons, tax/shipping, payment-API stub.
4. **Order History & Management** — user history + admin all-orders. *Beyond:* order-status workflow, refunds, CSV export.
5. **Reviews & Ratings** — product reviews/ratings; average display. *Beyond:* verified-buyer badges, moderation, helpfulness voting.

### Grade / Assignment Tracker
1. **Accounts** *(required)* — `users` (+ role: student/teacher). *Beyond:* admin role, password reset.
2. **Courses & Enrollment** — `courses`; assign teachers/enroll. *Beyond:* sections, waitlists, capacity limits.
3. **Assignments** — `assignments`; create/edit/list. *Beyond:* due-date reminders, attachments, weighted categories.
4. **Grades & Statistics** — `grades`; entry + class stats. *Beyond:* weighted categories, GPA calc, distribution charts.
5. **Attendance & Reports** — attendance records; report view. *Beyond:* CSV export, calendar view, email notifications.

### Monster Arena (legally distinct)
1. **Accounts** *(required)* — `users`. *Beyond:* profiles, roles.
2. **Creature Roster** — `creatures`; collect/train. *Beyond:* leveling curves, stats growth, evolution.
3. **Battle Engine** — `battles`; battle scene + combat logic. *Beyond:* type-effectiveness matrix, status effects, animations.
4. **Admin & Arenas** — `arenas`; creature/arena admin CRUD. *Beyond:* tournaments, seasonal arenas.
5. **Marketplace & Trading** — trade/buy creatures; currency/inventory. *Beyond:* auctions, trade history, REST import.

---

## The Enhancement Menu

Your slice is the required floor. On top of it, **choose at least one enhancement** to apply
to *your* feature — *which* one is up to you. Additional enhancements earn extra credit.

- TableView / ListView populated with live data
- Data binding (`ObservableList` / JavaFX `Property`)
- Notifications / alerts (custom dialog, desktop, or tray) on app events
- Custom reusable FXML component (`fx:include`) used in ≥2 scenes
- External library (Jackson, Gson, OkHttp, …) used meaningfully
- Consume an external REST API
- Supabase backend (local persistence still required)
- Extra TestFX scene tests
- Advanced query / reporting feature

---

## Testing

Testing is a graded area in its own right (15 pts).

- **Unit tests** — cover your DAO (insert / read / update / delete) against an in-memory H2
  database and your domain/service logic, including at least one **negative or edge case**.
- **UI test** — use **TestFX** to verify at least one scene transition in your slice.
- **AI-assisted tests** — see below. Curate them; do not paste them.

Your tests must **run on screen** in the Part 4 video.

---

## Using AI (required and documented)

AI use in this project is **required and transparent**. You are graded on your **judgment** —
how you curate and adjudicate AI output — not on the raw output itself.

### AI-drafted, then curated, tests
Use an AI tool to *draft* tests for your slice, then make them yours. In a `TESTING.md`
section (or your design-doc subsection) record:
1. The **prompt(s)** you gave the AI.
2. What the AI **produced** (snippet or commit link).
3. Your **evaluation** — which tests were valid, which were trivial, wrong, or hallucinated.
4. **What you changed and why**, ending in a curated suite that compiles and passes.

You score well by catching and fixing the AI's mistakes — even if its first draft was poor.

### AI as a second code reviewer (in addition to a human)
For at least one of your own PRs, run an **AI code review** *in addition to* the required
human teammate review, and record in the PR (linked from the README):
1. The **tool** used and which PR.
2. The AI's **findings**.
3. Your **adjudication** — each finding accepted (with the fixing commit) or rejected (with
   your reasoning).

The human reviewer catches intent and design; the AI catches mechanical and coverage gaps;
**you** decide. That decision is what's graded.

> **Integrity:** AI use here must be disclosed and documented. Undocumented AI use elsewhere
> still follows course policy.

---

## Code Review

- **Intra-team (primary):** every merge to `main` requires a teammate's PR review. Each
  student submits **≥3 substantive reviews** across the project — line-specific comments, at
  least one "request changes"; a bare "LGTM" does not count.
- **AI reviewer (required):** at least one documented, adjudicated AI review of your own PR.
- **Cross-team (Part 3):** one review of another team's running app, covering code quality,
  GitHub process, and design, citing specific files/commits.

---

## GitHub & Project Tracking

### Repository
One student creates a **public** GitHub repository (JavaFX project, IntelliJ + Gradle or
Maven), commits a `.gitignore` (use [gitignore.io](https://gitignore.io)), and invites all
teammates as collaborators. Everyone clones it.

### Branches & Pull Requests
- All branches follow `name/feature-name` (e.g., `alice/login-scene`, `bob/user-dao`). This
  is how your individual contributions are verified — other patterns will not count.
- Every merge to `main` goes through a Pull Request that references its issue with
  **`Closes #N`** and is **reviewed by a teammate** before merging.

### GitHub Issues are your live tracker
Every slice task, enhancement, and scope decision is a **GitHub Issue** — assigned to its
owner, labeled (e.g., `slice-1`, `testing`, `enhancement`, `will-not-do`, `extra-credit`),
and closed by a PR via `Closes #N`. Write **SMART** issues.

> **Good:** "Create `UserDao` and `User` entity with insert/update/delete/query-by-username;
> add JUnit tests using in-memory H2. Due end of Sprint 1."
> **Bad:** "Make a database table."

### The repo README is your dashboard
Copy the provided **`README.md` template** into your repo root and keep it current. It is the
first thing the instructor reads when grading, and a working README is part of your
integration score. It records: app concept, **slice-ownership table**, **WILL NOT DO list**,
links to Issues and PRs with their reviewers, enhancement chosen, AI-review links, the
extra-credit log, and build/run instructions.

---

## The Five Assignments

Each part is its own Canvas submission with its own rubric.

| Part | Points | What you do |
| :---- | :----: | :---- |
| **0 — Team & Slices** | 10 | Form the team; create the public repo + README; pick the app and assign one slice per member; draft the WILL NOT DO list. |
| **1 — The Plan** | 10 | Each student writes their own slice spec (2 use cases + ERD fragment + mockup); open 3+ SMART issues; choose an enhancement; write a test plan. |
| **2 — Data & Scenes** | 15 | **Milestone 1:** merge your slice skeleton (table + DAO CRUD + one scene); write your first passing test; give your first teammate PR review. |
| **3 — Reviews** | 15 | Accumulate substantive teammate PR reviews; run and adjudicate one AI code review; complete one cross-team app review; keep a working build. |
| **4 — Final Application** | 50 | **Milestone 2:** complete your slice + enhancement; finish the test suite + AI-curated tests + reflection; integrate the app; record an ≤8-minute video where each student narrates their own slice. |

---

## Helpful Links

- [JavaFX Documentation](https://openjfx.io/openjfx-docs/)
- [IntelliJ IDEA: JavaFX setup](https://www.jetbrains.com/help/idea/javafx.html)
- [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc)
- [H2 Database Engine](https://h2database.com)
- [TestFX (JavaFX UI Testing)](https://github.com/TestFX/TestFX)
- [gitignore.io](https://gitignore.io)
- [Writing SMART goals/issues](https://www.atlassian.com/blog/productivity/how-to-write-smart-goals)

---

## Version History

### Jul 2026 V2.0.0
- **Individual-first redesign.** Replaced the open menu + per-student points-target/penalty
  system with **vertical feature slices** (one per student, graded independently).
- Grade re-weighted toward process: **100 pts across 5 assignments** (10/10/15/15/50, Part 4
  worth half), measuring slice (30) / testing (15) / GitHub (20) / code review (20) /
  teamwork (15).
- Added **5 candidate slices per app** with a required **WILL NOT DO** scope list and
  beyond-scope stretch items; teams choose what to build.
- **Testing featured** as its own graded area.
- **AI use required and documented** — AI-drafted-then-curated tests + AI code review you
  adjudicate, in addition to human review.
- **Up to +10 extra credit** for work beyond declared scope.
- Retired the project spreadsheet in favor of **GitHub Issues (live tracker) + a repo README
  dashboard**.

### Mar 2026 V1.0.0
- Created JavaFX version based on the Android Application prompt (Android/Room → JavaFX/
  SQLite-H2; Activities/Intents → FXML Scenes/Scene Factory). *(See `__archive/` for the
  V1 menu/claiming version.)*
