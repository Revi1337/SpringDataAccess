## 트랜잭션 적용 확인

`@Transactional` 을 통한 트랜잭션은 AOP 를 기반으로 동작하기 때문에, 실제 트랜잭션이 적용되고 있는지
아닌지를 확인하기가 어렵다.

```java
// 현재 Thread(ThreadLocal) 에 트랜잭션이 적용되어있는지 확인할 수 있는 기능. 트랜잭션의 적용 여부를 가장 확실하게 알 수 있다.
TransactionSynchronizationManager.isActualTransactionActive();
```

```properties
# 트랜잭션 프록시가 호출하는 트랜잭션의 시작과 종료를 명확하게 로그로 확인 가능
logging.level.org.springframework.transaction.interceptor=TRACE
```
