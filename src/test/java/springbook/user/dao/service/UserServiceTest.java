package springbook.user.dao.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.MockUserDao;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.*;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static springbook.user.service.UserLevelUpgrade.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserLevelUpgrade.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserDao userDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    List<User> userList;

    @Autowired
    MailSender mailSender;

    @Autowired
    ApplicationContext context;

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Before
    public void setUp() {
        userList = Arrays.asList(
                new User("id1", "베이직", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "id1@mail.com"),
                new User("id2", "실버될베이직", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "id2@mail.com"),
                new User("id3", "실버", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "id3@mail.com"),
                new User("id4", "골드될실버", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "id4@mail.com"),
                new User("id5", "골드", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "id5@mail.com")
        );
    }

    /**
     * 사용자 레벨 업그레이드 테스트
     */
    /*
    @Test
    public void upgradeLevels() {
        userDao.deleteAll();

        for (User user : userList) {
            userDao.add(user);
        }

        userService.upgradeLevels();

        checkLevel(userList.get(0), Level.BASIC);
        checkLevel(userList.get(1), Level.SILVER);
        checkLevel(userList.get(2), Level.SILVER);
        checkLevel(userList.get(3), Level.GOLD);
        checkLevel(userList.get(4), Level.GOLD);
    }
    */


    /**
     * 6-13MockUserDao를 사용해서 만든 고립된 테스트
     * @throws Exception
     */
    @Test
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.userList);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "id2", Level.SILVER);
        checkUserAndLevel(updated.get(1), "id4", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(userList.get(1).getEmail()));
        assertThat(request.get(1), is(userList.get(3).getEmail()));
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }


    /**
     * 개선한 upgradeLevels 테스트
     */
    /*
    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();

        for (User user : userList) {
            userDao.add(user);
        }

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevelUpgraded(userList.get(0), false);
        checkLevelUpgraded(userList.get(1), true);
        checkLevelUpgraded(userList.get(2), false);
        checkLevelUpgraded(userList.get(3), true);
        checkLevelUpgraded(userList.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(userList.get(1).getEmail()));
        assertThat(request.get(1), is(userList.get(3).getEmail()));
    }
    */
    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    /**
     * add() 메소드의 테스트
     */
    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = userList.get(4);
        User userWithoutLevel = userList.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(userList.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);

        ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);  //팩토리빈 자체를 가져와야 하므로 빈 이름에 &를 반드시 넣어야 한다.
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for (User user : userList) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }
        checkLevelUpgraded(userList.get(1), false);

    }

    /**
     * Mockito를 적용한 테스트 코드
     */
    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.userList);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(userList.get(1));
        assertThat(userList.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(userList.get(3));
        assertThat(userList.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(userList.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(userList.get(3).getEmail()));

    }
}
