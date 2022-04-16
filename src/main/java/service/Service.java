package service;

import domain.Entity;
import domain.User;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;

public interface Service <ID,E extends Entity<ID>> {
    /**
     * @return a list with all the entities in memory
     * */
    Iterable<E> getAll();

    /**
     * saves in memory the entity
     * @param e
     *         entity must be not null
     * @throws ValidationException if the entity is invalid
     * @throws EntityNullException if the entity is null
     * @throws ExistenceException if the entity already exists in repository
     **/
    void add(E e) throws ValidationException, EntityNullException, ExistenceException;

    /**
     *  removes the entity with the specified id
     * @param e
     *      entity must be not null
     * @throws EntityNullException if the entity is null
     * @throws NotExistenceException if the entity doesn't exist in repository
     */
    void remove(E e) throws EntityNullException, NotExistenceException ;

   /**
    *  returns the entity with the specified id
    * @param id
    *      entity must be not null
    * @throws EntityNullException if the entity is null
    * @throws NotExistenceException if the entity doesn't exist in repository
    */
   E findOne(ID id) throws EntityNullException, NotExistenceException;

   void update(E e) throws EntityNullException, NotExistenceException, ValidationException;
}
