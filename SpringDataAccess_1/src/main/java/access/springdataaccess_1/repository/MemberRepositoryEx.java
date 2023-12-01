package access.springdataaccess_1.repository;

import access.springdataaccess_1.domain.Member;

import java.sql.SQLException;

/**
 * 체크예외를 interface 에 정의흘 하게되면 이를 구현받는 클래스들이 특정 기술에 종속적이게 된다.
 */
public interface MemberRepositoryEx {

    Member save(Member member) throws SQLException;

    Member findById(String memberId) throws SQLException;

    void update(String memberId, int money) throws SQLException;

    void delete(String memberId) throws SQLException;

}
