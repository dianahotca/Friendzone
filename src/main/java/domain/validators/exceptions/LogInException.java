package domain.validators.exceptions;

public class LogInException extends RuntimeException{
    public LogInException(){
        super("Please log in firstly!\n");
    }
}
