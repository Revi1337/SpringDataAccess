package access.repository;

import access.domain.Member;
import access.springdataaccess_1.connection.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * V0. JDBC - DriverManager 를 사용한 DB 저장 (Low Level)
 */
@Slf4j
public class MemberRepositoryV0 {

    /**
     * 데이터 변경은 executeUpdate()
     * @param member
     * @return
     * @throws SQLException
     */
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

    /**
     * 데이터 조회는 executeQuery() --> 결과는 ResultSet
     *
     * ResultSet 은 테이블과 같은 구조. (row, column 의 집합)
     * ResultSet 에서 데이터를 얻어올려면 매번 .next() 를 호출하여 row 가 있냐? 라고 물어봐야함. (true 있음. false 없음)
     * getString(column_name), getInt(column_name) 로 컬럼값을 가져올 수 있다.
     *
     * @param memberId
     * @return
     */
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

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

}
