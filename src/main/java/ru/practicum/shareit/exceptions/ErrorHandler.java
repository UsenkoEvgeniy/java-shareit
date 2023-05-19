package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;
import java.security.AccessControlException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String emailAlreadyInUse(EmailIsUsedException e) {
        return e.getMessage();
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, NoSuchElementException.class, AccessControlException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(RuntimeException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String wrongUser(WrongUserException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> wrongEnumValue(MethodArgumentTypeMismatchException e) {
        return Map.of("error", "Unknown state: " + e.getValue());
    }

    @ExceptionHandler({BadRequestException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(RuntimeException e) {
        return e.getMessage();
    }
}
