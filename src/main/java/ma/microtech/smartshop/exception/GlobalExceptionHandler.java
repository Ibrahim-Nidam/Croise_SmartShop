package ma.microtech.smartshop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    record ErrorResponse (
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ){}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req){
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest req){
        int status = ex.getMessage().contains("Invalid Credentials") ? 401 : 400;
        String error = status == 401 ? "Unauthorized" : "Bad request";

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.valueOf(status));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthenticated(IllegalStateException ex, HttpServletRequest req){
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                403,
                "Forbidden",
                "You are not Authenticated",
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

}
