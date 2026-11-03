package gameflix_auth;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, String>> invalidInput(
            Exception exception) {

        return ResponseEntity.badRequest().body(
            Map.of("message", "Invalid request input")
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> databaseConflict(
            DataIntegrityViolationException exception) {

        Throwable cause = exception;

        while (cause != null) {
            String message = cause.getMessage();

            if (message != null
                    && message.contains("uk_users_email")) {
                return ResponseEntity.status(409).body(
                    Map.of("message", "Email already registered")
                );
            }

            cause = cause.getCause();
        }

        return ResponseEntity.status(500).body(
            Map.of("message", "Unable to save account")
        );
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> invalidCredentials(
            InvalidCredentialsException exception) {

        return ResponseEntity.status(401).body(
            Map.of("message", "Invalid email or password")
        );
    }
}