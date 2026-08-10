# CST 338 Project 2 — Grade & Assignment Tracker

**Team members:** Jordan Browning, Brent Brewington, Yoko Mohr, Jit Tran

**Repository:** https://github.com/OtterConLabs/CST338-Project2

---

## Team & Slice Ownership

| Slice | Owner | GitHub username | Issues | Branch(es)                                                                                                                                                       | PR(s)                                           | Enhancement chosen | Status |
|---|---|---|---|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|---|---|
| 1 — Accounts | Yoko Mohr | yokocsumb | #2, #3, #5, #6, #8, #14, #30, #37, #41 | yoko/javafx_app, yoko/SQLite_database_structure, yoko/database-singleton, yoko/login-fxml, yoko/account-fxml-crud, yoko/FXML-dashboard-setup, yoko/test-UserDao,yoko/account-feature | #4, #12, #15, #19, #20, #31, #39, #42, #46, #56 | Notifications / alerts on app events | in-progress |
| 2 — Courses & Enrollment | Brent Brewington | Brewsdawg | #48 | brent/course-dao, brent/course-list-scene, brent/enrollment-scene                                                                                                | #21, #44, #47, #54                              | TableView / ListView populated with live data | in-progress |
| 3 — Assignments | Jordan Browning | jbrowning-otter | #13, #16, #17 | jordan/assignment-dao, jordan/assignment-list-scene, jordan/assignment-form-scene                                                                                | #43, #45, #59                                        | Data Binding using ObservableList | Completed |
| 4 — Grades | Jit Tran | jtcsumb | #24, #25, #26 | jtcsumb/grade-dao, jtcsumb/grade-entry-scene, jtcsumb/grade-statistics                                                                                           |                                                 | Data binding using ObservableList and JavaFX properties | planned |
| 5 — Attendance | Jit Tran | jtcsumb | #27, #28, #29 | jtcsumb/attendance-dao, jtcsumb/attendance-roster, jtcsumb/attendance-report                                                                                     |                                                 | Notifications for overwrite confirmation, saves, and CSV export results | planned |

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

| PR | Author | Human reviewer(s) | AI review / adjudication | Outcome |
| --- | --- | --- | --- | --- |
| #40 | Jordan Browning | Yoko Mohr | [Adjudication link](https://github.com/OtterConLabs/CST338-Project2/pull/40#issuecomment-5226793413) | AI findings adjudicated; approved and merged |
| #50 | Jordan Browning | Jit Tran | [Adjudication link](https://github.com/OtterConLabs/CST338-Project2/pull/50#issuecomment-5227427547) | AI findings adjudicated; rejected |
| #56 | Yoko Mohr | REVIEWER_NAME | https://github.com/OtterConLabs/CST338-Project2/pull/56 | 3 accepted, 1 partially accepted, 1 rejected |
| #54 | Brent Brewington | Jordan Browning | N/A | Changes requested, feedback addressed, then approved/merged |
| #51 | Jit Tran | Jordan Browning | N/A | Reviewed and approved/merged |
| #46 | Yoko Mohr | Jordan Browning | N/A | Reviewed and approved/merged |


---

## AI Usage Log

### Jordan Browning — Assignment Slice

- **AI-drafted tests:** [TESTING.md](TESTING.md)
  - Used ChatGPT to draft a more complete TestFX test for the Assignment slice.
  - The AI proposed one large UI test covering Assignment creation, editing, deletion, Course selection, and TableView verification.
  - I determined that the generated test duplicated DAO coverage and depended on too many parts of the application at once.
  - I curated the AI-generated approach by separating testing responsibilities:
    - `AssignmentDaoTest` tests database and CRUD behavior.
    - `AssignmentTest` tests Assignment model/domain validation.
    - `AssignmentListSceneTest` tests JavaFX scene navigation.
  - I also added an invalid Assignment ID edge case and negative-points domain validation.
  - The complete reflection, original prompt, AI-generated approach, and changes are documented in `TESTING.md`.

- **AI code review / adjudication:**
  - [PR #40 adjudication](https://github.com/OtterConLabs/CST338-Project2/pull/40#issuecomment-5226793413) — AI findings were reviewed and adjudicated before the PR was approved and merged.
  - [PR #50 adjudication](https://github.com/OtterConLabs/CST338-Project2/pull/50#issuecomment-5227427547) — AI findings were reviewed and adjudicated before the PR was finalized.
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
