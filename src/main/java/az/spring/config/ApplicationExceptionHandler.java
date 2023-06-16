package az.spring.config;

import az.spring.exception.*;
import az.spring.model.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ExceptionDto handleException(UserNotFoundException userNotFoundException) {
        log.error("User Not Found");
        return new ExceptionDto(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND.value());
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ExceptionDto handleException(UserAlreadyExistsException userAlreadyExistsException) {
        log.error("User Already Exists");
        return new ExceptionDto(userAlreadyExistsException.getMessage(), HttpStatus.ALREADY_REPORTED.value());
    }
    @ExceptionHandler(UnAuthorizedException.class)
    public ExceptionDto handleException(UnAuthorizedException unAuthorizedException) {
        log.error("UnAuthorizedException");
        return new ExceptionDto(unAuthorizedException.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }
    @ExceptionHandler(TokenIsNotValidException.class)
    public ExceptionDto handleException(TokenIsNotValidException tokenIsNotValidException) {
        log.error("Token Is Not Valid");
        return new ExceptionDto(tokenIsNotValidException.getMessage(), HttpStatus.NOT_FOUND.value());
    }
    @ExceptionHandler(TokenIsNotExists.class)
    public ExceptionDto handleException(TokenIsNotExists tokenIsNotExists) {
        log.error("Token Is Not Exists or has expired");
        return new ExceptionDto(tokenIsNotExists.getMessage(), HttpStatus.NOT_FOUND.value());
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ExceptionDto handleException(InvalidPasswordException invalidPasswordException) {
        log.error("Invalid Password Exception");
        return new ExceptionDto(invalidPasswordException.getMessage(), HttpStatus.NOT_ACCEPTABLE.value());
    }
}
