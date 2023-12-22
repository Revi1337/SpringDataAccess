package com.example.springtransaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    
     @Test
     @DisplayName("외부 트랜잭션이 수행중인데, 내부 트랜잭션을 추가로 수행 --> 내부 트랜잭션은 외부 트랜잭션에 참여.")
     public void inner_commit() {
         log.info("외부 트랜잭션 시작");
         TransactionStatus outer = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
         log.info("outer.isNewTransaction() = {}", outer.isNewTransaction()); // 처음 수행된 트랜잭션이냐?

         log.info("내부 트랜잭션 시작");
         TransactionStatus inner = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
         log.info("inner.isNewTransaction() = {}", inner.isNewTransaction()); // 처음 수행된 트랜잭션이냐?
         log.info("내부 트랜잭션 커밋");
         platformTransactionManager.commit(inner);

         log.info("외부 트랜잭션 커밋");
         platformTransactionManager.commit(outer);
     }

    @Test
    @DisplayName("외부트랜잭션 롤백과 내부트랜잭션 커밋")
    public void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction()); // 처음 수행된 트랜잭션이냐?

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}", inner.isNewTransaction()); // 처음 수행된 트랜잭션이냐?
        log.info("내부 트랜잭션 커밋");
        platformTransactionManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        platformTransactionManager.rollback(outer);
    }

    @Test
    @DisplayName("외부트랜잭션 커밋과 내부트랜잭션 롤백")
    public void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction()); // 처음 수행된 트랜잭션이냐?

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}", inner.isNewTransaction()); // 처음 수행된 트랜잭션이냐?
        log.info("내부 트랜잭션 롤백");
        platformTransactionManager.rollback(inner); // Participating transaction failed - marking existing transaction as rollback-only --> 내부 트랜잭션을 롤백하면 실제 물리 트랜잭션은 롤백하지 않는다. 대신에 기존 트랜잭션을 rollback-only 를 표시한다.
                                                    // Setting JDBC transaction [HikariProxyConnection@1926138523 wrapping conn0: url=jdbc:h2:mem:78a8e7d6-eebc-4461-bfe7-8e561b003506 user=SA] rollback-only
        log.info("외부 트랜잭션 커밋");
        assertThatThrownBy(() -> platformTransactionManager.commit(outer)) // Global transaction is marked as rollback-only but transactional code requested commit
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    @DisplayName("외부트랜잭션 커밋과 내부트랜잭션 롤백 --> REQUIRES_NEW 로 기존의 물리 트랜잭션이 있어도 새로운 트랜잭션을 생성")
    public void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);  // 트랜잭션 전파 옵션을 REQUIRES_NEW 로 설정 --> 기존 물리 트랜잭션이 존재해도 새로운 물리 트랜잭션을 만든다.
        TransactionStatus inner = platformTransactionManager.getTransaction(definition);    // Suspending current transaction, creating new transaction with name [null]
        log.info("inner.isNewTransaction() = {}", inner.isNewTransaction());

        log.info("내부 트랜잭션 롤백");
        platformTransactionManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        platformTransactionManager.commit(outer);                                           // Resuming suspended transaction after completion of inner transaction
    }

}
