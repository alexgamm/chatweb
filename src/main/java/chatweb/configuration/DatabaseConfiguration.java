package chatweb.configuration;

import chatweb.configuration.properties.DatabaseProperties;
import chatweb.db.Database;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.DriverManager;
import java.sql.SQLException;

@EnableConfigurationProperties(DatabaseProperties.class)
@Configuration
public class DatabaseConfiguration {
    @Bean
    public Database database(DatabaseProperties databaseProperties) throws SQLException {
        return new Database(DriverManager.getConnection(
                databaseProperties.getUrl(),
                databaseProperties.getUser(),
                databaseProperties.getPassword()
        ));
    }
}
