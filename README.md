# CST 338 Project 2 — Grade & Assignment Tracker

**Team members:** Jordan Browning, Brent Brewington, Yoko Mohr, Jit Tran

**Repository:** https://github.com/OtterConLabs/CST338-Project2

---

## Team & Slice Ownership

| Slice | Owner | GitHub username | Issues | Branch(es) | PR(s) | Enhancement chosen | Status |
|---|---|---|---|---|---|---|---|
| 1 — Accounts | Yoko Mohr | yokocsumb | #2, #3, #5, #6, #8, #14, #30, #37, #41 | yoko/javafx_app, yoko/SQLite_database_structure, yoko/database-singleton, yoko/login-fxml, yoko/account-fxml-crud, yoko/FXML-dashboard-setup, yoko/test-UserDao | #4, #12, #15, #19, #20, #31, #39, #42 | Notifications / alerts on app events | in-progress |
| 2 — Courses & Enrollment | Brent Brewington | Brewsdawg | #48 | brent/course-dao, brent/course-list-scene, brent/enrollment-scene | #21, #44, #47, #54 | TableView / ListView populated with live data | in-progress |
| 3 — Assignments | Jordan Browning | jbrowning-otter | #13, #16, #17 | jordan/assignment-dao, jordan/assignment-list-scene, jordan/assignment-form-scene | #43, #45 | Data Binding using ObservableList | in-progress |
| 4 — Grades | Jit Tran | jtcsumb | #24, #25, #26 | jtcsumb/grade-dao, jtcsumb/grade-entry-scene, jtcsumb/grade-statistics | | Data binding using ObservableList and JavaFX properties | planned |
| 5 — Attendance | Jit Tran | jtcsumb | #27, #28, #29 | jtcsumb/attendance-dao, jtcsumb/attendance-roster, jtcsumb/attendance-report | | Notifications for overwrite confirmation, saves, and CSV export results | planned |

*Status values: planned · in-progress · complete*

### Slice 2 — Courses & Enrollment (detail)

- **Issue:** #48
- **Pull requests:** #21, #44, #47, #54
- **Branches:** brent/course-dao, brent/course-list-scene, brent/enrollment-scene
- **Enhancement:** TableView / ListView populated with live data
- **Status:** in-progress

Work is assigned to Brewsdawg, labeled (slice-2, testing, enhancement as appropriate), and closed by a PR via `Closes #N`.

---

## WILL NOT DO (declared scope cuts)

| Slice | Features (Will Not Implement) |
|---|---|
| 1 — Accounts | Admin role, Password reset |
| 2 — Courses & Enrollment | Course sections, Enrollment waitlists, Course capacity limits |
| 3 — Assignments | Due-date reminders, File attachments, Weighted assignment categories |
| 4 — Grades & Statistics | Weighted-category grade calculations, GPA calculation |
| 5 — Attendance & Reports | Calendar view, Email notifications |

---

## Code Review Log
| PR  | Author | Human reviewer(s) | AI review (link) | Outcome |
|-----|--------|-------------------|------------------|---------|
| #40 |Jordan Browning | Yoko Mohr         | N/A              | Approved and merged |
| #43 |Jordan Browning | Yoko Mohr         | N/A              | Approved and merged |
| #45 |Jordan Browning | Yoko Mohr         | N/A              | Approved and merged |

---

## AI Usage Log

- **AI-drafted tests:** \<link to TESTING.md / commit\> — per owner.
- **AI code reviews:** \<PR link + adjudication note\> — per owner.

---

## Extra Credit Log

| Item | Who | Evidence (Issue/PR) |
|---|---|---|
| Built Slice 5 | Jit Tran | #27, #28, #29 |

---

## Build & Run

```bash
./gradlew run        # launch the app
./gradlew test       # run the test suite
```

**Requirements:** JDK \<version\>, JavaFX \<version\>. Any setup notes go here.
