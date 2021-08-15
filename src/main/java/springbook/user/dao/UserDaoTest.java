package springbook.user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        // 스프링 없이 DaoFactory 이용
        /*
        UserDao userDao = new DaoFactory().userDao();
        */

        // 1. 스프링을 이용한 UserDao test
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao userDao = applicationContext.getBean("userDao", UserDao.class);

        // 2. XML을 이용한 UserDao test
        /*
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao userDao = context.getBean("userDao", UserDao.class);
        */
        User user = new User();
        user.setId("horany14");
        user.setName("영란14");
        user.setPassword("eodfks09");
        userDao.add(user);
        System.out.println(user.getId() + " 등록 성공");

        User user2 = userDao.get(user.getId());
        if (!user.getName().equals(user2.getName())) {
            System.out.println("테스트 실패(name)");
        } else if (!user.getPassword().equals(user2.getPassword())) {
            System.out.println("테스트 실패(password)");
        } else {
            System.out.println("조회 테스트 성공");
        }

        // 오브젝트의 동일성과 동등성 테스트
        /*
        DaoFactory daoFactory = new DaoFactory();
        UserDao userDao1 = daoFactory.userDao();
        UserDao userDao2 = daoFactory.userDao();

        System.out.println(userDao1);
        System.out.println(userDao2);

        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao userDao3 = context.getBean("userDao", UserDao.class);
        UserDao userDao4 = context.getBean("userDao", UserDao.class);

        System.out.println(userDao3);
        System.out.println(userDao4);
        */
    }


}
