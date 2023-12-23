package com.example.springtransaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService        @Transactional : OFF
     * memberRepository     @Transactional : ON
     * logRepository        @Transactional : ON
     */
    @Test
    public void outerTxOff_success() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * memberService        @Transactional : OFF
     * memberRepository     @Transactional : ON
     * logRepository        @Transactional : ON Exception
     */
    @Test
    public void outerTxOff_fail() {
        String username = "로그예외_outerTxOff_fail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isFalse();
    }

    /**
     * memberService        @Transactional : ON
     * memberRepository     @Transactional : OFF
     * logRepository        @Transactional : OFF
     */
    @Test
    public void singleTx() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * memberService        @Transactional : ON
     * memberRepository     @Transactional : ON
     * logRepository        @Transactional : ON
     */
    @Test
    public void outerTxOn_success() {
        String username = "outerTxOn_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * memberService        @Transactional : ON
     * memberRepository     @Transactional : ON
     * logRepository        @Transactional : ON Exception
     */
    @Test
    public void outerTxOn_fail() {
        String username = "로그예외_outerTxOn_fail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // 모든 데이터가 롤백된다.
        assertThat(memberRepository.find(username).isPresent()).isFalse();
        assertThat(logRepository.find(username).isPresent()).isFalse();
    }

}
