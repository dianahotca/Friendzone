package repository.memory;

import com.example.socialnetworkguiapplication.FriendRequestModel;
import com.example.socialnetworkguiapplication.UserModel;
import domain.Message;
import domain.User;
import domain.validators.Validator;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.IdNullException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;
import repository.paging.Page;
import repository.paging.Pageable;

import java.util.HashMap;
import java.util.List;
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

    @Override
    public List<User> getConversation(String email1, String email2) {
        return null;
    }

    @Override
    public List<User> getFriends(String email) {
        return null;
    }

    @Override
    public List<FriendRequestModel> sentFriendships(String email) {
        return null;
    }


    @Override
    public Page<UserModel> getFriends(Pageable<UserModel> pageable, String email) {
        return null;
    }

    @Override
    public Page<Message> getConversation(Pageable<Message> pageable, String email1, String email2) {
        return null;
    }
}
