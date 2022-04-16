package graphNetwork;

import domain.Friendship;
import domain.Tuple;
import repository.Repository;

import java.util.List;


public class FriendshipNetwork {
    Repository<Tuple<String,String>, Friendship> friendshipRepository;
    Graph<String> friendshipGraph=new Graph<String>();

    public FriendshipNetwork(Repository<Tuple<String, String>, Friendship> friendshipRepository){
        this.friendshipRepository = friendshipRepository;

        for (Friendship friendship :friendshipRepository.getAllEntities()) {
            String userName1 = friendship.getId().getLeft();
            String userName2 = friendship.getId().getRight();
            friendshipGraph.addNewEdge(userName1, userName2, true);
        }
    }

    public int numberOfCommunities(){ return friendshipGraph.numberOfCommunities();}

    public List<String> mostSociableCommunity(){ return friendshipGraph.mostSociableCommunity();}
}
