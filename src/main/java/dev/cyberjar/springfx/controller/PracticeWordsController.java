package dev.cyberjar.springfx.controller;

import dev.cyberjar.springfx.user.CurrentUser;
import dev.cyberjar.springfx.word.StudyWord;
import dev.cyberjar.springfx.word.StudyWordService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Lazy
@Component
public class PracticeWordsController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView bulldog;

    @FXML
    private VBox selectionView;

    @FXML
    private VBox practiceView;

    @FXML
    private VBox completedView;

    @FXML
    private TableView<PracticeWordRow> wordsTable;

    @FXML
    private TableColumn<PracticeWordRow, Boolean> selectedColumn;

    @FXML
    private TableColumn<PracticeWordRow, String> termColumn;

    @FXML
    private TableColumn<PracticeWordRow, String> readingColumn;

    @FXML
    private TableColumn<PracticeWordRow, String> translationColumn;

    @FXML
    private Label selectionErrorLabel;

    @FXML
    private Label progressLabel;

    @FXML
    private Label translationLabel;

    @FXML
    private Label originalLabel;

    @FXML
    private Button showOriginalButton;

    private final CurrentUser currentUser;
    private final StudyWordService studyWordService;

    private final ObservableList<PracticeWordRow> wordRows = FXCollections.observableArrayList();

    private List<StudyWord> selectedWords = new ArrayList<>();
    private List<StudyWord> shuffledWords = new ArrayList<>();

    private int currentIndex;

    public PracticeWordsController(
            CurrentUser currentUser,
            StudyWordService studyWordService
    ) {
        this.currentUser = currentUser;
        this.studyWordService = studyWordService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bulldog.setFitWidth(1000);
        bulldog.setPreserveRatio(true);
        bulldog.setSmooth(true);

        titleLabel.setText("Practice words for " + currentUser.getRequired().getUserName());

        configureWordsTable();
        resetPracticeState();
        loadWordSelection();
        showSelectionView();
    }

    @FXML
    public void selectAllWords() {
        wordRows.forEach(row -> row.setSelected(true));
        selectionErrorLabel.setText("");
    }

    @FXML
    public void deselectAllWords() {
        wordRows.forEach(row -> row.setSelected(false));
        selectionErrorLabel.setText("");
    }

    @FXML
    public void startPractice() {
        selectedWords = wordRows.stream()
                .filter(PracticeWordRow::isSelected)
                .map(PracticeWordRow::getWord)
                .collect(Collectors.toCollection(ArrayList::new));

        if (selectedWords.isEmpty()) {
            selectionErrorLabel.setText("Choose at least one word to practice");
            return;
        }

        startPracticeWithSelectedWords();
    }

    @FXML
    public void showOriginalWord() {
        if (shuffledWords.isEmpty()) {
            return;
        }

        StudyWord currentWord = shuffledWords.get(currentIndex);

        String reading = currentWord.getReading();

        if (reading == null || reading.isBlank()) {
            originalLabel.setText(currentWord.getTerm());
        } else {
            originalLabel.setText(currentWord.getTerm() + " — " + reading);
        }

        showOriginalButton.setDisable(true);
    }

    @FXML
    public void nextWord() {
        currentIndex++;

        if (currentIndex >= shuffledWords.size()) {
            showCompletedView();
            return;
        }

        showCurrentPracticeWord();
    }

    @FXML
    public void startAgain() {
        if (selectedWords.isEmpty()) {
            showSelectionView();
            return;
        }

        startPracticeWithSelectedWords();
    }

    @FXML
    public void selectOtherWords() {
        resetPracticeState();
        loadWordSelection();
        showSelectionView();
    }

    private void configureWordsTable() {
        wordsTable.setEditable(true);
        wordsTable.setItems(wordRows);

        selectedColumn.setEditable(true);
        selectedColumn.setCellValueFactory(
                cellData -> cellData.getValue().selectedProperty()
        );
        selectedColumn.setCellFactory(
                CheckBoxTableCell.forTableColumn(selectedColumn)
        );

        termColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getWord().getTerm())
        );

        readingColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getWord().getReading())
        );

        translationColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getWord().getTranslation())
        );
    }

    private void loadWordSelection() {
        wordRows.setAll(
                studyWordService.findWordsForCurrentUser()
                        .stream()
                        .map(PracticeWordRow::new)
                        .toList()
        );

        selectionErrorLabel.setText("");
    }

    private void startPracticeWithSelectedWords() {
        shuffledWords = new ArrayList<>(selectedWords);
        Collections.shuffle(shuffledWords);

        currentIndex = 0;

        showPracticeView();
        showCurrentPracticeWord();
    }

    private void showCurrentPracticeWord() {
        StudyWord currentWord = shuffledWords.get(currentIndex);

        progressLabel.setText((currentIndex + 1) + " / " + shuffledWords.size());
        translationLabel.setText(currentWord.getTranslation());
        originalLabel.setText("");

        showOriginalButton.setDisable(false);
    }

    private void resetPracticeState() {
        selectedWords = new ArrayList<>();
        shuffledWords = new ArrayList<>();
        currentIndex = 0;
        selectionErrorLabel.setText("");
        progressLabel.setText("");
        translationLabel.setText("");
        originalLabel.setText("");
    }

    private void showSelectionView() {
        setVisibleAndManaged(selectionView, true);
        setVisibleAndManaged(practiceView, false);
        setVisibleAndManaged(completedView, false);
    }

    private void showPracticeView() {
        setVisibleAndManaged(selectionView, false);
        setVisibleAndManaged(practiceView, true);
        setVisibleAndManaged(completedView, false);
    }

    private void showCompletedView() {
        setVisibleAndManaged(selectionView, false);
        setVisibleAndManaged(practiceView, false);
        setVisibleAndManaged(completedView, true);
    }

    private void setVisibleAndManaged(VBox view, boolean value) {
        view.setVisible(value);
        view.setManaged(value);
    }

    public static class PracticeWordRow {

        private final StudyWord word;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public PracticeWordRow(StudyWord word) {
            this.word = word;
        }

        public StudyWord getWord() {
            return word;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }
    }
}