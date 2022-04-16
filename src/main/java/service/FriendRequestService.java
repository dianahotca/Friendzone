package service;

import domain.FriendRequest;
import domain.Tuple;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;
import utils.Constants;

import java.time.LocalDateTime;

public class FriendRequestService implements Service<Tuple<String,String>, FriendRequest> {
    Repository<Tuple<String,String>,FriendRequest> friendRequestRepository;

    public FriendRequestService(Repository<Tuple<String,String>, FriendRequest> friendRequestRepository) {
        this.friendRequestRepository = friendRequestRepository;
    }

    @Override
    public Iterable<FriendRequest> getAll() {
        return friendRequestRepository.getAllEntities();
    }

    @Override
    public void add(FriendRequest friendRequest) throws EntityNullException,NotExistenceException,ExistenceException{
        try{
            FriendRequest foundFriendRequest=friendRequestRepository.findOne(friendRequest.getId());
            if(foundFriendRequest.getStatus().equals("declined")) {
                foundFriendRequest.setStatus("pending");
                foundFriendRequest.setDate(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
                friendRequestRepository.delete(foundFriendRequest.getId());
                friendRequestRepository.save(foundFriendRequest);
            }else if(foundFriendRequest.getStatus().equals("pending")){
                throw new ExistenceException();
            }
        }catch (NotExistenceException exc) {
            friendRequestRepository.save(friendRequest);
        }
    }

    @Override
    public void remove(FriendRequest friendRequest) throws EntityNullException,NotExistenceException{
        friendRequestRepository.delete(friendRequest.getId());
    }

    @Override
    public void update(FriendRequest friendRequest) throws EntityNullException, ValidationException,NotExistenceException {
        friendRequestRepository.update(friendRequest);
    }

    @Override
    public FriendRequest findOne(Tuple<String, String> stringStringTuple) throws EntityNullException, NotExistenceException {
        return friendRequestRepository.findOne(stringStringTuple);
    }

}
