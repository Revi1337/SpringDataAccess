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

## 트랜잭션 적용 위치

스프링에서 우선순위는 항상 `더 구체적이고 자세한 것이 높은 우선순위를 갖는다.`
따라서 Class 레벨에 `@Transactional(readOnly=true)` 를 달아도, 메서드에 레벨에
`@Transactional` 가 달려있으면 메서드 레벨의 트랜잭션이 사용되게 된다.

```java
// 트랜잭션이 Readonly 인지 확인
TransactionSynchronizationManager.isCurrentTransactionReadOnly();
```

### 인터페이스에 @Transactional 적용

인터페이스에도 @Transactional 을 적용할 수 있다. 이 경우 다음 순서로 적용된다. 구체적인 것이 더 높은 우선순위를 가진다고 생각하면
바로 이해가 될 것이다.

1. 클래스의 메서드 (우선순위가 가장 높다)
2. 클래스 타입
3. 인터페이스의 메서드
4. 인터페이스의 타입 (우선순위가 가장 낮다)