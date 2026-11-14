package dev.cyberjar.springfx.controller;

import dev.cyberjar.springfx.config.StageManager;
import dev.cyberjar.springfx.controller.ui.ToolbarController;
import dev.cyberjar.springfx.user.CurrentUser;
import dev.cyberjar.springfx.user.User;
import dev.cyberjar.springfx.word.StudyWord;
import dev.cyberjar.springfx.word.StudyWordRepository;
import dev.cyberjar.springfx.word.StudyWordService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class AddWordsControllerTest {

    private StudyWordRepository repository;
    private StudyWord existingWord;
    private TextField termField;
    private TextField readingField;
    private TextField translationField;
    private Label titleLabel;
    private Label errorLabel;
    private TableView<StudyWord> wordsTable;

    @Start
    @SuppressWarnings("unchecked")
    public void start(Stage stage) throws Exception {
        // Only persistence and window navigation are mocked. Validation is the real service.
        CurrentUser currentUser = new CurrentUser();
        User user = new User(42L, "learner");
        currentUser.set(user);
        repository = mock(StudyWordRepository.class);
        existingWord = new StudyWord(user, "cat", "kat", "feline");
        List<StudyWord> storedWords = new ArrayList<>(List.of(existingWord));
        when(repository.findByUserIdAndActiveTrueOrderByTermAsc(42L))
                .thenAnswer(call -> List.copyOf(storedWords));
        when(repository.save(any(StudyWord.class))).thenAnswer(call -> {
            StudyWord word = call.getArgument(0);
            storedWords.add(word);
            return word;
        });

        AddWordsController controller = new AddWordsController(currentUser,
                new StudyWordService(repository, currentUser));
        ToolbarController toolbar = new ToolbarController(mock(StageManager.class));
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                getClass().getResource("/fxml/add-words.fxml")));
        // The same factory also constructs the real controller for the included toolbar FXML.
        loader.setControllerFactory(type -> {
            if (type == AddWordsController.class) {
                return controller;
            }
            if (type == ToolbarController.class) {
                return toolbar;
            }
            throw new IllegalArgumentException("Unexpected FXML controller: " + type);
        });
        Parent root = loader.load();
        termField = (TextField) loader.getNamespace().get("termField");
        readingField = (TextField) loader.getNamespace().get("readingField");
        translationField = (TextField) loader.getNamespace().get("translationField");
        titleLabel = (Label) loader.getNamespace().get("titleLabel");
        errorLabel = (Label) loader.getNamespace().get("addWordErrorLabel");
        wordsTable = (TableView<StudyWord>) loader.getNamespace().get("wordsTable");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/styles/styles.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @AfterEach
    void checkJavaFxExceptions() throws Throwable {
        WaitForAsyncUtils.checkException();
    }

    @Test
    void initializesActualScreenWithCurrentUsersWordsAndEssentialControls(FxRobot robot) {
        robot.interact(() -> {
            assertEquals("Add words for learner", titleLabel.getText());
            assertTrue(termField.isVisible());
            assertTrue(readingField.isVisible());
            assertTrue(translationField.isVisible());
            assertFalse(robot.lookup("Add word").queryButton().isDisabled());
            assertTrue(robot.lookup("#homeButton").queryButton().isVisible());
            assertTrue(wordsTable.isVisible());
            assertEquals(List.of(existingWord), wordsTable.getItems());
            assertEquals(List.of("Word", "Reading", "Translation"),
                    wordsTable.getColumns().stream().map(column -> column.getText()).toList());
            assertEquals("cat", wordsTable.getColumns().get(0).getCellData(0));
            assertEquals("kat", wordsTable.getColumns().get(1).getCellData(0));
            assertEquals("feline", wordsTable.getColumns().get(2).getCellData(0));
        });
        verify(repository).findByUserIdAndActiveTrueOrderByTermAsc(42L);
        verify(repository, never()).save(any(StudyWord.class));
    }

    @Test
    void addsWordThroughFormThenRefreshesTableAndClearsFields(FxRobot robot) {
        // First trigger validation so successful submission must also clear the error label.
        robot.clickOn("Add word");
        robot.interact(() -> assertEquals("Word must not be blank", errorLabel.getText()));

        robot.clickOn(termField).write("  dog  ");
        robot.clickOn(readingField).write("  dog-reading  ");
        robot.clickOn(translationField).write("  canine  ");
        robot.clickOn("Add word");

        robot.interact(() -> {
            assertEquals(2, wordsTable.getItems().size());
            assertSame(existingWord, wordsTable.getItems().getFirst());
            assertEquals("dog", wordsTable.getColumns().get(0).getCellData(1));
            assertEquals("dog-reading", wordsTable.getColumns().get(1).getCellData(1));
            assertEquals("canine", wordsTable.getColumns().get(2).getCellData(1));
            assertEquals("", termField.getText());
            assertEquals("", readingField.getText());
            assertEquals("", translationField.getText());
            assertEquals("", errorLabel.getText());
            assertTrue(termField.isFocused());
        });
        verify(repository).save(any(StudyWord.class));
        verify(repository, times(2)).findByUserIdAndActiveTrueOrderByTermAsc(42L);
    }

    @Test
    void invalidSubmissionShowsValidationErrorAndPreservesFormAndTable(FxRobot robot) {
        robot.clickOn(termField).write("dog");
        robot.clickOn("Add word");

        robot.interact(() -> {
            assertTrue(errorLabel.isVisible());
            assertEquals("Translation must not be blank", errorLabel.getText());
            assertEquals("dog", termField.getText());
            assertEquals(List.of(existingWord), wordsTable.getItems());
        });
        verify(repository, never()).save(any(StudyWord.class));
        verify(repository, times(1)).findByUserIdAndActiveTrueOrderByTermAsc(42L);
    }
}
