package access.springdataaccess_1.repository;


import access.springdataaccess_1.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static access.springdataaccess_1.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach
    public void beforeEach() {
        // 기본 DriverManger - 항상 새로운 커넥션을 획득 (성능 저하)
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
//        this.memberRepositoryV1 = new MemberRepositoryV1(driverManagerDataSource);

        // HikariCP 커넥션 풀링 - close() 시 커넥션을 닫는것이 아니라 커넥션을 풀에 다시 반환함.
        // 따라서 만들어진 커넥션이 실행되는것이 아니라, 반환된 커넥션이 재사용된다. (멀티스레드 환경에서 동작하면 풀에서 새로운 커넥션을 꺼내서 사용한다.)
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(URL);
        hikariDataSource.setUsername(USER);
        hikariDataSource.setPassword(PASSWORD);
        hikariDataSource.setPoolName("POOL-NAME");
        this.memberRepositoryV1 = new MemberRepositoryV1(hikariDataSource);
    }

    @Test
    @DisplayName("CRUD 테스트")
    public void crud() throws SQLException {
        // create
        Member member = new Member("memberV1", 10000);
        memberRepositoryV1.save(member);

        // read
        Member findMember = memberRepositoryV1.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member == findMember {}", member == findMember);
        log.info("member.equals(findMember) {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        // update money: 10000 -> 20000;
        memberRepositoryV1.update(member.getMemberId(), 20000);
        Member updateMember = memberRepositoryV1.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        // delete
        memberRepositoryV1.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepositoryV1.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }

}