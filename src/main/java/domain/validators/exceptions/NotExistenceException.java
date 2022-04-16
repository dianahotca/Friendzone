package domain.validators.exceptions;

public class NotExistenceException extends RuntimeException {
    public NotExistenceException() {
        super("It doesn't exists!\n");

    }
}
