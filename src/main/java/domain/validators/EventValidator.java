package domain.validators;

import domain.Event;

public class EventValidator implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException {
        String errorMessage="";
        if(entity.getDescription()==null)
            errorMessage.concat("Invalid description! ");
        if(entity.getDate()==null)
            errorMessage.concat("Invalid date!");
        if(!errorMessage.equals(""))
            throw new ValidationException(errorMessage);
    }
}
