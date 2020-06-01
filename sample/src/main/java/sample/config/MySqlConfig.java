package sample.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import data.ChatBotDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

/**
 * Configuration for MySQL DataSource
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
@Configuration
class MySqlConfig {

    @Inject
    Environment env;

    @Bean
    public MysqlDataSource mysqlDataSource() {
        String url = env.getProperty("MYSQL_DB_URL");
        String user = env.getProperty("MYSQL_DB_USER");
        String password = env.getProperty("MYSQL_DB_PASSWORD");
        url = url == null || url.isEmpty() ? "localhost" : url;
        user = user == null || user.isEmpty() ? "root" : user;
        password = password == null || password.isEmpty() ? "password" : password;

        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(url);
        ds.setPassword(password);
        ds.setUser(user);

        return ds;
    }

    @Bean(name = "chatbotds")
    public ChatBotDataSource getChatBotDataSource() {
        return new ChatBotDataSource(mysqlDataSource());
    }
}
