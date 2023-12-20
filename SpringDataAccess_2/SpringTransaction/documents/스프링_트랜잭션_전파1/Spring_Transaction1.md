## 스프링 트랜잭션 전파 - 트랜잭션 한번 사용

### Commit 

```java
@Test
public void commit() {
    log.info("트랜잭션 시작");
    TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());

    log.info("트랜잭션 커밋 시작");
    platformTransactionManager.commit(status);
    log.info("트랜잭션 커밋 완료");
}
```

```text
o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1203852392 wrapping conn0: url=jdbc:h2:mem:9532ff42-3551-4dfe-a4b7-50a15137f91e user=SA] for JDBC transaction
o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1203852392 wrapping conn0: url=jdbc:h2:mem:9532ff42-3551-4dfe-a4b7-50a15137f91e user=SA] to manual commit
c.e.s.propagation.BasicTxTest            : 트랜잭션 커밋 시작
o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@1203852392 wrapping conn0: url=jdbc:h2:mem:9532ff42-3551-4dfe-a4b7-50a15137f91e user=SA]
o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1203852392 wrapping conn0: url=jdbc:h2:mem:9532ff42-3551-4dfe-a4b7-50a15137f91e user=SA] after transaction
c.e.s.propagation.BasicTxTest            : 트랜잭션 커밋 완료
```

### Rollback

```java
@Test
public void rollback() {
    log.info("트랜잭션 시작");
    TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());

    log.info("트랜잭션 롤백 시작");
    platformTransactionManager.rollback(status);
    log.info("트랜잭션 롤백 완료");
}
```

```text
c.e.s.propagation.BasicTxTest            : 트랜잭션 시작
o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@785140711 wrapping conn0: url=jdbc:h2:mem:0baf9460-f077-442e-af99-1afc8ab1f3eb user=SA] for JDBC transaction
o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@785140711 wrapping conn0: url=jdbc:h2:mem:0baf9460-f077-442e-af99-1afc8ab1f3eb user=SA] to manual commit
c.e.s.propagation.BasicTxTest            : 트랜잭션 롤백 시작
o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@785140711 wrapping conn0: url=jdbc:h2:mem:0baf9460-f077-442e-af99-1afc8ab1f3eb user=SA]
o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@785140711 wrapping conn0: url=jdbc:h2:mem:0baf9460-f077-442e-af99-1afc8ab1f3eb user=SA] after transaction
c.e.s.propagation.BasicTxTest            : 트랜잭션 롤백 완료
```



