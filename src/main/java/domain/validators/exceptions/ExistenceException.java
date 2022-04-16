package domain.validators.exceptions;

public class ExistenceException extends RuntimeException {
    public ExistenceException() {
        super("Already exists!\n");

    }
}
