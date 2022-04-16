package domain.validators.exceptions;

import domain.validators.ValidationException;
import domain.validators.Validator;

public class PasswordValidator implements Validator<String> {
    @Override
    public void validate(String entity) throws ValidationException {
        String error="";
        if(entity.length() < 8){
            error = error.concat("Invalid password!\n");
        }
        if(!error.isEmpty())
            throw new ValidationException(error);
    }
}