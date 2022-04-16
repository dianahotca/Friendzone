package domain.validators.exceptions;

public class IdNullException extends RuntimeException {
    public IdNullException() {
        super("Id must not be null!\n");
    }
}
