package springbook.user.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {

    //    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
//        this.jdbcContext = new JdbcContext();
//        this.jdbcContext.setDataSource(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        this.dataSource = dataSource;
    }

    private JdbcContext jdbcContext;

// 임시 DBConnector
//    private ConnectionMaker connectionMaker;

    /*
    // 1. 생성자를 이용한 의존관계 주입
    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
    */


    // 2. 수정자(setter)를 이용한 의존관계 주입
    /*
    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
    */

    public void add(final User user) throws SQLException {
        // 전략패턴 사용시 문제점
        // 1. DAO 메소드마다 새로운 StatementStrategy 구현 클래스를 만들어야 한다.
        // 2. DAO 메소드에서 StatementStrategy에 전달할 User와 같은 부가적인 정보가 있는 경우,
        //    이를 위해 오브젝트를 전달받는 생성자와 이를 저장해둘 인스턴스 변수를 번거롭게 만들어야 한다.
        // 해결방법 1: 내부 로컬클래스
        /*
        class AddStatement implements StatementStrategy {

            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) value(?,?,?)");

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        }
        StatementStrategy statementStrategy = new AddStatement();
        jdbcContextWithStatementStrategy(statementStrategy);

        */
        // 해결방법 2: 내부 익명클래스
        /*
        this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) value(?,?,?)");

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        });
        */

        //this.jdbcContext.executeSetSql("insert into users(id, name, password) value(?,?,?)", user);
        this.jdbcTemplate.update("insert into users(id, name, password) value(?,?,?)", user.getId(), user.getName(), user.getPassword());


    }

    public User get(String id) throws SQLException {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id}, this.userRowMapper);

        /*
        Connection c = dataSource.getConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) {
            throw new EmptyResultDataAccessException(1);
        }

        return user;
        */
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userRowMapper);
    }

    /**
     * deleteAll() 컨텍스트 정리
     * DB커넥션 가져오기
     * PreparedStatement를 만들어줄 외부기능 호출하기 --> 전략패턴의 전략
     * 전달받은 PreparedStatement 실행하기
     * 예외가 발생하면 이를 다시 메소드 밖으로 던지기
     * 모든 경우에 만들어진 PreparedStatement와 Connection을 적절히 닫아주기
     */
    public void deleteAll() throws SQLException {
        this.jdbcTemplate.update("delete from users");
//        this.jdbcContext.executeSql("delete from users");

        /*
        StatementStrategy statementStrategy = new DeleteAllStatement(); // 선정한 전략 클래스의 오브젝트 생성
        jdbcContextWithStatementStrategy(statementStrategy);    //컨텍스트 호출, 전략 오브젝트 전달
        */

        // 내부 익명클래스
        /*
        jdbcContextWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("delete from users");
                return ps;
            }
        });
        */
    }

    // 템플릿 메소드 패턴을 적용 -> userDao 역시 abstract 이 되어야함
//    abstract protected PreparedStatement makeStatement(Connection c) throws SQLException;

    private PreparedStatement makeStatement(Connection c) throws SQLException {
        PreparedStatement ps = null;
        ps = c.prepareStatement("delete from users");
        return ps;
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);

        /*
        return this.jdbcTemplate.query("select count(*) from users", rs -> {
            rs.next();
            return rs.getInt(1);
        });
        */

        /*
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                }
            }

            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {

                }
            }
        }
        */
    }

    /**
     * 재사용 가능하도록 독립시킨 RowMapper
     */
    private RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    };


    /**
     * JdbcTemplate을 이용해 만든 getCount()
     */
    public int getCountWithJdbcTemplate() {
        return this.jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("select count(*) from users");
            }
        }, new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1);
            }
        });
    }

    // 메소드를 클래스로 분리
    /*
    public void jdbcContextWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = statementStrategy.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    */

    //2.. 추상메소드로 구현한 소스
//    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;

    // 1. DB Connection 을 직접 구현한 소스
    /*
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook?serverTimezone=UTC", "youngran", "eodfks09");
        return c;
    }
    */

}
