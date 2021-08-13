package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class CountingDaoFactory {

    @Bean
    public UserDao userDao() throws ClassNotFoundException {
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new DConnectionMaker();
    }

    @Bean
    public DataSource dataSource() throws ClassNotFoundException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
        dataSource.setDriverClass(driverClass);
        dataSource.setUrl("jdbc:mysql://localhost/springbook?serverTimezone=UTC");
        dataSource.setUsername("youngran");
        dataSource.setPassword("eodfks09");

        return dataSource;
    }
}
