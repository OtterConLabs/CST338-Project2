# TESTING.md — Slice 2: Courses & Enrollment
**Owner:** Brent Brewington · **Slice:** Courses & Enrollment · **Milestone 2**

> **Before you submit:** the AI section below is a *record of what actually happened*. I have filled in the findings that are verifiable in the committed code, but **you must paste your own prompt text and your own screenshots/commit links**. If you did not personally hit one of the issues listed, cut it. A reflection that does not match your commit history is worse than a short honest one.

---

## 1. Test suite overview

| File | Type | Count | What it covers |
|---|---|---|---|
| `CourseDaoTest.java` | DAO unit | 11 | Full CRUD, `findByTeacherId` filter, ordering, 4 negative cases |
| `EnrollmentDaoTest.java` | DAO unit | 6 | Enroll, unenroll, roster queries, cascade delete, 2 negative cases |
| `CourseValidatorTest.java` | Logic unit | 6 | Form rules, role rules, multi-error reporting |
| `CourseSceneUiTest.java` | TestFX UI | 6 | Roster rendering, enroll/unenroll transfer, button binding, scene transition |
| **Total** | | **29** | |

Run everything: `./gradlew test`
Run headless (CI or no display): `./gradlew test -Dtestfx.headless=true -Dglass.platform=Monocle`

### Required coverage, mapped

| Requirement | Satisfied by |
|---|---|
| DAO insert / read / update / delete | `crud_roundTripWorksEndToEnd()` in `CourseDaoTest` |
| Domain/service logic | `CourseValidatorTest` (all 6) |
| At least one negative or edge case | 6 negative tests, listed in §3 |
| At least one TestFX scene transition | `clickingBack_transitionsToCourseListScene()` |

---

## 2. Why SQLite instead of H2

The Part 01 plan said in-memory **H2**. The suite runs on in-memory **SQLite** (`jdbc:sqlite::memory:`) instead.

The shared schema uses SQLite-specific syntax: `AUTOINCREMENT`, `datetime('now')`, and `COLLATE NOCASE`. H2 rejects or reinterprets all three. Testing on H2 would mean maintaining a second, rewritten schema and testing DDL the application never executes, which is exactly the kind of drift that lets a constraint bug reach production. `CourseSchema.create()` is called by both `DatabaseManager` and every test, so there is one schema and no drift.

In-memory SQLite keeps the properties that made H2 attractive: each test gets a throwaway database in `@BeforeEach`, so tests are order-independent and leave nothing behind.

---

## 3. Negative and edge cases

| Test | Rule proven |
|---|---|
| `insert_duplicateCourseCode_isRejected` | `UNIQUE ... COLLATE NOCASE` blocks `cst338` when `CST338` exists |
| `newCourse_blankCode_throws` | A blank code fails in the constructor, never reaching SQL |
| `findByTeacherId_invalidId_throws` | A non-positive ID is a programming error, not a normal miss |
| `delete_missingCourse_returnsFalse` | Deleting a non-existent row reports failure rather than throwing |
| `enroll_duplicateStudent_isRejected` | The duplicate-enrollment alternate flow from Use Case 2 |
| `unenroll_studentNotEnrolled_returnsFalse` | Removing a non-enrolled student is a no-op |
| `deleteCourse_cascadesToEnrollment` | `ON DELETE CASCADE` leaves no orphaned junction rows |

---

## 4. AI-drafted, then curated tests

### 4.1 The prompt

> **Paste your actual prompt here.** The prompt I was given was, in substance:
>
> *"Write JUnit 5 tests for my CourseDao and EnrollmentDao classes for a CST338 JavaFX group project. The DAOs use SQLite through a shared DatabaseManager singleton. Cover full CRUD, the enrollment junction table, and include negative cases. Also write a TestFX test for the Enrollment scene."*

Follow-up prompts asked for a TestFX UI test and for the tests to run without touching the real database.

### 4.2 What the AI produced

The first draft was a plausible-looking suite of roughly 30 tests. It compiled in outline but had four problems that mattered and several that did not.

### 4.3 My evaluation

**Valid and kept (with light edits):**
- The CRUD round-trip structure, which is genuinely the core of the DAO test.
- The `@BeforeEach` / `@AfterEach` fresh-connection pattern for order-independence.
- The duplicate-course-code and duplicate-enrollment negative cases.

**Wrong — the AI proposed H2, which the schema cannot run.**
The first draft opened `jdbc:h2:mem:testdb` and pasted a rewritten `CREATE TABLE` into the test file, silently dropping `AUTOINCREMENT` and `COLLATE NOCASE`. That would have produced a green suite that proved nothing about the real schema, and specifically would have made `insert_duplicateCourseCode_isRejected` pass for the wrong reason. **I changed it to in-memory SQLite and extracted `CourseSchema` so the tests and the app share one DDL.** This is the single most important correction I made.

**Wrong — foreign keys were never enforced.**
SQLite disables foreign keys per connection by default. The AI's schema declared `FOREIGN KEY ... ON DELETE CASCADE` and then wrote a test asserting the cascade worked. Without `PRAGMA foreign_keys = ON`, that test fails, and worse, the constraint does nothing at runtime either. **I added the PRAGMA inside `CourseSchema.create()` so it runs on every connection that builds the schema.**

**Wrong — the TestFX test could not inject a test DAO.**
The draft called `controller.setCourseDao(testDao)` from a controller factory. Because `setCourseDao` immediately called `refreshTable()`, and `FXMLLoader` had not yet injected `@FXML` fields, this threw `NullPointerException` on `courseTable`. **I added a null guard to the three DI setters so `initialize()` performs the first refresh instead.** This was not a test bug; the AI's draft exposed a real design flaw in my controllers.

**Trivial — deleted.**
About eight tests asserted that a getter returns what the constructor was handed (`assertEquals("CST338", course.getCourseCode())` on a freshly constructed object). These test the Java language, not my code. I deleted them and replaced the coverage with `validate_emptyForm_returnsAllErrors`, which proves the form reports *every* problem at once rather than stopping at the first, which is behavior a user actually sees.

**Hallucinated — deleted.**
The draft called `courseDao.findByCourseCode("CST338")` and `enrollmentDao.countEnrollments(courseId)`. Neither method exists. I deleted both rather than writing the methods, because neither is needed by any scene in my slice and adding them to satisfy a test would be backwards.

**Weak assertion — strengthened.**
The AI's enroll test asserted only that the enrolled `ListView` grew by one. That passes even if the row was added to the observable list but never written to the database. **I added `assertTrue(enrollmentDao.isEnrolled(...))` so the test proves persistence, not just a visual change.**

### 4.4 Known limitation I chose to accept

`clickingBack_transitionsToCourseListScene()` goes through the real `SceneFactory`, which constructs a real `CourseDao` and therefore opens `app.db`. I kept it anyway: the requirement is to verify a *scene transition*, and stubbing out `SceneFactory` would mean testing a fake instead of the wiring that actually ships. The trade-off is that this one test creates a file in the working directory. The other five UI tests are fully hermetic.

### 4.5 What I would do differently

The AI's drafts were fastest to fix when I gave it the actual schema rather than describing it. Every one of the four real defects came from the AI guessing at something it could not see: it guessed H2, guessed that declared foreign keys are enforced, guessed the controller lifecycle, and guessed method names. The lesson I am taking forward is that AI-drafted tests are useful for *structure and breadth* and unreliable for *anything environment-specific*. The curation work was almost entirely about the environment, not about the test logic.

---

## 5. Evidence for the video

| Show | Where |
|---|---|
| Full suite green | `./gradlew test` terminal output |
| TestFX window driving itself | Run non-headless so the robot is visible |
| Negative case | Add a course with a duplicate code, show the inline error |
| Cascade | Delete a course with enrolled students, confirm the dialog, show the roster is gone |
