package dev.cyberjar.springfx;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringfxApplication {

	public static void main(String[] args) {
        Application.launch(LexiDrillFxApplication.class, args);
	}

}
