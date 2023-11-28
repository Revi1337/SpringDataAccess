package access.springdataaccess_1.service;


import access.springdataaccess_1.domain.Member;
import access.springdataaccess_1.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static access.springdataaccess_1.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - DataSource TransactionManager 자동 등록
 */
@Slf4j
@SpringBootTest
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired MemberRepositoryV3 memberRepository;
    @Autowired MemberServiceV3_3 memberService;

    /**
     * @TestConfiruation : 테스트 안에서 내부 설정 클래스를 만들어서 사용하면서 이 어노테이션을 붙이면, 스프링 부트가 자동으로 만들어주는 Bean 들에 추가로 필요한 스프링 빈들을 등록하고 테스트를 수행할 수 있다.
     */
    @TestConfiguration
    @RequiredArgsConstructor
    static class TestBeanConfiguration {

        private final DataSource dataSource;

        @Bean
        public MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        public MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }

    @AfterEach
    public void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("@Transactional 이 달린 Service 에 프록시가 적용됨을 확인")
    public void aopTest() {
        log.info("memberService = {}", this.memberService.getClass());
        log.info("memberRepository = {}", this.memberRepository.getClass());

        assertThat(AopUtils.isAopProxy(this.memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(this.memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws SQLException {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        log.info("START TX");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("END TX");

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생 - 트랜잭션으로 인해 memberA 의 까진 돈이 다시 Rollback 된다.")
    public void accountTransferEx() throws SQLException {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }

}