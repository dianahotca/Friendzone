package service;

import com.example.socialnetworkguiapplication.FriendRequestModel;
import domain.FriendRequest;
import com.example.socialnetworkguiapplication.FriendRequestSentModel;
import domain.Tuple;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Observable;

public class FriendRequestService extends Observable implements Service<Tuple<String,String>, FriendRequest> {
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
        setChanged();
        notifyObservers();
    }

    @Override
    public void remove(FriendRequest friendRequest) throws EntityNullException,NotExistenceException{
        friendRequestRepository.delete(friendRequest.getId());
        setChanged();
        notifyObservers();
    }

    @Override
    public void update(FriendRequest friendRequest) throws EntityNullException, ValidationException,NotExistenceException {
        friendRequestRepository.update(friendRequest);
        setChanged();
        notifyObservers();
    }

    @Override
    public FriendRequest findOne(Tuple<String, String> stringStringTuple) throws EntityNullException, NotExistenceException {
        return friendRequestRepository.findOne(stringStringTuple);
    }

    public List<FriendRequestModel> sentFriendRequest(String email){
        return friendRequestRepository.sentFriendships(email);
    }
}
