package dev.cyberjar.springfx.controller;

import dev.cyberjar.springfx.config.FxmlView;
import dev.cyberjar.springfx.config.StageManager;
import dev.cyberjar.springfx.user.CurrentUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class HomeController implements Initializable {

    @FXML
    private Button wordsButton;

    @FXML
    private Button practiceButton;

    @FXML
    private Label helloLabel;

    @FXML
    private ImageView bulldog;

    private final StageManager stageManager;

    StringProperty nameProperty = new SimpleStringProperty();

    private final CurrentUser currentUser;

    @Lazy
    public HomeController(StageManager stageManager, CurrentUser currentUser) {
        this.stageManager = stageManager;
        this.currentUser = currentUser;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bulldog.setFitWidth(1000);
        bulldog.setPreserveRatio(true);
        bulldog.setSmooth(true);

        helloLabel.textProperty().bind(nameProperty);
        nameProperty.setValue("Hello, " + currentUser.getRequired().getUserName() + "!");

    }

    @FXML
    void addWords() {
        stageManager.switchToNextScene(FxmlView.ADD_WORDS);
    }

    @FXML
    public void practiceWords() {
        stageManager.switchToNextScene(FxmlView.PRACTICE_WORDS);
    }

}
