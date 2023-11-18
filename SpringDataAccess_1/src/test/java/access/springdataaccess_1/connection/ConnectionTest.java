package access.springdataaccess_1.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static access.springdataaccess_1.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    @DisplayName("자바가 지원하는 DriverManager 를 통해 커넥션을 획득. --> 커넥션을 얻어올때마다 DB 설정 정보를 넘겨주어야 한다.")
    public void driverManger() throws SQLException {
        Connection connection1 = DriverManager.getConnection(URL, USER, PASSWORD);
        Connection connection2 = DriverManager.getConnection(URL, USER, PASSWORD);
        log.info("connection = {}, class = {}", connection1, connection1.getClass());
        log.info("connection = {}, class = {}", connection2, connection2.getClass());
    }

    @Test
    @DisplayName("""
            스프링이 제공하는 DriverManagerDataSource 를 통해 커넥션을 획득
            --> DataSource 를 가져오는 과정에서만 DB 의 설정 정보를 제공하며 커넥션을 사용할때는 DB 설정 정보를 넘겨주지 않아도 된다.
            --> 설정과 사용을 분리
    """)
    public void dataSourceDriverManager() throws SQLException {
        DataSource driverManagerDataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        useDataSource(driverManagerDataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();
        log.info("connection = {}, class = {}", connection1, connection1.getClass());
        log.info("connection = {}, class = {}", connection2, connection2.getClass());
    }


}
