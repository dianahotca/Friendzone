package com.example.socialnetworkguiapplication;

import domain.*;
import domain.validators.ValidationException;
import domain.validators.exceptions.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import repository.paging.Page;
import repository.paging.Pageable;
import service.*;

import java.io.IOException;
import java.sql.Timestamp;
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
    private EventService eventService;
    private String loggedId;
    private String loggedPassword;

    public Controller(UserService userService, FriendshipService friendshipService, MessageService messageService, FriendRequestService friendRequestService,EventService eventService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.friendRequestService=friendRequestService;
        this.eventService=eventService;
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
        Iterable<FriendRequest> friendRequests = friendRequestService.getAll();
        for(FriendRequest friendRequest :friendRequests){
            if(friendRequest.getId().getLeft().equals(getLoggedEmail()) && friendRequest.getId().getRight().equals(email) || friendRequest.getId().getRight().equals(getLoggedEmail()) && friendRequest.getId().getLeft().equals(email)){
                friendRequestService.remove(friendRequest);
                break;
            }
        }
    }

    public List<UserDto> getFriends(String email){
        List<UserDto> friends = new ArrayList<>();
        List<Friendship> friendships = friendshipService.getFriends(email);
        for(Friendship friendship : friendships){
            if(friendship.getUserEmails().getRight().equals(email)){
                User user = getUser(friendship.getUserEmails().getLeft());
                UserDto userDto = new UserDto(user.getFirstName(),user.getLastName(),friendship.getDate());
                friends.add(userDto);
            }
            else{
                User user = getUser(friendship.getUserEmails().getRight());
                UserDto userDto = new UserDto(user.getFirstName(),user.getLastName(),friendship.getDate());
                friends.add(userDto);
            }

        }
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

    public Page<UserModel> getFriends(Pageable<UserModel> pageable, String email){
        return friendshipService.getFriends(pageable,email);
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
        Message newMessage = new Message( this.messageService.size()+1,loggedUser,usersList,message,null,LocalDateTime.now().format(DATE_TIME_FORMATTER));
        messageService.add(newMessage);
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
        Message messageToReply = (Message) messageService.findOne(id);
        reply = new Message(messageService.size()+1,userService.findOne(getLoggedEmail()), Arrays.asList(messageToReply.getFrom()),messageReply,messageToReply, LocalDateTime.now().format(DATE_TIME_FORMATTER));
        this.messageService.add(reply);
    }

    public void replyAll(Long id, String messageReply){
        List<User> usersToReply = new ArrayList<>();
        Message messageToReply = (Message) messageService.findOne(id);
        usersToReply.addAll(messageToReply.getTo());
        usersToReply.remove(getUser(getLoggedEmail()));
        usersToReply.add(messageToReply.getFrom());
        Message reply = new Message(messageService.size()+1,userService.findOne(getLoggedEmail()), usersToReply,messageReply,messageToReply, LocalDateTime.now().format(DATE_TIME_FORMATTER));
        this.messageService.add(reply);
    }

    public List<Message> viewConversation(String email1, String email2){
       List<Message> conversation = messageService.conversation(email1,email2);
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

    public List<FriendRequestModel> sentRequests(String email){
        return friendRequestService.sentFriendRequest(email);
    }


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

    public void addObserver(Observer obj){
        userService.addObserver(obj);
        friendshipService.addObserver(obj);
        messageService.addObserver(obj);
        friendRequestService.addObserver(obj);
        eventService.addObserver(obj);
    }

    public void saveConversation(String email1,String email2, Timestamp start, Timestamp end) throws IOException {
        List<Message> conversationAux = viewConversation(email1,email2);
        StringBuilder body = new StringBuilder();

        List<Message> conversation = new ArrayList<>();
        int m = 0;
        for(Message message:conversationAux) {
            if (message.getTo().contains(getUser(getLoggedEmail())) && message.getDate().isAfter(start.toLocalDateTime()) && message.getDate().isBefore(end.toLocalDateTime())) {
                m++;
                conversation.add(message);
            }
        }

        if(m == 0)
            body.append("You have no messages!").append("\n");
        else {
            body.append("You have: ").append(m).append(" messages").append("\n");
            User friend = getUser(email2);
            for (Message message : conversation) {
                body.append("New message from: ").append(friend.getFirstName()).append(" ").append(friend.getLastName()).append("\nContent: \n\"").append(message.getMessage())
                        .append("\"\n").append("Time: ").append(message.getDate().format(DATE_TIME_FORMATTER)).append("\n\n");
            }
        }

        saveTextToPdf(body.toString());

    }

    public void saveActivity(User user, Timestamp start, Timestamp end) throws IOException {
        List<Message> activityMessages = new ArrayList<>();
        Iterable<Message> messages = messageService.getAll();
        for(Message message : messages){
            if(message.getTo().contains(user) && message.getDate().isBefore(end.toLocalDateTime()) && message.getDate().isAfter(start.toLocalDateTime()) )
                activityMessages.add(message);
        }
        int m = 0;
        List<Friendship> activityFriendships = new ArrayList<>();
        Iterable<Friendship> friendships = friendshipService.getAll();
        for(Friendship friendship : friendships){
            if((friendship.getUserEmails().getRight().equals(getLoggedEmail()) || friendship.getUserEmails().getLeft().equals(getLoggedEmail())) && LocalDateTime.parse(friendship.getDate(), DATE_TIME_FORMATTER).isAfter(start.toLocalDateTime()) && LocalDateTime.parse(friendship.getDate(), DATE_TIME_FORMATTER).isBefore(end.toLocalDateTime())) {
                m++;
                activityFriendships.add(friendship);
            }
        }

        StringBuilder body = new StringBuilder();
        for (Message message : activityMessages) {
            body.append("New message from: ").append(message.getFrom().getFirstName()).append(" ").append(message.getFrom().getLastName()).append("\nContent: \n\"")
                    .append(message.getMessage()).append("\"\n").append("Time: ").append(message.getDate()).append("\n\n");
        }

        List<Friendship> newFrienships = new ArrayList<>();
        User friend = null;
        int f = 0;
        for (Friendship friendship : friendships) {
            if (friendship.getUserEmails().getRight().equals(getLoggedEmail()) || friendship.getUserEmails().getLeft().equals(getLoggedEmail())) {
                newFrienships.add(friendship);
                f++;
            }
        }

        for(Friendship friendship : newFrienships){
            if(friendship.getUserEmails().getRight().equals(getLoggedEmail()))
                 friend = getUser(friendship.getUserEmails().getLeft());
            else friend = getUser(friendship.getUserEmails().getRight());
            body.append("You became friend with ").append(friend.getFirstName()).append(" ").append(friend.getLastName()).append(" on ")
                    .append(friendship.getDate()).append("\n");
        }

        saveTextToPdf(body.toString());
    }

    private void saveTextToPdf(String text) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, 700);
        contentStream.setLeading(14.5f);
        List<String> chunks = List.of(text.split("\n"));

        for (int index = 0, availableRows = 25; index < chunks.size(); ++index, --availableRows) {
            if (availableRows == 0) {
                availableRows = 25;
                contentStream.endText();
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 700);
                contentStream.setLeading(14.5f);
            }

            contentStream.showText(chunks.get(index));
            contentStream.newLine();
            contentStream.newLine();
        }

        contentStream.endText();
        contentStream.close();

        document.save("D:/ProiectExtins/report.pdf");
        document.close();
    }


    public void addEvent(Integer id,String description,String date) throws ValidationException, EntityNullException, ExistenceException
    {
        Event event=new Event(id,description,date);
        eventService.add(event);
    }

    public void deleteEvent(Integer id,String description,String date) throws ValidationException, EntityNullException, ExistenceException
    {
        Event event=new Event(id,description,date);
        eventService.remove(event);
    }

    public void getEvent(Integer id) throws EntityNullException, NotExistenceException
    {
        if(id<=0)
            throw new ValidationException("Invalid id!");
        Event event=eventService.findOne(id);
    }

    public Iterable<Event> getAllEvents(){
        return eventService.getAll();
    }

    public void updateEvent(Integer id,String newDate) throws EntityNullException, NotExistenceException{
        Event foundEvent=eventService.findOne(id);
        foundEvent.setDate(newDate);
        eventService.update(foundEvent);
    }

    public void subscribeEvent(Integer eventId, String userEmail) throws EntityNullException, NotExistenceException, ValidationException{
        userService.findOne(userEmail);
        if(eventId<=0)
            throw new ValidationException("Invalid event id!");
        eventService.addEventAttendee(eventId,userEmail);
    }

    public Set<User> getEventAttendees(Integer eventId) throws EntityNullException, NotExistenceException{
        Set<String> attendeesEmails=eventService.getEventAttendees(eventId);
        Set<User> attendees=new HashSet<>();
        for (String email:attendeesEmails) {
            attendees.add(userService.findOne(email));
        }
        return attendees;
    }

    public Iterable<Event> getSubscribedEvents() {
        return eventService.getSubscribedEvents(loggedId);
    }

    public Long getPublicEventsNumber(){
        return eventService.getEventsNumber();
    }

    public void unsubscribeEvent(Integer eventId, String userEmail) throws ValidationException,NotExistenceException,EntityNullException{
        userService.findOne(userEmail);
        if(eventId<=0)
            throw new ValidationException("Invalid event id!");
        eventService.removeEventAttendee(eventId,userEmail);
    }

    public Set<Event> getTomorrowEvents() {
        return eventService.getNextDayEvents(loggedId);
    }
}
