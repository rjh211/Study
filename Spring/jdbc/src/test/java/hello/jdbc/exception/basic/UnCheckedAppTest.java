package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class UnCheckedAppTest {
    static class Controller{
        Service service = new Service();
        public void request() {
            service.logic();
        }
    }

    static class Service{
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("커넥션 연결 실패");
        }
    }

    static class Repository{
        public void call() {
            //해당 메서드에서 체크 예외를 받아서 언체크예외로 던짐
            try{
                runSQL();
            } catch (SQLException e){
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("SQL Exception");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            //cause -> 해당 예외의 원인을 함께 넣어서 보여줄 수 있음
            super(cause);
        }
    }

    @Test
    void unChecked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(()-> controller.request()).isInstanceOf(Exception.class);
    }
}
