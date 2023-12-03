## 데이터 접근 기술 - Spring JdbcTemplate

- SQL 을 직접 사용하는 경우에 Spring 이 제공하는 JdbcTemplate 은 아주 좋은 선택지이다.
- JdbcTemplate 은 JDBC 를 매우 편리하게 사용할 수 있게 도와준다.

### 장점

1. 설정의 편리함
    - spring-jdbc 라이브러리에 포함되어있기 spring-jdbc 의 의존성이 있다면 별도의 설정 없이 바로 사용할 수 있다.
2. 반복 문제 해결
   - 개발자는 SQL 을 작성하고 전달할 파라미터를정의하고 , 응답 값을 매핑하기만 하면 된다.
        - Connection 획득
        - Statement 를 준비하고 실행
        - 결과를 반복하도록 루프를 실행
        - Connection 종료, Statement, ResultSet 종료
        - 트랜잭션을 다루기 위한 Connection 동기화
        - 예외 발생 시 스프링 예외 변환기 실행

### 단점
 
- 동적 SQL 을 해결하기 어렵다.

### JdbcTemplate

- 순서 기반 파라미터 바인딩을 지원

```
jdbcTemplate.update(PreparedStatementCreator preparedStatementCreator, KeyHolder keyHolder);
jdbcTemplate.update(String sql, Object... params);
jdbcTemplate.queryForObject(String sql, RowMapper<T> rowMapper, Object... params);
jdbcTemplate.query(String sql, RowMapper<T> rowMapper, Object... params);
```

### NamedParameterJdbcTemplate

- 이름 기반 파라미터 바인딩을 지원. (권장)
- NamedParameterJdbcTemplate 를 사용하면 3 가지 방법으로 파라미터 매핑 가능
    1. BeanPropertySqlParameterSource (SqlParameterSource 구현체)
    2. MapSqlParameterSource (SqlParameterSource 구현체)
    3. Map

```
jdbcTemplate.update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder)
jdbcTemplate.update(String sql, SqlParameterSource paramSource);
jdbcTemplate.queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T>rowMapper)
jdbcTemplate.query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
```

### SimpleJdbcInsert

- INSERT SQL 을 편리하게 사용할 수 있다.
- PK 생성이 DB 에서 AutoIncrement 될때 유용하다.

```
new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id");
//                .usingColumns("item_name", "price", "quantity"); // 생략 가능
```

### 정리

기본적으로 JdbcTemplate 의 아래와 같은 메서드 시그니처를 사용.

- jdbcTemplate.update() 는 insert, update, delete 에서 사용
- jdbcTemplate.queryForObject() 는 단건 조회에 사용
- jdbcTemplate.query() 는 목록 조회에 사용
