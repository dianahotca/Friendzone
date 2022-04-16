package domain.validators;

import domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    private EmailValidator emailValidator=new EmailValidator();
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errorMessage = "";
        try {
            emailValidator.validate(entity.getUserEmails().getLeft());
        }catch(ValidationException exc)
        {
            errorMessage = errorMessage.concat("Invalid first ID\n");
        }
        try {
            emailValidator.validate(entity.getUserEmails().getRight());
        }catch (ValidationException exc){
            errorMessage = errorMessage.concat("Invalid second ID\n");
        }
        if(!errorMessage.isEmpty()){
            throw new ValidationException(errorMessage);
        }
    }
}
