package access.springdataaccess_1.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLDataException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

public class CheckedAppTest {

    static class Controller {

        private Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {

        private Repository repository = new Repository();
        private NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {

        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }

    }

    static class Repository {

        public void call() throws SQLException {
            throw new SQLException();
        }
    }

    @Test
    public void checked() {
        Controller controller = new Controller();
        assertThatThrownBy(controller::request)
                .isInstanceOf(SQLException.class);
    }

}
