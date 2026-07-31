# CST 338 — Project 02, Part 02: The Data
## Slice 2 — Courses & Enrollment (Brent Brewington)

This is the build order for Milestone 1. Every file referenced here is in the `slice2` folder and drops straight into the team repo.

---

## 1. What Part 02 grades, and where each point comes from

| Requirement | Where it is satisfied |
|---|---|
| Table added to the shared schema | `CourseSchema.java` (courses + enrollment) called from `DatabaseManager.createTables()` |
| DAO with working CRUD | `CourseDao.java` (insert / findById / findAll / update / delete + `findByTeacherId` filter) and `EnrollmentDao.java` |
| One working FXML scene through the scene factory | `CourseListScene.fxml` + `CourseListController.java`, wired into `SceneFactory.buildCourseListScene()` |
| At least one passing test | `CourseValidatorTest.java` (logic, no DB) and `CourseDaoTest.java` / `EnrollmentDaoTest.java` (DAO CRUD) |
| Substantive review of a teammate's PR | Draft comments in `PR_REVIEW_AssignmentDao.md` |
| DB works end to end | `crud_roundTripWorksEndToEnd()` in `CourseDaoTest` proves insert → read → update → delete |
| Conventions (`name/feature-name`, `Closes #N`) | Section 6 below |
| README updated | Section 7 below |

---

## 2. Files to add to the repo

Put these in the same source folder the team is already using (`src/main/java`, since the project has no package declarations):

| File | What it is |
|---|---|
| `Course.java` | Model class. **Rename your empty `Courses.java` to `Course.java`** — one object represents one course, and the DAO and TableView read better singular. |
| `Enrollment.java` | Model class for the junction row. Replaces your empty stub. |
| `CourseSchema.java` | The DDL for both of your tables, plus a `create(Connection)` method the app and the tests both call. |
| `CourseDao.java` | Full CRUD plus `findByTeacherId()`. |
| `EnrollmentDao.java` | `enroll`, `unenroll`, `isEnrolled`, `findEnrolledStudents`, `findAvailableStudents`. |
| `CourseValidator.java` | The domain/logic class that holds the slice's rules, testable with no database. |
| `CourseListController.java` | Controller for your scene. |
| `CourseListScene.fxml` | Goes in `src/main/resources` next to `LoginScene.fxml`. |
| `CourseDaoTest.java`, `EnrollmentDaoTest.java`, `CourseValidatorTest.java` | Go in `src/test/java`. |

**Why `CourseSchema` instead of pasting SQL into `DatabaseManager`:** your tests need the exact same DDL the app runs. Keeping the SQL in one class means the schema can never drift between production and test, and it keeps your diff into Yoko's file down to two lines, which makes her review easy.

---

## 3. Two small edits to shared files

### `DatabaseManager.java` (Yoko's file — keep the diff tiny)

Uncomment your line in `createTables()` and add the helper:

```java
    private void createTables() {
        createUsersTable();

        // Brent
        createCoursesTable();

        // Jordan
//        createAssignmentsTable();
    }

    /**
     * Creates the Slice 2 tables (courses and enrollment) and turns on
     * foreign key enforcement, which SQLite leaves off by default.
     */
    private void createCoursesTable() {
        try {
            CourseSchema.create(connection);
            System.out.println("Courses and enrollment tables ready.");
        } catch (SQLException e) {
            System.out.println("createCoursesTable failed: " + e.getMessage());
        }
    }
```

> Heads up for the PR description: `PRAGMA foreign_keys = ON` runs inside `CourseSchema.create()`. It is per-connection in SQLite, so if the team ever opens a second connection, that PRAGMA has to run there too. Worth calling out so Yoko knows why it lives there.

### `SceneFactory.java` (Yoko's file)

Replace your placeholder with a real FXML load that mirrors her Login/Register pattern:

```java
    // Loads the Course List scene from its FXML file and connects its controller.
    private static Scene buildCourseListScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/CourseListScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            CourseListController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CourseListScene.fxml", e);
        }
    }
```

Leave `buildCourseEditScene()` on the placeholder for now. Milestone 1 only requires **one** working scene, and the Dashboard already has a "Courses & Enrollment" button that routes to `COURSE_LIST`, so your slice is reachable end to end the moment this merges.

---

## 4. Build file additions

Gradle (`build.gradle`) needs the SQLite driver on the test classpath and JUnit 5 turned on:

```groovy
dependencies {
    implementation 'org.xerial:sqlite-jdbc:3.46.1.3'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

test {
    useJUnitPlatform()
}
```

Run them with `./gradlew test`.

---

## 5. One deviation from your Part 01 test plan (say this out loud in the PR)

Part 01 said the DAO tests would run on in-memory **H2**. They run on in-memory **SQLite** (`jdbc:sqlite::memory:`) instead, because the shared schema uses SQLite-only syntax — `AUTOINCREMENT`, `datetime('now')`, and `COLLATE NOCASE`. On H2 you would be testing a rewritten schema, not the one the app actually creates. SQLite in memory gives a throwaway database per test and exercises the real DDL. That reasoning is already in the class Javadoc of `CourseDaoTest` and belongs in TESTING.md too.

---

## 6. Git workflow for the milestone

Three issues, three branches, three PRs — matching the SMART issues you opened in Part 01.

```bash
# Issue 1 — model + schema + DAO + tests
git checkout main && git pull
git checkout -b brent/course-dao
git add Course.java Enrollment.java CourseSchema.java CourseDao.java EnrollmentDao.java CourseValidator.java
git commit -m "Add Course and Enrollment models with courses/enrollment schema"
git commit -m "Add CourseDao CRUD and findByTeacherId query"
git commit -m "Add EnrollmentDao with duplicate enrollment rule"
git commit -m "Add CourseDao, EnrollmentDao, and CourseValidator unit tests"
git push -u origin brent/course-dao
```

```bash
# Issue 2 — the scene
git checkout main && git pull
git checkout -b brent/course-list-scene
git push -u origin brent/course-list-scene
```

Commit in small pieces rather than one large drop — the rubric counts 15+ commits across the project, and small commits make your PRs reviewable.

**PR description template:**

```markdown
Closes #<issue number>

### What this adds
- courses and enrollment tables added to the shared schema through CourseSchema
- CourseDao: insert, findById, findAll, update, delete, findByTeacherId
- EnrollmentDao: enroll, unenroll, isEnrolled, enrolled/available student lists
- 20 unit tests, all passing on in-memory SQLite

### How I verified it
`./gradlew test` — full CRUD round trip passes, duplicate course code and
duplicate enrollment are both rejected, deleting a course cascades to enrollment.

### Notes for the reviewer
- Test plan moved from H2 to in-memory SQLite; reasoning in the CourseDaoTest Javadoc.
- `PRAGMA foreign_keys = ON` runs per connection inside CourseSchema.create().
- Course Edit scene is still a placeholder; it lands in Milestone 2.
```

Request **Yoko** as reviewer on the DAO PR (it touches `DatabaseManager`) and **Jordan** on the scene PR (it touches `SceneFactory`).

---

## 7. README rows to update

Replace your row in the Team & Slice Ownership table:

| Slice | Owner | GitHub username | Issues | Branch(es) | PR(s) | Enhancement chosen | Status |
|---|---|---|---|---|---|---|---|
| 2 — Courses & Enrollment | Brent Brewington | Brewsdawg | #32, #33, #34 | `brent/course-dao`, `brent/course-list-scene` | #35, #36 | TableView / ListView populated with live data | in-progress |

Add your review to the Code Review Log:

| PR | Author | Human reviewer(s) | AI review (link) | Outcome |
|---|---|---|---|---|
| #35 | Brent Brewington | Yoko Mohr | pending | open |
| #<Jordan's PR> | Jordan Browning | Brent Brewington | N/A | Changes requested |

Swap the placeholder issue and PR numbers for the real ones before you submit.

---

## 8. Submission checklist

- [ ] `Courses.java` renamed to `Course.java` and filled in
- [ ] `Enrollment.java` filled in
- [ ] `CourseSchema` wired into `DatabaseManager.createTables()`
- [ ] `CourseListScene.fxml` loads through `SceneFactory` and is reachable from the Dashboard
- [ ] `./gradlew test` green, screenshot for the submission
- [ ] Full CRUD demonstrated (the round-trip test, plus Delete works from the Course List screen)
- [ ] Branches named `brent/...`, every PR says `Closes #N`
- [ ] At least one substantive, line-specific review left on a teammate's PR
- [ ] README slice row, issues, branches, PRs, and Code Review Log updated
