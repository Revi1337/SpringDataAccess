package com.example.springtransaction.propagation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
public class MemberRepository {

    @PersistenceContext EntityManager entityManager;

    @Transactional
    public void save(Member member) {
        log.info("member 저장");
        entityManager.persist(member);
    }

    public Optional<Member> find(String username) {
        return entityManager.createQuery("select m from Member as m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}
