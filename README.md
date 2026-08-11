# CST 338 Project 2 — Grade & Assignment Tracker

**Team members:** Jordan Browning, Brent Brewington, Yoko Mohr, Jit Tran

**Repository:** [https://github.com/OtterConLabs/CST338-Project2](https://github.com/OtterConLabs/CST338-Project2)

---



## Team & Slice Ownership


| Slice                     | Owner            | GitHub username | Issues                                 | Branch(es)                                                                                                                                                                            | PR(s)                                           | Enhancement chosen                                                      | Status      |
| ------------------------- | ---------------- | --------------- | -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------- | ----------------------------------------------------------------------- | ----------- |
| 1 — Accounts             | Yoko Mohr        | yokocsumb       | #2, #3, #5, #6, #8, #14, #30, #37, #41 | yoko/javafx_app, yoko/SQLite_database_structure, yoko/database-singleton, yoko/login-fxml, yoko/account-fxml-crud, yoko/FXML-dashboard-setup, yoko/test-UserDao, yoko/account-feature | #4, #12, #15, #19, #20, #31, #39, #42, #46, #56 | Notifications / alerts on app events                                    | in-progress |
| 2 — Courses & Enrollment | Brent Brewington | Brewsdawg       | #48, #55, #60, #61                     | brent/course-dao, brent/course-list-scene, brent/enrollment-scene                                                                                                                     | #21, #44, #47, #54                              | TableView / ListView populated with live data                           | in-progress |
| 3 — Assignments          | Jordan Browning  | jbrowning-otter | #13, #16, #17                          | jordan/assignment-dao, jordan/assignment-list-scene, jordan/assignment-form-scene                                                                                                     | #43, #45                                        | Data Binding using ObservableList                                       | in-progress |
| 4 — Grades               | Jit Tran         | jtcsumb         | #24, #25, #26                          | jtcsumb/grade-dao, jtcsumb/grade-entry-scene, jtcsumb/grade-statistics                                                                                                                | —                                              | Data binding using ObservableList and JavaFX properties                 | planned     |
| 5 — Attendance           | Jit Tran         | jtcsumb         | #27, #28, #29                          | jtcsumb/attendance-dao, jtcsumb/attendance-roster, jtcsumb/attendance-report                                                                                                          | —                                              | Notifications for overwrite confirmation, saves, and CSV export results | planned     |

_Status values: planned · in-progress · complete_

### Slice 2 — Courses & Enrollment (detail)

- **Issues:** #48, #55, #60, #61
- **Pull requests:** #21, #44, #47, #54
- **Branches:** brent/course-dao, brent/course-list-scene, brent/enrollment-scene
- **Enhancement:** TableView / ListView populated with live data
- **Extra credit:** Course capacity / seat limits on enrollment (see [Extra Credit Log](#extra-credit-log))
- **Status:** in-progress

Work is assigned to Brewsdawg, labeled (`slice-2`, `testing`, `enhancement` as appropriate), and closed by a PR via `Closes #N`.

## WILL NOT DO (declared scope cuts)


| Slice                     | Features (Will Not Implement)                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------- |
| 1 — Accounts             | Admin role, Password reset                                                                                  |
| 2 — Courses & Enrollment | Course sections, Enrollment waitlists,~~Course capacity limits~~ (implemented as extra credit — see below) |
| 3 — Assignments          | Due-date reminders, File attachments, Weighted assignment categories                                        |
| 4 — Grades & Statistics  | Weighted-category grade calculations, GPA calculation                                                       |
| 5 — Attendance & Reports | Calendar view, Email notifications                                                                          |

## Code Review Log


| PR  | Author          | Human reviewer(s) | AI review (link)                                        | Outcome                                      |
| --- | --------------- | ----------------- | ------------------------------------------------------- | -------------------------------------------- |
| #40 | Jordan Browning | Yoko Mohr         | N/A                                                     | Approved and merged                          |
| #50 | Jordan Browning | Jit Tran          | N/A                                                     | Approved and merged                          |
| #56 | Yoko Mohr       | REVIEWER_NAME     | https://github.com/OtterConLabs/CST338-Project2/pull/56 | 3 accepted, 1 partially accepted, 1 rejected |

## AI Usage Log

Full prompts, AI output, and curation notes are documented per owner in [TESTING.md](TESTING.md).

- **AI-drafted tests:**
  - **Brent Brewington (Slice 2 — Courses & Enrollment):** AI-assisted drafting for the Slice 2 test suite (`CourseDaoTest`, `EnrollmentDaoTest`, `CourseValidatorTest`) and the extra-credit capacity tests (`CourseCapacityValidatorTest`, `CourseCapacityUiTest`, `EnrollmentCapacityTest`); all reviewed, curated, and verified by the author before merge.
  - **Jordan Browning (Slice 3 — Assignments):** AI drafted a single full-CRUD TestFX test; curated down to focused navigation tests (`addButtonOpensAssignmentForm`, `cancelReturnsToAssignmentList`) with DAO/model behavior kept in `AssignmentDaoTest` and `AssignmentTest`. Details in TESTING.md.
  - **Yoko Mohr (Slice 1 — Accounts):** AI-drafted `LoginSceneTest`; reviewed and verified by the author (linked from PR #56).
- **AI code reviews:**
  - **Yoko Mohr — PR #56:** AI review adjudicated 3 accepted, 1 partially accepted, 1 rejected (link in [Code Review Log](#code-review-log)).

## Extra Credit Log


| Item                                                                                                              | Who              | Evidence (Issue / PR / Tests)                                                                               |
| ----------------------------------------------------------------------------------------------------------------- | ---------------- | ----------------------------------------------------------------------------------------------------------- |
| Built Slice 5 (Attendance)                                                                                        | Jit Tran         | #27, #28, #29                                                                                               |
| Course capacity / seat limits on enrollment — originally a Slice 2 "Will Not Do" item, delivered as extra credit | Brent Brewington | Issues #55, #60, #61; Tests:`CourseCapacityValidatorTest`, `CourseCapacityUiTest`, `EnrollmentCapacityTest` |

## Build & Run

```
./gradlew run        # launch the app
./gradlew test       # run the test suite
```

**Requirements:** JDK _\<version\>_, JavaFX _\<version\>_. Add any setup notes here.
