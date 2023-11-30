package access.springdataaccess_1.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * Checked 익셉션을 UnChecked 익셉션으로 바꾸어서 Throws 하면 된다.
 */
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

    ////////////////////////////////////////////////////////

    static class Controller {

        private Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    ////////////////////////////////////////////////////////

    static class Service {

        private Repository repository = new Repository();
        private NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    ////////////////////////////////////////////////////////

    static class NetworkClient {

        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }

    }

    ////////////////////////////////////////////////////////

    static class Repository {

        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    ///////////////// Checked 예외를 잡아서 UnChecked 예외로 변경 /////////////////

    static class RuntimeConnectException extends RuntimeException {

        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {

        public RuntimeSQLException(String message) {
            super(message);
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

    ////////////////////////////////////////////////////////

    @Test
    public void unchecked() {
        Controller controller = new Controller();
        assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeSQLException.class);
    }

    @Test
    public void printEx() {
        CheckedAppTest.Controller controller = new CheckedAppTest.Controller();
        try {
            controller.request();
        } catch (Exception e) {
//            e.printStackTrace();
            log.info("ex", e);
        }
    }

}
