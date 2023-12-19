package com.example.springtransaction.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * logging.level.org.springframework.transaction.interceptor=TRACE
 * logging.level.org.springframework.jdbc.datasource.DataSourceTransactionManager=DEBUG --> (jdbc) 현재사용되는 트랜잭션 매니저 로그 (commit, rollback 확인용)
 *
 * # JPA Log
 * logging.level.org.springframework.orm.jpa.JpaTransactionManager=DEBUG --> (JPA) 현재사용되는 트랜잭션 매니저 로그 (commit, rollback 확인용)
 * logging.level.org.hibernate.resource.transaction=DEBUG --> 보류
 */
@SpringBootTest
public class RollbackTest {

    @Autowired RollbackService rollbackService;

    @Test
    @DisplayName("RuntimeException 은 Rollback (언체크 예외) --> Initiating transaction rollback")
    public void runtimeException() {
        assertThatThrownBy(() -> rollbackService.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Exception 은 Commit (체크 예외) --> Initiating transaction commit")
    public void checkedException() {
        assertThatThrownBy(() -> rollbackService.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    @DisplayName("체크 예외이기 때문에 원래라면 Commit 이지만, rollbackFor 옵션으로 인해 Rollback  --> Initiating transaction rollback")
    public void rollbackFor() {
        assertThatThrownBy(() -> rollbackService.rollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        // 런타임 예외 발생 : 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생 : 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        // 체크 예외 rollbackFor 지정 : 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
        }
    }

    static class MyException extends Exception {
    }

}
