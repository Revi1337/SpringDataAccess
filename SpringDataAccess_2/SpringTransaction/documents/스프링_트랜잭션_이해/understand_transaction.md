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

## 트랜잭션 AOP 주의사항 - 프록시 내부 호출

@Transactionl 을 사용하면 스프링의 트랜잭션 AOP 가 적용된다.
기본적으로 프록시 방식의 AOP 를 사용하기 때문에 프록시객체가 요청을 먼저 받아 트랜잭션을 처리하고
실제 객체를 호출해준다. 따라서 트랜잭션을 적용하려면 항상 프록시를 통해서 대상 객체를 호출하게 된다.
만약 프록시를 거치지 않고 대상 객체를 직접 호출하게 되면 AOP 가 적용되지 않고, 트랜잭션도 적용되지 않는다.

AOP 를 적용하면 스프링은 대상 객체 대신에 프록시를 스프링 Bean 으로 등록한다. 따라서 스프링은 의존관계 주입시에
항상 실제 객체 대신에 프록시 객체를 주입한다. 프록시 객체가 주입되기 떄문에 대상 객체를 직접 호출하는 문제는 일반적으로 발생하지 않는다.
하지만, `대상 객체의 내부에서 메서드 호출이 발생하면 프록시를 거치지 않고 대상 객체를 직접 호출하는 문제가 발생`한다. 이렇게 되면
`@Transactional`이 있어도 트랜잭션이 적용되지 않는다. 실무에서 반드시 한번은 만나서 고생하는 문제이기 때문에 꼭 이해하고 넘어가자.

> 기본적으로 메서드 앞에는 this 가 붙기때문에, 메서드 내부에서 호출된 다른 메서드는
프록시 객체에서 호출되는 것이 아닌, 실제 객체에서 호출이 되게된다. 따라서 트랜잭션이 먹히지 않는것이다.

### 해결방법

1. 첫번째 가장 단순한 해결방법은 메서드 안에서 호출되는 `다른 메서드` 를 별도의 클래스로 분리하는 것이다.
   (이 방법을 실무에서 많이 사용한다.)

![img.png](img.png)

> 참고로 스프링 트랜잭션 AOP 는 `public` 메서드에만 트랜잭션을 적용하도록 기본 설정이 되어있다.
따라서 protected, private, package-visible 범위에는 트랜잭션이 적용되지 않는다. (예외는 아니고 무시된다.)
프록시의 내부호출문제와는 무관하며, 그냥 스프링이 막아둔 것이다.

## 트랜잭션 AOP 주의사항 - 초기화 시점

스프링 초기화 시점에는 트랜잭션 AOP 가 적용되지 않을 수 있다.

- @PostConstruct 와 @Transactional 을 함께 사용하면 트랜잭션이 적용되지 않는다.
왜냐하면 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP 가 적용되기 때문이다. 따라서 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 없다. (순서 꼬임)

> 꼭 트랜잭션 안에서 수행되어야할때 @PostConstruct 와 @Transactional 를 같이쓰면 트랜잭션이 동작하지 않는 것이지,
단순히 초기데이터, 더미데이터를 를 위한것이면 @PostConstruct 를사용하면 된다.

### 해결방법

해결방법으로는 트랜잭션 AOP 를 포함한 스프링이 컨테이너가 완전히 생성되고 난 다음에 이벤트가 붙은 메서드를 호출해주면된다.

- 스프링 컨테이너가 완전히 초기화되었을 때, 호출되는 ApplicationReadyEvent 를 사용하면 트랜잭션을 적용시킬 수 있다. 

## 트랜잭션 옵션

### value 혹은 transactionManager 옵션

`@Transactional()` 의 value 혹은 transactionManager 옵션에 트랜잭션 매니저 Bean 을 지정해주면 사용할 트랜잭션 매니저를 정해줄 수 있다.

> 참고로 해당 옵션은 트랜잭션 매니저가 둘 이상일때 사용한다. 그 외에는 보통 생략해준다. (default 값으로 사용)

### rollbackFor 옵션

- 기본적으로 트랜잭션은 RuntimeException, Error 와 그 하위 예외가 발생하는 롤백한다.
- 체크 예외인 Exception 과 그 하위 예외들은 커밋한다.

rollbackFOr 옵션을 명시하면, 추가적으로 어떤 예외가 발생할떄 롤백할지 지정할 수 있다.

```java
@Transactional(rollbackFor = Exception.class)
```

### noRollbackFor 옵션

rollbackFor 와 반대 기능

### propagation 옵션

트랜잭션 전파에 관한 옵션. 매우 중요하기 때문에 뒤에서 설명

### isolation 옵션

트랜잭션 격리 수준을 지정. 기본값은 데이터베이스에서 설정한 트래잭션 격리 수준을 사용하는 `DEFAULT` 이다.

- `DEFAULT` : 데이터베이스에서 설정한 격리 수준을 따른다.
- `READ_UNCOMMITTED` : 커밋되지 않은 읽기
- `READ_COMMITTED` : 커밋된 읽기
- `REPEATABLE_READ` : 반복 가능한 읽기
- `SERIALIZABLE` : 직렬화 가능

### timeout 옵션

트랜잭션 수행 시간에 대한 타임아웃을 초 단위로 지정한다. 기본 값은 트랜잭션 시스템의 타임아웃을 사용.
`timeoutString` 도 있는데, 숫자 대신 문자 값으로 지정할 수 있다.

### label 옵션

트랜잭션 애노테이션에 있는 값을 직접 읽어서 어떤 동작을 하고 싶을 때 사용할 수 있다. 일반적으로 사용하지 않는다.

### readOnly 옵션

트랜잭션은 기본적으로 읽기 쓰기가 모두 가능한 트랜잭션이 생성된다.
`readOnly=true` 를 사용하게 되면 읽기 전용 트랜잭션이 생성된다. 이 경우 `등록, 수정, 삭제가 안되고 읽기 기능만 동작`한다.
(드라이버나 데이터베이스에 따라 정상 동작하지 않는 경우도 있다.) 그리고 `readOnly` 옵션을 사용하면 일긱에서 다양한 성능 최적화가 발생한다.

```properties
# 트랜잭션의 시작(get) 과, 끝(End). 즉 complete 을 확인 가능. (commit 이 되는지, rollback 이 되는지는 확인할 수 없음.)
logging.level.org.springframework.transaction.interceptor=TRACE

# jdbc : 현재사용되는 트랜잭션 매니저 로그 (commit 되는지 rollback 되는지 확인 가능)
logging.level.org.springframework.jdbc.datasource.DataSourceTransactionManager=DEBUG

# JPA Log
# jpa : 현재사용되는 트랜잭션 매니저 로그 (commit 되는지 rollback 되는지 확인 가능)
logging.level.org.springframework.orm.jpa.JpaTransactionManager=DEBUG
logging.level.org.hibernate.resource.transaction=DEBUG
```

## 예외와 트랜잭션 커밋, 롤백 - 활용

- Q. 스프링은 왜 체크예외는 커밋하고 언체크(런타임) 예외는 롤백할까?
- A. 스프링은 기본적으로 체크 예외는 비즈니스 의미가 있을 때 사용하고, 런타임(언체크) 예외는 복구 불가능한 예외로 가정하기 때문
   - 체크 예외 : 비즈니스 의미가 있을떄 사용
   - 언체크 예외 : 복구 불가능한 예외

> 꼭 이런 정책을 따를 필요가 없음. 그냥 rollbackFor 라는 옵션을 사용해서 에크 예외도 롤백하면 된다.

### 정리

- 런타임 예외는 항상 롤백. 체크 예외인 경우 rollbackFor 옵션을 사용해서 비즈니스 상황에 따라서 커밋과 롤백을 선택하면 된다.