# Project 2 Testing

## Assignment Slice

The Assignment slice includes DAO tests, model/domain logic tests, and TestFX UI tests.

My DAO tests cover creating, reading, updating, deleting, and retrieving Assignments. I also added an edge-case test for an invalid Assignment ID.

My Assignment model test verifies validation behavior, including preventing negative values for points possible.

My TestFX tests verify navigation between the Assignment List and Assignment Form.

---

# AI-Drafted, Then Curated Tests

## Prompt Given to AI

I asked ChatGPT to generate a more complete TestFX test for my Assignment slice.

My prompt was:

> Can you make a much more complete TestFX test for my Assignment slice? I already have a basic test that checks if the Add button opens the Assignment form, but I want you to go further. Have it create an assignment through the UI, make sure it shows up in the table, edit it, verify the changes, and then delete it. Include whatever else you think is needed to test the full Assignment UI.

---

## What the AI Produced

The AI suggested creating one large TestFX test that performed almost the entire Assignment CRUD workflow through the UI.

The generated approach attempted to:

1. Open the Assignment Form.
2. Select a Course from the ComboBox.
3. Enter an Assignment title.
4. Enter a description.
5. Enter a due date.
6. Enter the points possible.
7. Save the Assignment.
8. Verify that the Assignment appeared in the TableView.
9. Select the Assignment.
10. Open the Edit form.
11. Modify the Assignment.
12. Save the changes.
13. Verify that the updated Assignment appeared.
14. Delete the Assignment.
15. Confirm the deletion.

A portion of the AI-generated test looked like this:

```java
@Test
void completeAssignmentCrudWorkflow()
{
    clickOn("Add");

    clickOn("#courseComboBox");
    press(KeyCode.DOWN);
    press(KeyCode.ENTER);

    clickOn("#titleField").write("TestFX Assignment");
    clickOn("#descriptionArea").write("Created through TestFX");
    clickOn("#pointsField").write("100");

    clickOn("Save");

    verifyThat("#assignmentTable", isVisible());

    clickOn("TestFX Assignment");
    clickOn("Edit");

    verifyThat("#formTitleLabel", hasText("Edit Assignment"));

    clickOn("Save");

    clickOn("TestFX Assignment");
    clickOn("Delete");
    clickOn("OK");
}
```

---

## My Evaluation of the AI-Generated Test

I thought the AI-generated test was more complicated than what I needed for the UI portion of my Assignment slice.

The test depended on several different parts of the application working at the same time. It depended on a Course already being available in the database, the Course ComboBox having a usable selection, database state, form input, TableView behavior, editing, and the delete confirmation dialog.

I also noticed that the test was repeating behavior that I was already testing in `AssignmentDaoTest`.

My DAO tests already verify:

- inserting an Assignment
- finding an Assignment by ID
- retrieving Assignments
- retrieving Assignments by Course
- updating an Assignment
- deleting an Assignment
- handling an invalid Assignment ID

Because the DAO tests already verify the database operations, I did not think it was necessary for one TestFX test to repeat the entire CRUD process through the UI.

The AI-generated test also had more possible failure points. For example, the test could fail because a Course was unavailable or because a UI control behaved differently, even if the Assignment DAO itself was working correctly.

I decided that TestFX should focus on testing the UI and scene navigation while the DAO tests should focus on database operations.

---

## What I Changed and Why

Instead of using the entire AI-generated CRUD TestFX test, I reduced the TestFX testing to focused tests that verify navigation within my Assignment slice.

The first test verifies that clicking the Add button from the Assignment List opens the Assignment Form.

```java
@Test
void addButtonOpensAssignmentForm()
{
    clickOn("Add");

    verifyThat("#formTitleLabel", isVisible());
}
```

I also added another TestFX test that verifies navigation in the opposite direction.

The test opens the Assignment Form and then clicks Cancel. It verifies that the application returns to the Assignment List.

```java
@Test
void cancelReturnsToAssignmentList()
{
    clickOn("Add");

    verifyThat("#formTitleLabel", isVisible());

    clickOn("Cancel");

    verifyThat("#assignmentTable", isVisible());
}
```

I kept the database CRUD testing in `AssignmentDaoTest` instead of duplicating all of that behavior inside TestFX.

I also created an `AssignmentTest` for model/domain logic. One of these tests verifies that an Assignment cannot be created with negative points possible.

This gave each type of test a specific responsibility:

- `AssignmentDaoTest` tests database and DAO behavior.
- `AssignmentTest` tests Assignment model/domain rules.
- `AssignmentListSceneTest` tests JavaFX UI navigation.

---

## Edge-Case Test

I added an edge-case test to `AssignmentDaoTest`.

The test calls `deleteById(0)` and verifies that the DAO returns `false`.

```java
@Test
void deleteByIdInvalidId() throws SQLException
{
    boolean deleted = assignmentDao.deleteById(0);

    assertFalse(deleted);
}
```

I chose this test because Assignment IDs should be greater than zero and my DAO already defines how an invalid ID should be handled.

---

## Domain Logic Test

I added an `AssignmentTest` to test validation inside the Assignment model.

The test attempts to create an Assignment with negative points possible.

```java
@Test
void negativePointsThrowsException()
{
    assertThrows(
            IllegalArgumentException.class,
            () -> new Assignment(
                    1,
                    "Test Assignment",
                    "Testing negative points",
                    LocalDate.of(2026, 8, 15),
                    -1
            )
    );
}
```

The expected result is an `IllegalArgumentException` because negative points possible are not valid for an Assignment.

---

## Final Test Suite

After curating the AI-generated testing ideas, my final Assignment testing is divided between DAO testing, model/domain testing, and TestFX UI testing.

The final Assignment tests include:

- DAO insert testing
- DAO find-by-ID testing
- DAO find-all testing
- DAO Course filtering
- DAO update testing
- DAO delete testing
- an invalid-ID edge case
- Assignment model validation
- Assignment List to Assignment Form navigation
- Assignment Form back to Assignment List navigation

Before submission, I ran the complete project test suite and verified that all tests passed.