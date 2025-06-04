package app.server.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Configuration
@PropertySource("classpath:flyway.properties")
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource, @Value("${flyway.locations}") String loc) {
        Flyway flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .locations(loc)
                .schemas("public")
                .cleanDisabled(false)
                .load();

        System.out.println(flyway.getConfiguration().getLocations()[0]);
        System.out.println("Flyway migrations to apply: " + Arrays.toString(flyway.info().pending()));

        System.out.println(flyway.migrate().migrationsExecuted);
        return flyway;
    }
}
