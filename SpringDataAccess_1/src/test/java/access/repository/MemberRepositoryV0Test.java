package access.repository;


import access.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    @DisplayName("CRUD 테스트")
    public void crud() throws SQLException {
        Member member = new Member("memberV1", 10000);
        memberRepositoryV0.save(member);
    }

}