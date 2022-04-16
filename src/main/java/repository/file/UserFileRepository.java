package repository.file;

import domain.User;
import domain.validators.Validator;

import java.util.List;

public class UserFileRepository extends AbstractUserFileRepository {

    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getEmail();
    }

    @Override
    protected User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4));
        return user;
    }
}
