package com.example.springtransaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @TestConfiguration
    static class Config {

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    public void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        platformTransactionManager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    public void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        platformTransactionManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    public void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        platformTransactionManager.commit(status);

        log.info("트랜잭션2 시작");
        TransactionStatus status2 = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        platformTransactionManager.commit(status2);
    }

    @Test
    public void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        platformTransactionManager.commit(status);

        log.info("트랜잭션2 시작");
        TransactionStatus status2 = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        platformTransactionManager.rollback(status2);
    }

}
