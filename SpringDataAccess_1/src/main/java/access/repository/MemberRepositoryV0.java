package access.repository;

import access.domain.Member;
import access.springdataaccess_1.connection.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * V0. JDBC - DriverManager 를 사용한 DB 저장 (Low Level)
 */
@Slf4j
public class MemberRepositoryV0 {

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

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

}
