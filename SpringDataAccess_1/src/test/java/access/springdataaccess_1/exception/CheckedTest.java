package access.springdataaccess_1.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    /**
     * Exception 을 상속받은 예외는 컴파일러가 체크하는 체크 예외가 된다. (단 RuntimeException 은 제외)
     */
    static class MyCheckedException extends Exception {

        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Checked 예외는
     * 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 필수로 선택해야 한다.
     */
    static class Service {

        private Repository repository = new Repository();

        /**
         * 예외를 잡아 처리하는 코드. --> 따라서 정상로직으로 돌아가게 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직 후 정상흐름으로 돌아간다.
                log.info("예외 처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * 체크 예외를 밖으로 던지는 코드
         * 체크 예외는 예외를 잡지 않고 밖으로 던지려면  throws 예외를 메서드에 필수로 선언해야 한다.
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {

        /**
         * @MyCheckedException 은 은 컴파일러가 체크하는 체크예외이기 때문에, catch 를 하지않으면 무조건 밖으로 throw 해야 한다.
         * @throws MyCheckedException
         */
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }

    @Test
    @DisplayName("Service 에서 발생한 Check 예외를 잡아 처리하였기 때문에, Service 를 사용하는 코드안에서는 체크 예외가 올라오지 않고, 정상 흐름으로 동작한다.")
    public void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    @DisplayName("Service 에서 발생한 Check 를 예외를 잡지 않고 던졌기 때문에 예외가 그대로 올라와서 현재 테스트메서드에서 예외를 잡거나 또 던져야한다.")
    public void checked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyCheckedException.class);
    }
}
