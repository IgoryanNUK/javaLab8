package app.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("app.server")
@PropertySource("classpath:flyway.properties")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {
    @Bean
    public DataSource dataSource(@Value("${flyway.url}") String url, @Value("${flyway.user}") String user, @Value("${flyway.password}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        return dataSource;
    }

}
