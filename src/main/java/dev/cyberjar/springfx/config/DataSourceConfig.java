package dev.cyberjar.springfx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Configuration
public class DataSourceConfig {

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }


    @Bean
    public DataSource getDataSource() {
        Path appDir = Path.of(System.getProperty("user.home"), ".lexidrill");

        try {
            Files.createDirectories(appDir);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create app data directory", exception);
        }

        Path databasePath = appDir.resolve("words.db");

        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("driverClassName")));
        dataSource.setUrl("jdbc:sqlite:" + databasePath.toAbsolutePath());
        dataSource.setUsername(env.getProperty("username"));
        dataSource.setPassword(env.getProperty("password"));
        return dataSource;
    }

}
