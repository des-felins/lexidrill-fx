package dev.cyberjar.springfx;


import dev.cyberjar.springfx.config.FxmlView;
import dev.cyberjar.springfx.config.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class LexiDrillFxApplication extends Application {


    private static Stage stage;

    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(SpringfxApplication.class).run();
    }

    @Override
    public void stop() {
        applicationContext.close();
        stage.close();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stageManager = applicationContext.getBean(StageManager.class, primaryStage);
        showLoginScene();
    }

    private void showLoginScene() {
        stageManager.switchScene(FxmlView.LOGIN);
    }

}
