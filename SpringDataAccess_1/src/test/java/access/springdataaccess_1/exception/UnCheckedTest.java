package access.springdataaccess_1.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedTest {

    /**
     * RuntimeException 을 상속 받은 예외는 언체크 예외가 된다.
     */
    static class MyUnCheckedException extends RuntimeException {

        public MyUnCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Unchecked 예외는
     * 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service {

        private Repository repository = new Repository();

        /**
         * 언체크 예외는 필요한 경우 예외를 잡아서 처리하면 된다. (필수가 아님)
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUnCheckedException e) {
                log.info("예외 처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {

        public void call() {
            throw new MyUnCheckedException("ex");
        }
    }

    @Test
    @DisplayName("필수는 아니지만 언체크 예외는 catch 잡아서 해결할 수 있다.")
    public void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    @DisplayName("언체크 예외는 throws 를 하지 않아도 자동으로 밖으로 throws 된다.")
    public void checked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUnCheckedException.class);
    }
}
