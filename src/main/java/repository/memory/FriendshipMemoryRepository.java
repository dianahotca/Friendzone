package repository.memory;

import com.example.socialnetworkguiapplication.FriendRequestModel;
import com.example.socialnetworkguiapplication.UserModel;
import domain.Friendship;
import domain.Message;
import domain.Tuple;
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

public class FriendshipMemoryRepository implements Repository<Tuple<String,String>, Friendship> {

    private Validator<Friendship> validator;
    private Map<Tuple<String,String>, Friendship> entities;

    public FriendshipMemoryRepository(Validator<Friendship> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public Long getEntitiesCount() {
        return (long) entities.size();
    }

    @Override
    public Friendship findOne(Tuple<String,String> id) {
        if (id == null)
            throw new IdNullException();
        for (Friendship f:getAllEntities())
        {
            if(f.getId().equals(id))
                return entities.get(id);
        }
        throw new NotExistenceException();
    }

    @Override
    public Iterable<Friendship> getAllEntities() {
        return entities.values();
    }

    @Override
    public void save(Friendship entity) {
        if (entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        try {
            findOne(entity.getId());
            throw new ExistenceException();
        } catch (NotExistenceException exc) {
            entities.put(entity.getId(), entity);
        }
    }

    @Override
    public void delete(Tuple<String,String> id){
        if (id == null) {
            throw new IdNullException();
        }
        findOne(id);
        entities.remove(id);
    }


    @Override
    public void update(Friendship entity) {
        if(entity == null)
            throw new EntityNullException();
        validator.validate(entity);
        findOne(entity.getId());
        entities.put(entity.getId(),entity);
    }

    @Override
    public List<Friendship> getConversation(String email1, String email2) {
        return null;
    }

    @Override
    public List<Friendship> getFriends(String email) {
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
