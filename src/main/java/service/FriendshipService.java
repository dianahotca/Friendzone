package service;

import com.example.socialnetworkguiapplication.FriendRequestListener;
import domain.*;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import graphNetwork.FriendshipNetwork;
import repository.Repository;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.*;

public class FriendshipService implements Service<Tuple<String,String>, Friendship>, FriendRequestListener {
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
    }

    @Override
    public void remove(Friendship e) throws EntityNullException,NotExistenceException{
        friendshipRepository.findOne(e.getId());
        friendshipRepository.delete(e.getId());
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
    }

    @Override
    public Friendship findOne(Tuple<String, String> stringStringTuple) {
        return friendshipRepository.findOne(stringStringTuple);
    }

    @Override
    public void onFriendRequestAccepted(Friendship friendship) {
        add(friendship);
    }
}