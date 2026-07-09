package dev.cyberjar.springfx.controller;

import dev.cyberjar.springfx.user.CurrentUser;
import dev.cyberjar.springfx.word.StudyWord;
import dev.cyberjar.springfx.word.StudyWordService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class AddWordsController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView bulldog;

    @FXML
    private TextField termField;

    @FXML
    private TextField readingField;

    @FXML
    private TextField translationField;

    @FXML
    private Label addWordErrorLabel;

    @FXML
    private TableView<StudyWord> wordsTable;

    @FXML
    private TableColumn<StudyWord, String> termColumn;

    @FXML
    private TableColumn<StudyWord, String> readingColumn;

    @FXML
    private TableColumn<StudyWord, String> translationColumn;

    private final CurrentUser currentUser;
    private final StudyWordService studyWordService;

    private final StringProperty titleProperty = new SimpleStringProperty();
    private final StringProperty errorProperty = new SimpleStringProperty();

    public AddWordsController(
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

        titleLabel.textProperty().bind(titleProperty);
        titleProperty.setValue("Add words for " + currentUser.getRequired().getUserName());

        addWordErrorLabel.textProperty().bind(errorProperty);

        configureWordsTable();
        refreshWordsTable();
    }

    @FXML
    public void addWord() {
        try {
            studyWordService.addWord(
                    termField.getText(),
                    readingField.getText(),
                    translationField.getText()
            );

            clearForm();
            refreshWordsTable();
            errorProperty.setValue("");

        } catch (IllegalArgumentException exception) {
            errorProperty.setValue(exception.getMessage());
        }
    }

    private void configureWordsTable() {
        termColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTerm())
        );

        readingColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getReading())
        );

        translationColumn.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTranslation())
        );
    }

    private void refreshWordsTable() {
        wordsTable.setItems(
                FXCollections.observableArrayList(
                        studyWordService.findWordsForCurrentUser()
                )
        );
    }

    private void clearForm() {
        termField.clear();
        readingField.clear();
        translationField.clear();
        termField.requestFocus();
    }
}
