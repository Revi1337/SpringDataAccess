package access.springdataaccess_1.repository;

import access.springdataaccess_1.domain.Member;


public interface MemberRepository {

    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);

}
