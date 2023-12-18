package com.example.springtransaction.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    public void printProxy() {
        log.info("callService class = {}", callService.getClass());
    }

    @Test
    @DisplayName("외부에서 @Transactional 이 달린 메서드를 호출하면 프록시를 통해 트랜잭션이 잘 동작한다.")
    public void internalCall() {
        callService.internal();
    }
    
    @Test
    @DisplayName("""
    하지만, @Transactional 가 달리지 않은 메서드에서 @Transactional 가 달린 메서드를 내부에서 호출하게 되면, 
    @Transactional 이 달린 메서드의 트랜잭션이 동작하지 않는다. --> 굉장히 주의
    """)
    public void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        public CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService {

        public void external() {
            log.info("call external");
            printTxInfo();
            internal();
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);
        }
    }


}
