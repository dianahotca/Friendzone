package service;

import domain.User;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;

import java.util.Observable;

public class UserService extends Observable implements Service<String, User> {
    private Repository<String, User> userRepository;

    public UserService(Repository<String, User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Iterable<User> getAll() {
        return userRepository.getAllEntities();
    }

    @Override
    public void add(User e) throws ValidationException, EntityNullException, ExistenceException {
        this.userRepository.save(e);
        setChanged();
        notifyObservers();
    }

    @Override
    public void remove(User e) throws EntityNullException, NotExistenceException, ValidationException {
        this.userRepository.delete(e.getId());
        setChanged();
        notifyObservers();
    }

    @Override
    public User findOne(String id) throws EntityNullException, NotExistenceException, ValidationException {
        return userRepository.findOne(id);
    }

    @Override
    public void update(User newUser) throws EntityNullException, NotExistenceException, ValidationException {
        userRepository.update(newUser);
        setChanged();
        notifyObservers();
    }

    public void findOneByEmailAndPassword(String loggedEmail, String password) throws EntityNullException, NotExistenceException, ValidationException {
        User foundUser = userRepository.findOne(loggedEmail);
        if (foundUser == null)
            throw new NotExistenceException();
        if (!foundUser.getPassword().equals(password)) {
            throw new NotExistenceException();
        }
    }
}
