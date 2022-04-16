package repository.memory;

import domain.User;
import domain.validators.Validator;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.IdNullException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;
import java.util.HashMap;
import java.util.Map;

public class UserMemoryRepository implements Repository<String, User> {
    private Validator<User> validator;
    private Validator<String> emailValidator;
    private Map<String, User> entities;

    public UserMemoryRepository(Validator<User> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public Long getEntitiesCount() {
        return (long) entities.size();
    }

    @Override
    public User findOne(String id) {
        if (id == null)
            throw new IdNullException();
        emailValidator.validate(id);
        for (User user : getAllEntities()) {
            if (user.getId().equals(id))
                return entities.get(id);
        }
        throw new NotExistenceException();
    }

    @Override
    public Iterable<User> getAllEntities() {
        return entities.values();
    }

    @Override
    public void save(User entity) {
        if (entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        try {
            findOne(entity.getId());
            throw new ExistenceException();
        } catch (NotExistenceException exc){
            entities.put(entity.getId(), entity);
        }
    }

    @Override
    public void delete(String id){
        if (id == null) {
            throw new IdNullException();
        }
        findOne(id);
        entities.remove(id);
    }

    @Override
    public void update(User entity) {
        if(entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        findOne(entity.getId());
        entities.put(entity.getId(),entity);
    }
}
