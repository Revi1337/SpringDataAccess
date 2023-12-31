package com.example.springtransaction.propagation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
public class LogRepository {

    @PersistenceContext EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Log logMessage) {
        log.info("log 저장");
        entityManager.persist(logMessage);

        if (logMessage.getMessage().contains("로그예외")) {
            log.info("log 저장시 예외 발생");
            throw new RuntimeException("예외 발생");
        }
    }

    public Optional<Log> find(String message) {
        return entityManager.createQuery("select l from Log as l where l.message = :message", Log.class)
                .setParameter("message", message)
                .getResultList()
                .stream()
                .findFirst();
    }

}
