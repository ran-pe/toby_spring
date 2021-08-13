package springbook.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao userDao = context.getBean("userDao", UserDao.class);

        User user = userDao.get("horany");
        System.out.println(user.getName());
        System.out.println(user.getPassword());
        System.out.println(user.getId() + " 조회 성공");

        CountingConnectionMaker connectionMaker = context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("Connection counter : " + connectionMaker.getCounter());

    }
}
