package app.server;

import app.server.config.ApplicationConfiguration;
import app.server.config.FlywayConfig;
import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class,
                FlywayConfig.class);

        Flyway f = context.getBean(Flyway.class);
        System.out.println(f.info());
        Server server = context.getBean(Server.class);
        server.run();
    }
}
