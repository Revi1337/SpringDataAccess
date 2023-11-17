package access.repository;


import access.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    @DisplayName("CRUD 테스트")
    public void crud() throws SQLException {
        // create
        Member member = new Member("memberV1", 10000);
        memberRepositoryV0.save(member);

        // read
        Member findMember = memberRepositoryV0.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member == findMembe {}", member == findMember);
        log.info("member.equals(findMembe) {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);
    }

}