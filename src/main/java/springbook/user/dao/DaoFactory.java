package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() throws ClassNotFoundException {
        /*
        // 생성자를 이용한 주입방식
        return new UserDao(connectionMaker());
        */

        // 수정자를 이용한 주입방식
        /*
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());
        return userDao;
        */

        // DataSource 타입의 빈을 DI 받는 userDao() 빈 정의 메소드
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;

    }

    @Bean
    public AccountDao accountDao() {
        return new AccountDao(connectionMaker());
    }

    @Bean
    public MessageDao messageDao() {
        return new MessageDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
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
