package access.springdataaccess_1.repository;

import access.springdataaccess_1.domain.Member;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * V3. JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String insertSql = "insert into member(member_id, money) values (?, ?)";

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertSql)
        ) {
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            int resultCount = preparedStatement.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        }
    }

    public Member findById(String memberId) throws SQLException {
        String findSql = "select * from member where member_id = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findSql)
        ) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        }
    }

    /**
     * Connection 을 Repository 게층에서 닫으면 안된다. 그 이유는 Service 계층에 넘길것이기 떄문. (Service 계층에서 닫아주어야함)
     * @param con
     * @param memberId
     * @return
     * @throws SQLException
     */
    public Member findById(Connection con, String memberId) throws SQLException {
        String findSql = "select * from member where member_id = ?";
        try (
                PreparedStatement preparedStatement = con.prepareStatement(findSql)
        ) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        }
    }


    public void update(String memberId, int money) throws SQLException {
        String findSql = "update member set money = ? where member_id = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findSql)
        ) {
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultCount = preparedStatement.executeUpdate();
            log.info("resultCount = {}", resultCount);
        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        }
    }

    /**
     * Connection 을 Repository 게층에서 닫으면 안된다. 그 이유는 Service 계층에 넘길것이기 떄문. (Service 계층에서 닫아주어야함)
     * @param con
     * @param memberId
     * @param money
     * @throws SQLException
     */
    public void update(Connection con, String memberId, int money) throws SQLException {
        String findSql = "update member set money = ? where member_id = ?";
        try (
                PreparedStatement preparedStatement = con.prepareStatement(findSql)
        ) {
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultCount = preparedStatement.executeUpdate();
            log.info("resultCount = {}", resultCount);
        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        }
    }

    public void delete(String memberId) throws SQLException {
        String findSql = "delete member where member_id = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findSql)
        ) {
            preparedStatement.setString(1, memberId);
            int resultCount = preparedStatement.executeUpdate();
            log.info("resultCount = {}", resultCount);
        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();
        log.info("Get Connection = {}, class = {}", connection, connection.getClass());
        return connection;
    }

}
