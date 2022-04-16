package domain.validators;

public class EmailValidator implements Validator<String>{
    @Override
    public void validate(String entity) throws ValidationException {
        String error="";
        if(entity.isEmpty() || !entity.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            error = error.concat("Invalid email!\n");
        }
        if(!error.isEmpty())
            throw new ValidationException(error);
    }
}
