package dev.cyberjar.springfx;

import dev.cyberjar.springfx.config.HibernateRuntimeHints;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(HibernateRuntimeHints.class)
public class SpringfxApplication {

    private static final String AOT_PROCESSING_PROPERTY = "springfx.aot.processing";

	public static void main(String[] args) {
        if (Boolean.getBoolean(AOT_PROCESSING_PROPERTY)) {
            new SpringApplicationBuilder(SpringfxApplication.class)
                    .main(SpringfxApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
            return;
        }

        Application.launch(LexiDrillFxApplication.class, args);
    }

}
