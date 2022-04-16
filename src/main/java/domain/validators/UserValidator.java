package domain.validators;

import domain.User;
import domain.validators.exceptions.PasswordValidator;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String val = "";
        if(entity.getFirstName().isEmpty() || !entity.getFirstName().matches("[a-zA-Z]+") || (entity.getFirstName().charAt(0) < 'A' ||  entity.getFirstName().charAt(0) > 'Z')){
            val = val.concat("Invalid first name!\n");
        }
        if(entity.getLastName().isEmpty() || !entity.getLastName().matches("[a-zA-Z]+") || (entity.getLastName().charAt(0) < 'A' ||  entity.getFirstName().charAt(0) > 'Z')){
            val = val.concat("Invalid last name!\n");
        }

        EmailValidator emailValidator=new EmailValidator();
        try{
            emailValidator.validate(entity.getEmail());
        }catch (ValidationException exc){
            val=val.concat(exc.getMessage());
        }

        PasswordValidator passwordValidator=new PasswordValidator();
        try{
            passwordValidator.validate(entity.getPassword());
        }catch (ValidationException exc){
            val=val.concat(exc.getMessage());
        }

        if(!val.isEmpty())
            throw new ValidationException(val);
    }
}
