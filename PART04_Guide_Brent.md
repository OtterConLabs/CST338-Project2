# CST 338 — Project 02, Part 04: Final Application
## Slice 2 — Courses & Enrollment (Brent Brewington)

Part 04 is 50 points, half the project. This is the build order.

---

## 1. Where each rubric area is earned

| Area | Pts | Where it comes from |
|---|---|---|
| Feature slice | 20 | 3 scenes, full CRUD through `CourseDao`, rules in `CourseValidator`, live-data enhancement |
| Testing | 10 | 29 tests incl. TestFX; `TESTING.md` AI reflection |
| GitHub contributions | 8 | `brent/*` branches, 15+ commits, 3+ issues closed by merged PRs |
| Code review | 5 | Reviews of teammates' PRs + your adjudicated AI review |
| Teamwork / integration | 7 | Clean clone builds and runs, scene factory wires all slices, README current |

---

## 2. New files for this milestone

| File | Goes in | What it is |
|---|---|---|
| `CourseEditController.java` | `src/main/java` | Add/Edit form; one scene serves both |
| `CourseEditScene.fxml` | `src/main/resources` | The form layout |
| `EnrollmentController.java` | `src/main/java` | Two-list roster manager |
| `EnrollmentScene.fxml` | `src/main/resources` | The roster layout |
| `CourseListController.java` | `src/main/java` | **Replaces** your Part 02 version |
| `CourseSceneUiTest.java` | `src/test/java` | TestFX suite |
| `TESTING.md` | repo root | Test docs + AI reflection |

---

## 3. Three edits to shared files

These touch Yoko's files. Keep each diff small and mention them in the PR.

### 3a. `SceneType.java` — add one value

```java
    //Brent
    COURSE_LIST,
    COURSE_EDIT,
    ENROLLMENT,
```

### 3b. `SceneFactory.java` — a selected-course holder plus two real scene builders

Add the holder next to the existing `loggedInUser` field. This mirrors the pattern Yoko already established, which is why it is the right call here: the scene factory's `create(SceneType, Stage)` signature has no room for a payload, and changing that signature would break every slice.

```java
    // Stores the course the user picked on the Course List screen so the
    // Edit and Enrollment scenes know which course they are working on.
    private static Course selectedCourse;

    /**
     * Saves the course being edited or managed. Pass null to indicate that
     * the Course Edit scene should create a new course.
     *
     * @param course the selected course, or null for a new one
     */
    public static void setSelectedCourse(Course course) {
        selectedCourse = course;
    }

    /**
     * Returns the course being edited or managed.
     *
     * @return the selected course, or null if a new course is being added
     */
    public static Course getSelectedCourse() {
        return selectedCourse;
    }
```

Add the case to the switch:

```java
            case ENROLLMENT -> buildEnrollmentScene(stage);
```

Then replace the two placeholder builders:

```java
    // Loads the Add/Edit Course scene and connects its controller.
    private static Scene buildCourseEditScene(Stage stage) {
        return loadScene("/CourseEditScene.fxml", stage);
    }

    // Loads the Manage Enrollment scene and connects its controller.
    private static Scene buildEnrollmentScene(Stage stage) {
        return loadScene("/EnrollmentScene.fxml", stage);
    }
```

If you want the smallest possible diff, keep the try/catch inline in each method the way Yoko wrote `buildLoginScene`. If she is open to it, the shared helper below removes four copies of the same block, but **ask her first** since it is her file:

```java
    /**
     * Loads a scene from FXML and hands the controller the primary Stage.
     * Every controller in this application exposes setStage(Stage), so the
     * call is made reflectively to keep this helper generic.
     */
    private static Scene loadScene(String fxmlPath, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneFactory.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setStage", Stage.class)
                    .invoke(controller, stage);

            return scene;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlPath, e);
        }
    }
```

> **My recommendation:** use the inline try/catch version. Reflection here trades a compile-time error for a runtime one, and on a shared file with three other people, boring and obvious beats clever. The helper is shown only because you will be tempted by the duplication.

### 3c. `build.gradle` — add TestFX

```groovy
dependencies {
    implementation 'org.xerial:sqlite-jdbc:3.46.1.3'

    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // TestFX UI testing
    testImplementation 'org.testfx:testfx-core:4.0.18'
    testImplementation 'org.testfx:testfx-junit5:4.0.18'
    // Monocle enables headless runs
    testImplementation 'org.testbench:openjfx-monocle:21.0.2'
}

test {
    useJUnitPlatform()
}
```

If Monocle fails to resolve, drop that one line and run the UI tests with a visible window. Headless is a convenience, not a requirement.

---

## 4. Integration checklist (the shared 7 points)

Do this from a **fresh clone**, not your working copy. This is the single most common place teams lose integration points, because a stale `app.db` or an untracked file hides the problem locally.

```bash
git clone <repo-url> fresh-check
cd fresh-check
./gradlew clean build
./gradlew test
./gradlew run
```

Then walk the whole app: Register → Login → Dashboard → each slice's button → back to Dashboard.

- [ ] `.gitignore` excludes `app.db`, `build/`, `.gradle/`, `.idea/`
- [ ] No absolute paths anywhere in the build
- [ ] Every `SceneType` value maps to a real scene, no placeholders left
- [ ] Dashboard reaches all four slices
- [ ] README build/run instructions actually work as written

If a teammate's slice is not done, **do not fix it silently**. Open an issue, tag them, and note it in the README status column. Your grade is individual; quietly absorbing their work costs you time and them the feedback.

---

## 5. README updates

Update your slice row to `complete`, then fill in the Build & Run section, which is currently a stub with blank JDK and JavaFX versions:

```markdown
### Build & Run

**Requirements:** JDK 21, Gradle wrapper (included). JavaFX 21.0.2 is pulled
automatically by the Gradle plugin, so no manual SDK install is needed.

```bash
./gradlew run     # launch the app
./gradlew test    # run the full test suite
```

Run UI tests headless:

```bash
./gradlew test -Dtestfx.headless=true -Dglass.platform=Monocle
```

The SQLite database file `app.db` is created automatically in the project root
on first run and is not tracked in git.

**First-time use:** register an account with the TEACHER role, then log in.
A course requires a teacher, so at least one TEACHER account must exist before
the Add Course form will save.
```

Also add to the AI Usage Log:

```markdown
- **AI-drafted tests:** Slice 2 — see TESTING.md §4 (Brent Brewington)
- **AI code reviews:** Slice 2 — PR #<n>, adjudication in PR thread (Brent Brewington)
```

---

## 6. Git workflow

```bash
git checkout main && git pull
git checkout -b brent/course-edit-scene
# commit CourseEditController + CourseEditScene.fxml + SceneFactory/SceneType edits
git push -u origin brent/course-edit-scene

git checkout main && git pull
git checkout -b brent/enrollment-scene
# commit EnrollmentController + EnrollmentScene.fxml + CourseListController update
git push -u origin brent/enrollment-scene

git checkout main && git pull
git checkout -b brent/testfx-and-docs
# commit CourseSceneUiTest + TESTING.md + README updates
git push -u origin brent/testfx-and-docs
```

Suggested commit messages, one per logical change, to keep your commit count honest and your PRs reviewable:

```
Add ENROLLMENT scene type and selected-course holder to SceneFactory
Add Course Edit form with validation and teacher dropdown
Wire Course List Add and Edit buttons to the edit form
Add confirmation dialog before deleting a course
Add Manage Enrollment scene with available and enrolled lists
Enforce duplicate enrollment rule in the enrollment controller
Guard DI setters against pre-injection null controls
Add TestFX suite for the Enrollment scene
Document test suite and AI curation in TESTING.md
Update README build and run instructions
```

**Still outstanding for full credit:** you owe at least one **AI code review of your own PR**, adjudicated finding by finding in the PR thread (Part 04 rubric, 5 pts), and the README needs the link. Do that on `brent/enrollment-scene` since it has the most logic.

---

## 7. Submission checklist

- [ ] All three scenes work: list, add/edit, enrollment
- [ ] Create, read, update, delete all demonstrated through the UI
- [ ] Enhancement visible and explained: `ObservableList`-backed TableView and two ListViews
- [ ] `./gradlew test` green, 29 tests
- [ ] TESTING.md has **your** prompts and **your** findings
- [ ] Fresh clone builds and runs
- [ ] README slice row `complete`, Build & Run filled in, AI log linked
- [ ] Video recorded, under 8 minutes total, you narrate your own slice
