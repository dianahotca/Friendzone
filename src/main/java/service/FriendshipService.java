package service;

import com.example.socialnetworkguiapplication.FriendRequestListener;
import com.example.socialnetworkguiapplication.UserModel;
import domain.*;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import graphNetwork.FriendshipNetwork;
import repository.Repository;
import repository.paging.Page;
import repository.paging.Pageable;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.*;

public class FriendshipService extends Observable implements Service<Tuple<String,String>, Friendship>, FriendRequestListener {
    private Repository<Tuple<String,String>, Friendship> friendshipRepository;

    public FriendshipService(Repository<Tuple<String,String>, Friendship> friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public Iterable<Friendship> getAll() {
        return friendshipRepository.getAllEntities();
    }

    @Override
    public void add(Friendship friendship) throws EntityNullException, ValidationException, ExistenceException {
        this.friendshipRepository.save(friendship);
        setChanged();
        notifyObservers();
    }

    @Override
    public void remove(Friendship e) throws EntityNullException,NotExistenceException{
        friendshipRepository.findOne(e.getId());
        friendshipRepository.delete(e.getId());
        setChanged();
        notifyObservers();
    }

    /**
     * removes all the friendships that contains the entity that must be deleted
     * @param user - entity
     * */
    public void removeAllFriendshipsOfOneUser(User user){
        Iterable<Friendship> allFriendships = friendshipRepository.getAllEntities();
        List<Tuple<String,String>> idToDel = new ArrayList<>();
        for(Friendship friendship:allFriendships){
            if(friendship.getUserEmails().getLeft().equals(user.getId()) || friendship.getUserEmails().getRight().equals(user.getId())) {
                idToDel.add(friendship.getId());
            }
        }
        for(Tuple<String,String> id:idToDel){
            friendshipRepository.delete(id);
        }
    }

    public int numberOfCommunities() {
        FriendshipNetwork networking = new FriendshipNetwork(friendshipRepository);
        return networking.numberOfCommunities();
    }

    public List<String> mostSociableCommunity() {
        FriendshipNetwork networking = new FriendshipNetwork(friendshipRepository);
        return networking.mostSociableCommunity();
    }

    @Override
    public void update(Friendship e) {
        Iterable<domain.Friendship> friendships =  friendshipRepository.getAllEntities();
        boolean ok = false;
        for(domain.Friendship friendship:friendships){
            if(friendship.getUserEmails().equals(e.getUserEmails())){
                friendship.setDate();
                friendshipRepository.update(friendship);
                ok = true;
                break;
            }
        }
        if(!ok)
            throw new NotExistenceException();
        setChanged();
        notifyObservers();
    }

    @Override
    public Friendship findOne(Tuple<String, String> stringStringTuple) {
        return friendshipRepository.findOne(stringStringTuple);
    }

    @Override
    public void onFriendRequestAccepted(Friendship friendship) {
        add(friendship);
    }

    public List<Friendship> getFriends(String email){
        return  friendshipRepository.getFriends(email);
    }

    public Page<UserModel> getFriends(Pageable<UserModel> pageable, String email){
        return  friendshipRepository.getFriends(pageable, email);
    }
}