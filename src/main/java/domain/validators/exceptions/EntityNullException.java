package domain.validators.exceptions;

public class EntityNullException extends RuntimeException{
    public EntityNullException(){
        super("Entity must not be null!\n");
    }
}
