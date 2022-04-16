package com.example.socialnetworkguiapplication;

import com.example.socialnetworkguiapplication.FriendRequestModel;
import domain.*;
import domain.validators.ValidationException;
import domain.validators.exceptions.*;
import service.FriendRequestService;
import service.FriendshipService;
import service.MessageService;
import service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static utils.Constants.DATE_TIME_FORMATTER;

public class Controller {
    private PasswordValidator passwordValidator;
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private FriendRequestService friendRequestService;
    private String loggedId;
    private String loggedPassword;

    public Controller(UserService userService, FriendshipService friendshipService, MessageService messageService, FriendRequestService friendRequestService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.friendRequestService=friendRequestService;
        passwordValidator=new PasswordValidator();
        loggedId="";
        loggedPassword="";
    }

    /**
     * @return the id of the logged user
     * @throws LogInException if no user is logged
     */
    public String getLoggedEmail() throws LogInException{
        if(!Objects.equals(loggedId, ""))
            return loggedId;
        throw new LogInException();
    }

    /**
     * @param id the id of the user
     * Sets the value of the logged id to id
     * @throws NotExistenceException if there is no user with the specified id
     * @throws EntityNullException if the id is null
     */
    public void setLoggedEmail(String id) throws EntityNullException, NotExistenceException, ValidationException {
        userService.findOne(id);
        loggedId=id;
    }

    /**
     * @param password the password of the user
     * Sets the value of the logged password to password
     * @throws NotExistenceException if there is no user with the specified password
     * @throws EntityNullException if the password is null
     * @throws LogInException if no user is logged
     */
    public void setLoggedPassword(String password) throws EntityNullException, NotExistenceException, LogInException, ValidationException {
        passwordValidator.validate(password);
        String loggedEmail=getLoggedEmail();
        userService.findOneByEmailAndPassword(loggedEmail,password);
        loggedPassword=password;
    }

    /**
     * @return the password of the logged user
     * @throws LogInException if no user is logged
     */
    public String getLoggedPassword() throws LogInException{
        getLoggedEmail();
        return loggedPassword;
    }

    public int numberOfCommunities(){
        return friendshipService.numberOfCommunities();
    }

    public List<User> mostSociableCommunity(){
        List<String> rez = friendshipService.mostSociableCommunity();
        List<User> users = new ArrayList<>();
        for(String id: rez)
            users.add(userService.findOne(id));
        return users;
    }

    public Iterable<User> getAllUsers(){
        return userService.getAll();
    }

    public Iterable<Friendship> getAllFriendships(){
        return friendshipService.getAll();
    }

    public void addUser(String firstName, String lastName, String email, String password) throws EntityNullException, ValidationException,ExistenceException {
        User user = new User(firstName, lastName, email,password);
        this.userService.add(user);
    }

    public void removeUser(String email) throws EntityNullException,ValidationException,NotExistenceException{
        User userToDel = userService.findOne(email);
        friendshipService.removeAllFriendshipsOfOneUser(userToDel);
        userService.remove(userToDel);
    }

    public void addFriend(String newFriendEmail) throws LogInException,EntityNullException,ValidationException,ExistenceException{
        userService.findOne(newFriendEmail);
        String loggedUserEmail = getLoggedEmail();
        friendshipService.add(new Friendship(new Tuple<>(loggedUserEmail,newFriendEmail),LocalDateTime.now().format(DATE_TIME_FORMATTER)));
    }

    public void removeFriend(String email) throws EntityNullException,NotExistenceException{
        userService.findOne(email);
        Tuple<String,String> idToDelete = new Tuple<>(getLoggedEmail(),email);
        Friendship friendshipToDelete= friendshipService.findOne(idToDelete);
        friendshipService.remove(friendshipToDelete);
    }

    public List<UserDto> getFriends(User user){
        List<UserDto> friends = new ArrayList<>();
        Set<Friendship> friendships = (Set<Friendship>)friendshipService.getAll();
        friendships.stream()
                .filter(x->x.getUserEmails().getLeft().equals(user.getId()))
                .forEach(x->{
                    User friend=userService.findOne(x.getUserEmails().getRight());
                    friends.add(new UserDto(friend.getFirstName(),friend.getLastName(),x.getDate()));
                });
        friendships.stream()
                .filter(x->x.getUserEmails().getRight().equals(user.getId()))
                .forEach(x->{
                    User friend=userService.findOne(x.getUserEmails().getLeft());
                    friends.add(new UserDto(friend.getFirstName(),friend.getLastName(),x.getDate()));
                });
        return friends;
    }

    public List<User> getFriendsOfUser(User user){
        List<User> friends = new ArrayList<>();
        Set<Friendship> friendships = (Set<Friendship>)friendshipService.getAll();
        friendships.stream()
                .filter(x->x.getUserEmails().getLeft().equals(user.getId()))
                .forEach(x->{
                    User friend=userService.findOne(x.getUserEmails().getRight());
                    friends.add(friend);
                });
        friendships.stream()
                .filter(x->x.getUserEmails().getRight().equals(user.getId()))
                .forEach(x->{
                    User friend=userService.findOne(x.getUserEmails().getLeft());
                    friends.add(friend);
                });
        return friends;
    }

    public void updateUser(String email, String newF, String newL) throws EntityNullException,ValidationException,NotExistenceException{
        User user = userService.findOne(email);
        user.setFirstName(newF);
        user.setLastName(newL);
        userService.update(user);
    }

    public void updateFriendship(String email1, String email2) throws EntityNullException,ValidationException,NotExistenceException{
        User user1 = userService.findOne(email1);
        User user2 = userService.findOne(email2);
        Friendship friendship = new Friendship(new Tuple<>(user1.getId(),user2.getId()), LocalDateTime.now().format(DATE_TIME_FORMATTER));
        friendshipService.update(friendship);

    }

    public User getUser(String email) throws EntityNullException,ValidationException,NotExistenceException{
        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
            throw new ValidationException("Invalid email!\n");
        return userService.findOne(email);
    }

    public List<FriendRequestModel> getRecievedRequests() throws LogInException{
        List<FriendRequestModel> friendRequests=new ArrayList<>();
        Set<User> usersWhoSentRequests= (Set<User>) getUsersWhoSentRequests();
        for (User user:usersWhoSentRequests) {
            FriendRequest request=friendRequestService.findOne(new Tuple<>(user.getEmail(),getLoggedEmail()));
            FriendRequestModel requestDto=new FriendRequestModel(user.getEmail(),request.getStatus(),request.getDate());
            friendRequests.add(requestDto);
        }
        return friendRequests;
    }

    public List<UserDto> friendshipsByMonth(String email, String month) throws EntityNullException,ValidationException,NotExistenceException{

        User user = this.getUser(email);
        if(Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12)
            throw new ValidationException("Invalid month!\n");

        Iterable<Friendship> friendships = friendshipService.getAll();
        List<Friendship> newList = new ArrayList<>();
        friendships.forEach(newList::add);

        List<UserDto> userDtos = newList.stream()
                .filter(x->x.getUserEmails().getLeft().equals(user.getId()) && x.getDate().substring(5,7).equals(month))
                .map(x->new UserDto(userService.findOne(x.getUserEmails().getRight()).getLastName(),userService.findOne(x.getUserEmails().getRight()).getFirstName(), x.getDate()))
                .collect(Collectors.toList());

        userDtos.addAll(newList.stream()
                .filter(x->x.getUserEmails().getRight().equals(user.getId()) && x.getDate().substring(5,7).equals(month))
                .map(x->new UserDto(userService.findOne(x.getUserEmails().getLeft()).getLastName(),userService.findOne(x.getUserEmails().getLeft()).getFirstName(), x.getDate()))
                .collect(Collectors.toList()));

        return userDtos;
    }

    public void sendMessage(List<User> usersList, String message) throws LogInException{
        String email = this.getLoggedEmail();
        User loggedUser = userService.findOne(email);
        Iterable<Friendship> friendships = friendshipService.getAll();
        Message newMessage = new Message( this.messageService.size()+1,loggedUser,usersList,message,null,LocalDateTime.now().format(DATE_TIME_FORMATTER));
        messageService.add(newMessage);

        boolean ok = true;
        for(User user : usersList){
            for(Friendship friendship: friendships){
                if((friendship.getUserEmails().getRight().equals(user.getEmail()) && friendship.getUserEmails().getLeft().equals(loggedUser.getEmail())) || friendship.getUserEmails().getRight().equals(loggedUser.getEmail()) && friendship.getUserEmails().getLeft().equals(user.getEmail())) {
                    ok = false;
                    break;
                }
            }
        }
        if(ok == true)
            throw new ValidationException("User is not in friend's list!");
    }

    public List<Message> allMessages(){

        List<Message> messageOfCrtUser = new ArrayList<>();
        User user = userService.findOne(getLoggedEmail());
        Iterable<Message> messageList = (Iterable<Message>) messageService.getAll();
        for(Message message : messageList){
            if(message.getTo().contains(user)){
                messageOfCrtUser.add(message);
            }
        }
        return messageOfCrtUser;
    }

    public void replyMessage(Long id, String messageReply){

        Message reply = null;
        if(id > messageService.size()+1){
            throw new ValidationException("This message doesn't exist!");
        }

        Iterable<Message> messageList = (Iterable<Message>) messageService.getAll();
        for(Message message1 : messageList){
            if(message1.getId().equals(id)){
                reply = new Message(messageService.size()+1,userService.findOne(getLoggedEmail()), Arrays.asList(message1.getFrom()),messageReply,message1, LocalDateTime.now().format(DATE_TIME_FORMATTER));
                break;
            }
        }
        this.messageService.add(reply);
    }

    public void replyAll(Long id, String messageReply){

        Message reply = null;
        if(id > messageService.size()+1){
            throw new ValidationException("This message doesn't exist!");
        }

        List<User> usersToReply = new ArrayList<>();

        Iterable<Message> messageList = (Iterable<Message>) messageService.getAll();
        for(Message message1 : messageList){
            if(message1.getId().equals(id)){
                for(User user : message1.getTo())
                    if(!getLoggedEmail().equals(user.getEmail()))
                        usersToReply.add(user);
                usersToReply.add(message1.getFrom());
                reply = new Message(messageService.size()+1,userService.findOne(getLoggedEmail()), usersToReply,messageReply,message1, LocalDateTime.now().format(DATE_TIME_FORMATTER));
                break;
            }
        }
        this.messageService.add(reply);
    }

    public List<Message> viewConversation(String email1, String email2){
        User user1 = userService.findOne(email1);
        User user2 = userService.findOne(email2);

        Iterable<Message> messageList = (Iterable<Message>) messageService.getAll();
        List<Message> conversation = new ArrayList<>();
        for(Message message : messageList){
            if((message.getFrom().equals(user2) && message.getTo().contains(user1)) || (message.getFrom().equals(user1) && message.getTo().contains(user2)) ){
                conversation.add(message);
            }
        }
        return conversation.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }


    public void sendRequest(String email) throws NotExistenceException,ExistenceException{
        userService.findOne(email);
        String loggedEmail= getLoggedEmail();
        try{
            friendshipService.findOne(new Tuple<>(loggedEmail,email));
            throw new ExistenceException();
        }catch (NotExistenceException exc){
            friendRequestService.add(new FriendRequest("pending",new Tuple<>(loggedEmail,email),LocalDateTime.now().format(DATE_TIME_FORMATTER)));
        }
    }

    public void unsendRequest(String email) {
        userService.findOne((email));
        FriendRequest friendRequestToDelete=friendRequestService.findOne(new Tuple<>(getLoggedEmail(),email));
        friendRequestService.remove(friendRequestToDelete);
    }

    public Iterable<User> getUsersWhoSentRequests() throws LogInException{
        Set<User> userSet=new HashSet<>();
        String loggedEmail=getLoggedEmail();
        Set<FriendRequest> allFriendRequests= (Set<FriendRequest>) friendRequestService.getAll();
        for (FriendRequest friendRequest:allFriendRequests) {
            if(friendRequest.getStatus().equals("pending") && friendRequest.getId().getRight().equals(loggedEmail))
                userSet.add(userService.findOne(friendRequest.getId().getLeft()));
        }
        return userSet;
    }

    public void acceptRequest(String userEmail) throws LogInException,EntityNullException,NotExistenceException{
        String loggedEmail=getLoggedEmail();
        userService.findOne(userEmail);
        FriendRequest friendRequest=friendRequestService.findOne(new Tuple<>(userEmail,loggedEmail));
        friendRequest.addListener(friendshipService);
        if(!friendRequest.getStatus().equals("pending"))
            throw new ExistenceException();
        friendRequest.setStatus("approved");
        friendRequestService.update(friendRequest);
    }

    public void declineRequest(String userEmail) throws LogInException,EntityNullException,NotExistenceException{
        String loggedEmail=getLoggedEmail();
        userService.findOne(userEmail);
        FriendRequest friendRequest=friendRequestService.findOne(new Tuple<>(userEmail,loggedEmail));
        if(!friendRequest.getStatus().equals("pending"))
            throw new ExistenceException();
        friendRequest.setStatus("declined");
        friendRequestService.update(friendRequest);
    }

    /**
     *
     * @param firstName - the user's firstname
     * @param lastName - the user's firstname
     * @return the email of the user with the specified firstname and lastname
     * @throws NotExistenceException if there is no user with the specified firstname and lastname
     * @throws EntityNullException if the firstname or lastname is null
     */
    public String getUserEmail(String firstName,String lastName) throws NotExistenceException,EntityNullException{
        if(firstName == null || lastName==null)
            throw new EntityNullException();
        Set<User> users= (Set<User>) getAllUsers();
        for (User user:users) {
            if(user.getFirstName().equals(firstName) && user.getLastName().equals(lastName))
                return user.getEmail();
        }
        throw new NotExistenceException();
    }
}
