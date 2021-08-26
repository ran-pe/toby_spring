package springbook.user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.util.List;

public class UserServiceImpl implements UserService{
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    /**
     * 사용자 레벨 업그레이드 메소드
     * 사용자의 레벨은 BASIC, SILVER, GOLD 세가지중 하나다.
     * 사용자가 처음 가입하면 BASIC 레벨이 되며, 이후 활동에 따라서 한단계씩 업그레이드될 수 있다.
     * 가입 후 50회 이상 로그인을 하면 BASIC에서 SILVER 레벨이 된다.
     * SILVER 레벨이면서 30번 이상 추천을 받으면 GOLD 레벨이 된다.
     * 사용자 레벨의 변경작업은 일정한 주기를 가지고 일괄적으로 진행된다. 변경 작업 전에는 조건을 충족하더라도 레벨의 변경이 일어나지 않는다.
     */

    /**
     * 트랜잭션 동기화 방식을 적용한 UserService
     * @throws Exception
     */
    /*
    public void upgradeLevels() throws Exception {
        // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
        //DB커넥션을 생성하고 트랙잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
        TransactionSynchronizationManager.initSynchronization();

        //DB커넥션 생성과 동기화를 함께 해주는 유틸리티 메소드
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> userList = userDao.getAll();
            for (User user : userList) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        }finally {
            // 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
    */

    /**
     * 스프링의 트랜잭션 추상화 API를 적용한 upgradeLevels
     *
     * @throws Exception
     */
    public void upgradeLevels() {
        List<User> userList = userDao.getAll();
        for (User user : userList) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    public void upgradeLevel(User user) {
        // 간결해진 upgradeLevel
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    /**
     * 스프링의 MailSender를 이요한 메일 발송 메소드
     *
     * @param user
     */
    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@sug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다");

        this.mailSender.send(mailMessage);
    }

    /**
     * 업그레이드 가능 확인 메소드
     *
     * @param user
     * @return
     */
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC:
                return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }
    /* 리펙토링전 코드
    public void upgradeLevels() {
        List<User> userList = userDao.getAll();
        for (User user : userList) {
            Boolean changed = null;
            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                user.setLevel(Level.SILVER);
                changed = true;
            } else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
                user.setLevel(Level.GOLD);
                changed = true;
            } else if (user.getLevel() == Level.GOLD) {
                changed = false;
            } else {
                changed = false;
            }

            if (changed) {
                userDao.update(user);
            }
        }
    }
    */

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

}
