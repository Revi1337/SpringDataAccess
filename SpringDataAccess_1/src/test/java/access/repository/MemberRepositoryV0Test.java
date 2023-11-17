package access.repository;


import access.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

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
        log.info("member == findMember {}", member == findMember);
        log.info("member.equals(findMember) {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        // update money: 10000 -> 20000;
        memberRepositoryV0.update(member.getMemberId(), 20000);
        Member updateMember = memberRepositoryV0.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        // delete
        memberRepositoryV0.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepositoryV0.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }

}