package dev.cyberjar.springfx.controller;


import dev.cyberjar.springfx.config.FxmlView;
import dev.cyberjar.springfx.config.StageManager;
import dev.cyberjar.springfx.exception.ConstraintsException;
import dev.cyberjar.springfx.exception.NotFoundException;
import dev.cyberjar.springfx.exception.UserExistsException;
import dev.cyberjar.springfx.user.CurrentUser;
import dev.cyberjar.springfx.user.User;
import dev.cyberjar.springfx.user.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Lazy
@Component
public class LoginController implements Initializable {

    @FXML
    private Button signInButton;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField userName;

    @FXML
    private Label loginErrorLabel;

    StringProperty errorProperty = new SimpleStringProperty();

    @FXML
    private ImageView bulldog;

    private final StageManager stageManager;

    private final UserService userService;

    private final CurrentUser currentUser;

  //  @Lazy
    public LoginController(StageManager stageManager, UserService userService, CurrentUser currentUser) {
        this.stageManager = stageManager;
        this.userService = userService;
        this.currentUser = currentUser;
    }


    public void loadUserAndOpenHomePage() {
        Optional<User> user = userService.findByUsername(userName.getText().trim());
        if (user.isEmpty()) {
            errorProperty.setValue(
                    "No user with this name found.");
            throw new NotFoundException(errorProperty.getValue());
        }

        currentUser.set(user.get());
        stageManager.switchToNextScene(FxmlView.HOME);

    }


    public void saveUserAndOpenHomePage() {

        String name = userName.getText().trim();

        if (name.length() > 20 || name.isBlank()) {
            errorProperty.setValue(
                    "Username must not be blank or longer that 20 characters");
            throw new ConstraintsException(errorProperty.getValue());
        }

        Optional<User> user = userService.findByUsername(name);
        if (user.isPresent()) {
            errorProperty.setValue(
                    "User with this name already exists! Sign in or use another name.");
            throw new UserExistsException(errorProperty.getValue());
        }

        User savedUser = userService.saveUser(name);
        currentUser.set(savedUser);
        stageManager.switchToNextScene(FxmlView.HOME);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bulldog.setFitWidth(1000);
        bulldog.setPreserveRatio(true);
        bulldog.setSmooth(true);

        loginErrorLabel.textProperty().bind(errorProperty);

    }
}
