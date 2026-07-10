package dev.cyberjar.springfx.config;


import org.hibernate.cfg.AvailableSettings;
import org.hibernate.community.dialect.SQLiteDialect;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer sqliteDialectCustomizer() {
        return hibernateProperties ->
                hibernateProperties.put(AvailableSettings.DIALECT, SQLiteDialect.class);
    }

}
