package access.service;

import access.domain.Member;
import access.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, pool 을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);    // 트랜잭션 시작

            // ============== 비지니스 로직을 수행 ==============
            bizLogic(connection, fromId, toId, money);
            // ==============================================

            connection.commit();                // 성공 시 커밋
        } catch (Exception e) {
            connection.rollback();              // 실패 시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
    }

    private void release(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true); // Connection Pool 고려하여 커넥션을 반납하기전에 자동커밋모드로 설정.
                connection.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void bizLogic(Connection connection, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(connection, fromId);
        Member toMember = memberRepository.findById(connection, toId);

        memberRepository.update(connection, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
