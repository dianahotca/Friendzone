package ui;

import com.example.socialnetworkguiapplication.Controller;
import domain.*;
import domain.validators.ValidationException;
import domain.validators.exceptions.*;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class UserInterface implements UI<String,User,Tuple<String,String>,Friendship,Long,Message> {

    private Controller controller;
    private Scanner in;

    public UserInterface(Controller controller) {
        this.controller = controller;
        this.in = new Scanner(System.in);
    }

    private void printOptions(){
        System.out.println("----------------MENU-----------------");
        System.out.println("    Choose an option");
        System.out.println("    1.  Print all");
        System.out.println("    2.  Log In");
        System.out.println("    3.  Add user");
        System.out.println("    4.  Remove user");
        System.out.println("    5.  Add friend");
        System.out.println("    6.  Remove friend");
        System.out.println("    7.  Number of communities");
        System.out.println("    8.  The most sociable community");
        System.out.println("    9.  Update user");
        System.out.println("    10. Update date of a friendship");
        System.out.println("    11. Find user by email");
        System.out.println("    12. Friendships in a month");
        System.out.println("    13. Find one user's friends");
        System.out.println("    14. Send message");
        System.out.println("    15. Reply message");
        System.out.println("    16. Reply all");
        System.out.println("    17. View conversation");
        System.out.println("    18. View requests");
        System.out.println("    19. Exit");
        System.out.println("----------------MENU------------------");
    }

    @Override
    public void startUI() {
        boolean run = true;
        String loggedEmail;
        while(run){
            try {
                try{
                    loggedEmail=controller.getLoggedEmail();
                    System.out.println("Logged user email: "+loggedEmail);
                }catch(LogInException exc){
                    System.out.println("No user is logged!");
                }
                this.printOptions();
                System.out.println("Enter option:");
                String input = in.next();
                switch (input) {
                    case "19":
                        run = false;
                        break;
                    case "1":
                        this.printAll();
                        break;
                    case "2":
                        this.logIn();
                        break;
                    case "3":
                        this.addUser();
                        break;
                    case "4":
                        this.removeUser();
                        break;
                    case "5":
                        this.sendRequest();
                        break;
                    case "6":
                        this.removeFriend();
                        break;
                    case "7":
                        this.numberOfCommunities();
                        break;
                    case "8":
                        this.mostSociableCommunity();
                        break;
                    case "9":
                        this.updateUser();
                        break;
                    case "10":
                        this.updateFriendship();
                        break;
                    case "11":
                        this.getUser();
                        break;
                    case "12":
                        this.friendshipsByMonth();
                        break;
                    case "13":
                        this.findOneUsersFriends();
                        break;
                    case "14":
                        this.sendMessage();
                        break;
                    case "15":
                        this.replyMessage();
                        break;
                    case "16":
                        this.replyAll();
                        break;
                    case "17":
                        this.viewConversation();
                        break;
                    case "18":
                        this.viewRequests();
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            }catch (LogInException| ValidationException | IdNullException | EntityNullException  | ExistenceException | NotExistenceException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    private void printFriendRequestOptions(){
        boolean ok=true;
        while(ok) {
            try {
                System.out.println("Enter one user's email:");
                String userEmail = in.next();
                System.out.println("Choose an option:");
                System.out.println("1. Accept");
                System.out.println("2. Decline");
                String option = in.next();
                switch (option) {
                    case "1":
                        controller.acceptRequest(userEmail);
                        ok = false;
                        break;
                    case "2":
                        controller.declineRequest(userEmail);
                        ok = false;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            }catch (ValidationException | EntityNullException | NotExistenceException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    private void viewRequests() {
        Set<User> users= (Set<User>) controller.getUsersWhoSentRequests();
        for (User user:users) {
            System.out.println(user.toString());
        }
        if(users.isEmpty())
            System.out.println("No new friend requests!");
        else
            printFriendRequestOptions();
    }

    private void sendRequest() {
        System.out.println("Enter the email of the user you want to add as friend: ");
        String email=in.next();
        controller.sendRequest(email);
    }

    public void viewConversation(){
        System.out.println("Enter two emails to see conversation between them: ");
        String email1 = in.next();
        String email2 = in.next();
        List<Message> conversation = controller.viewConversation(email1,email2);
        for(Message message : conversation){
                System.out.println(message.getDate().format(Constants.DATE_TIME_FORMATTER) + " | From: " + message.getFrom().getFirstName() +" "+ message.getFrom().getLastName() + " | " + message.getMessage());
            if(message.getReply()!=null){
                System.out.println("    |");
                System.out.println("    |----> REPLIED");
                System.out.println("    " + message.getReply().getDate().format(Constants.DATE_TIME_FORMATTER) + "| From: " + message.getReply().getFrom().getFirstName() +" "+ message.getReply().getFrom().getLastName() + " | " + message.getReply().getMessage());
            }
        }
    }

    public void replyMessage(){
        List<Message> messagesOfCrtUser = controller.allMessages();
        for(Message message : messagesOfCrtUser){
            System.out.println(message.getId() + "|" + message.getDate().format(Constants.DATE_TIME_FORMATTER) + " | " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName()  + " | " + message.getMessage());
        }
        System.out.println("Enter which message you want to reply: ");
        Long id = Long.parseLong(in.next());
        System.out.println("Enter message: ");
        in.nextLine();
        String message = in.nextLine();
        controller.replyMessage(id,message);
    }

    public void replyAll(){
        List<Message> messagesOfCrtUser = controller.allMessages();
        for(Message message : messagesOfCrtUser){
            if(message.getTo().size()>1)
            System.out.println(message.getId() + "|" + message.getDate().format(Constants.DATE_TIME_FORMATTER) + " | " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName()  + " | " + message.getMessage());
        }
        System.out.println("Enter which conversation group you want to reply: ");
        Long id = Long.parseLong(in.next());

        System.out.println("Enter message: ");
        in.nextLine();
        String message = in.nextLine();
        controller.replyAll(id,message);
    }
    public void sendMessage() {
        List<User> usersToSend = new ArrayList<>();
        boolean ok = true;
        while (ok){
            if (ok) {
                System.out.println("Enter friend's email to send message");
                String email = in.next();
                User user = controller.getUser(email);
                usersToSend.add(user);
                System.out.println("Do you want to sent this message to other friends? / Press yes or no");
                String answer = in.next();
                if (!answer.equals("yes"))
                    ok = false;
            }
        }

        System.out.println("Enter message");
        in.nextLine();
        String message = in.nextLine();
        controller.sendMessage(usersToSend,message);
    }

    public void findOneUsersFriends() {
        System.out.println("Enter email:");
        String email=in.next();
        User user = controller.getUser(email);
        List<UserDto> friends = controller.getFriends(user.getEmail());
        if (friends.isEmpty())
            System.out.println("This user has no friends!");
        else {
            System.out.println("Friends: ");
            friends.stream().forEach(System.out::println);
        }
    }

    public void getUser() {
        System.out.println("Enter email user:");
        String email = in.next();
        System.out.println(controller.getUser(email).toString());
    }

    public void updateUser(){
        System.out.println("Enter email:");
        String email = in.next();
        System.out.println("Enter new first name:");
        String newF = in.next();
        System.out.println("Enter new last name:");
        String newL = in.next();
        controller.updateUser(email, newF, newL);
        System.out.println("User updated successfully!");
    }

    public void updateFriendship(){
        System.out.println("Enter first email");
        String email1 = in.next();
        System.out.println("Enter second email");
        String email2 = in.next();
        controller.updateFriendship(email1, email2);
        System.out.println("Friendship updated successfully!");
    }

    @Override
    public void numberOfCommunities(){
        System.out.println("The number of communities is: " + controller.numberOfCommunities());
    }

    @Override
    public void mostSociableCommunity(){
        System.out.println("The most sociable community is: ");
        List<User> rez = controller.mostSociableCommunity();
        for(User user: rez)
           System.out.println(user.toString());

    }

    @Override
    public void printAll() {
        Iterable<User> users = controller.getAllUsers();
        for(User user: users)
            System.out.println(user.toString());
        Iterable<Friendship> friendships = controller.getAllFriendships();
        for(Friendship friendship:friendships){
            System.out.println(friendship.toString());
            System.out.println();
        }
    }

    @Override
    public void logIn() {
        this.printAll();
        System.out.println("Enter email to log in: ");
        String email = in.next();
        controller.setLoggedEmail(email);
    }

    @Override
    public void addUser() {
        System.out.println("Enter first name: ");
        String firstName = in.next();
        System.out.println("Enter last name: ");
        String lastName = in.next();
        System.out.println("Enter email: ");
        String email = in.next();
        System.out.println("Enter password: ");
        String password=in.next();
        controller.addUser(firstName, lastName, email,password);
        System.out.println("User added successfully!");
    }

    @Override
    public void removeUser() {
        System.out.println("Enter email: ");
        String email = in.next();
        controller.removeUser(email);
        System.out.println("User deleted successfully!");
    }

    @Override
    public void addFriend() {
        System.out.println("Enter email: ");
        String newFriendEmail = in.next();
        controller.addFriend(newFriendEmail);
        System.out.println("Friend added successfully!");
    }

    @Override
    public void removeFriend() {
        System.out.println("Enter email: ");
        String email = in.next();
        controller.removeFriend(email);
        System.out.println("Friend deleted successfully!");
    }

    @Override
    public void friendshipsByMonth(){
        System.out.println("Enter email: ");
        String email = in.next();
        System.out.println("Enter month: ");
        String month = in.next();
        List<UserDto> friendshipsByMonth = controller.friendshipsByMonth(email, month);
        for (UserDto user : friendshipsByMonth) {
            System.out.println(user.toString());
        }
    }
}
