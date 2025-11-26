package ma.microtech.smartshop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}

    private ErrorResponse buildError(HttpServletRequest request, int status, String error, String message) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildError(request, 401, "Unauthorized", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildError(request, 403, "Forbidden", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        if (ex.getMessage().contains("Invalid Credentials")) {
            return new ResponseEntity<>(buildError(request, 401, "Unauthorized", "Invalid username or password"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(buildError(request, 400, "Bad Request", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthenticated(IllegalStateException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildError(request, 403, "Forbidden", "You are not authenticated"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildError(request, 422, "Unprocessable Entity", ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildError(request, 404, "Not Found", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var fieldError = ex.getBindingResult().getFieldError();
        String errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        if (fieldError != null && fieldError.getField().equals("codePromo")) {
            return new ResponseEntity<>(buildError(request, 422, "Unprocessable Entity", errors), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (fieldError != null && fieldError.getField().equals("items")) {
            return new ResponseEntity<>(buildError(request, 422, "Unprocessable Entity", errors), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(buildError(request, 400, "Validation Failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "Database constraint violation";
        if (ex.getMessage().contains("not-null")) {
            message = "Required field is missing";
        }

        return new ResponseEntity<>(buildError(request, 400, "Bad Request", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        return new ResponseEntity<>(buildError(request, 500, "Internal Server Error", "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}