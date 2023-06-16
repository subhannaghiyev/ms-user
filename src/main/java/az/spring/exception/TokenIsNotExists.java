package az.spring.exception;

public class TokenIsNotExists extends RuntimeException{
    public TokenIsNotExists(String message){
        super(message);
    }
}
