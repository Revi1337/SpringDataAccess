package access.springdataaccess_1.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionUtil.class);

    /**
     * DriverManager.getConnection() 는 DriverManager 라이브러리에 있는 DB Driver 를 찾아서
     * 해당 Driver 가 제공하는 커넥션을 반환해준다. (정확히는 Connection 인터페이스의 구현체를 반환)
     *
     * 여기서는 H2 데이터베이스 Driver 가 작동해서 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환해준다.
     * 정확히는 h2.Drvier 라이브러리의 (org.h2.jdbc.JdbcConnection 을 반환)
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    ConnectionConst.URL, ConnectionConst.USER, ConnectionConst.PASSWORD
            );
            LOGGER.info("Get Connection  = {}, Get Class = {}", connection, connection.getClass());
            return connection;
        }
        catch (SQLException sqlException) {
            throw new IllegalStateException();
        }
    }

}
