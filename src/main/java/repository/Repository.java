package repository;

import com.example.socialnetworkguiapplication.FriendRequestModel;
import com.example.socialnetworkguiapplication.FriendRequestSentModel;
import domain.Entity;
import com.example.socialnetworkguiapplication.FriendRequestSentModel;
import domain.validators.ValidationException;
import domain.validators.exceptions.NotExistenceException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.EntityNullException;
import repository.db.FriendRequestPagingRepository;
import repository.db.FriendshipPagingRepository;
import repository.db.MessagePagingRepository;

import java.util.List;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */
public interface Repository<ID, E extends Entity<ID>> extends FriendshipPagingRepository, FriendRequestPagingRepository, MessagePagingRepository {

    /**
     * @return the number of entities
     */
    Long getEntitiesCount();

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * @throws EntityNullException if id is null.
     * @throws NotExistenceException if the id doesn't exist
     */
    E findOne(ID id) throws EntityNullException,NotExistenceException;

    /**
     *
     * @return all entities
     */
    Iterable<E> getAllEntities();

    /**
     *
     * @param entity
     *         entity must be not null
     * @throws ValidationException
     *            if the entity is not valid
     * @throws EntityNullException
     *             if the given entity is null.
     * @throws ExistenceException if the id of the given entity already exists
     */
    void save(E entity) throws ValidationException,EntityNullException,ExistenceException;

    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @throws EntityNullException
     *                   if the given id is null.
     * @throws NotExistenceException if the id doesn't exist
     */
    void delete(ID id) throws EntityNullException,NotExistenceException;

    /**
     *
     * @param entity
     *          entity must not be null
     * @throws EntityNullException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     * @throws NotExistenceException if the id doesn't exist
     */
    void update(E entity) throws EntityNullException,ValidationException,NotExistenceException;
    List<E> getConversation(String email1,String email2);
    List<E> getFriends(String email);
    List<FriendRequestModel> sentFriendships(String email);
}

