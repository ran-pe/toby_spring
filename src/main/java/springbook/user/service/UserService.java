package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 사용자 레벨 업그레이드 메소드
     * 사용자의 레벨은 BASIC, SILVER, GOLD 세가지중 하나다.
     * 사용자가 처음 가입하면 BASIC 레벨이 되며, 이후 활동에 따라서 한단계씩 업그레이드될 수 있다.
     * 가입 후 50회 이상 로그인을 하면 BASIC에서 SILVER 레벨이 된다.
     * SILVER 레벨이면서 30번 이상 추천을 받으면 GOLD 레벨이 된다.
     * 사용자 레벨의 변경작업은 일정한 주기를 가지고 일괄적으로 진행된다. 변경 작업 전에는 조건을 충족하더라도 레벨의 변경이 일어나지 않는다.
     */

    public void upgradeLevels() {
        List<User> userList = userDao.getAll();
        for (User user : userList) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    private void upgradeLevel(User user) {
        if(user.getLevel() == Level.BASIC) {
            user.setLevel(Level.SILVER);
        } else if(user.getLevel() == Level.SILVER) {
            user.setLevel(Level.GOLD);
        }
        userDao.update(user);
    }

    /**
     * 업그레이드 가능 확인 메소드
     *
     * @param user
     * @return
     */
    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC:
                return (user.getLogin() >= 50);
            case SILVER:
                return (user.getRecommend() >= 30);
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
