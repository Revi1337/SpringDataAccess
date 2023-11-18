package access.repository;

import access.domain.Member;
import access.springdataaccess_1.connection.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * V1. JDBC - DataSource 사용, JdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
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
