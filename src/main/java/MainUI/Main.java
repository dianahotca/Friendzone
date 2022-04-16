package MainUI;

import com.example.socialnetworkguiapplication.Controller;
import domain.*;
import domain.validators.*;
import repository.Repository;
import repository.db.*;
import service.*;
import ui.UserInterface;

public class Main {
    public static void main(String[] args) {

        Validator<User> userValidator = new UserValidator();
        Validator<String> emailValidator = new EmailValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        Validator<Event> eventValidator=new EventValidator();
        Repository<String, User> userDbRepository=new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda", userValidator,emailValidator);
        Repository<Tuple<String, String>, Friendship> friendshipDbRepository =new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda",friendshipValidator);
        Repository<Long, Message> messageDbRepository = new MessageDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda");
        Repository<Tuple<String, String>, FriendRequest> friendRequestDbRepository=new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda");
        Repository<Integer,Event> eventDbRepository=new EventDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda",eventValidator);
        Service<String, User> userService = new UserService(userDbRepository);
        Service<Tuple<String, String>, Friendship> friendshipService = new FriendshipService(friendshipDbRepository);
        Service<Long,Message> messageService = new MessageService(messageDbRepository);
        Service<Tuple<String, String>, FriendRequest> friendRequestService=new FriendRequestService(friendRequestDbRepository);
        Service<Integer,Event> eventService=new EventService((EventDbRepository) eventDbRepository);

        //In memory

        /*UserMemoryRepository<Long, User> userMemoryRepository = new UserMemoryRepository<>(userValidator);
        FriendshipMemoryRepository<Long, Friendship> friendshipMemoryRepository = new FriendshipMemoryRepository<>(friendshipValidator);
        */

        //In file

        /* UserFileRepository userFileRepository = new UserFileRepository("src/userFile.cvs",userValidator);
         FriendshipFileRepository friendshipFileRepository = new FriendshipFileRepository("src/friendshipFile.cvs",friendshipValidator);
         UserService<Long, User> userService = new UserService<>(userFileRepository);
          FriendshipService<Long, Friendship> friendshipService = new FriendshipService<>(friendshipFileRepository);
        */

        //In db

        Controller controller = new Controller((UserService) userService,(FriendshipService) friendshipService,(MessageService) messageService,(FriendRequestService) friendRequestService,(EventService) eventService);
        UserInterface Ui = new UserInterface(controller);
        Ui.startUI();
    }
}