package careconnect.logic.commands;

import static careconnect.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static careconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static careconnect.testutil.TypicalPersons.ALICE;
import static careconnect.testutil.TypicalPersons.CARL;
import static careconnect.testutil.TypicalPersons.ELLE;
import static careconnect.testutil.TypicalPersons.FIONA;
import static careconnect.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import careconnect.model.Model;
import careconnect.model.ModelManager;
import careconnect.model.UserPrefs;
import careconnect.model.person.NameAndAddressContainsKeywordPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameAndAddressContainsKeywordPredicate firstPredicate =
                new NameAndAddressContainsKeywordPredicate(
                        Collections.singletonList("first"),
                        Collections.singletonList("first"));
        NameAndAddressContainsKeywordPredicate secondPredicate =
                new NameAndAddressContainsKeywordPredicate(
                        Collections.singletonList("second"),
                        Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertEquals(findFirstCommand, findFirstCommand);

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertEquals(findFirstCommand, findFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, findFirstCommand);

        // null -> returns false
        assertNotEquals(null, findFirstCommand);

        // different person -> returns false
        assertNotEquals(findFirstCommand, findSecondCommand);
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameAndAddressContainsKeywordPredicate predicate = preparePredicate(" ", " ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameAndAddressContainsKeywordPredicate predicate = preparePredicate("Kurz Elle Kunz", "");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_addressKeyword_onePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameAndAddressContainsKeywordPredicate predicate = preparePredicate("", "jurong");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameAndAddressKeywords_onePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameAndAddressContainsKeywordPredicate predicate = preparePredicate("Alice", "jurong");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameAndAddressContainsKeywordPredicate predicate =
                new NameAndAddressContainsKeywordPredicate(List.of("keyword"), List.of("keyword 2"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameAndAddressContainsKeywordPredicate preparePredicate(String userInputName,
                                                                    String userInputAddress) {
        return new NameAndAddressContainsKeywordPredicate(
                userInputName.length() == 0
                        ? List.of()
                        : Arrays.asList(userInputName.split("\\s+")),
                userInputAddress.length() == 0
                        ? List.of()
                        : Arrays.asList(userInputAddress.split("\\s+"))
        );
    }
}
