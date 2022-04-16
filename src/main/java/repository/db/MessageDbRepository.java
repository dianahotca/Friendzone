package repository.db;
import com.example.socialnetworkguiapplication.FriendRequestModel;
import com.example.socialnetworkguiapplication.UserModel;
import domain.*;
import domain.validators.exceptions.EntityNullException;
import repository.Repository;
import repository.paging.Page;
import repository.paging.Pageable;
import utils.Constants;

import java.sql.*;
import java.util.*;

public class MessageDbRepository implements Repository<Long, Message>{
    private String url;
    private String username;
    private String password;

    public MessageDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Iterable<Message> getAllEntities() {
        Set<Message> messagesList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String fromStr = resultSet.getString("fromtbl");
                String toStr = resultSet.getString("totbl");
                String messageStr = resultSet.getString("messagetbl");
                Long replyID = resultSet.getLong("replytbl");
                String date = resultSet.getString("datetbl");

                User from = this.getUserFromTable(fromStr);
                if(from!=null) {
                    List<String> userEmails = Arrays.asList(toStr.split(" "));
                    List<User> usersTo = new ArrayList<>();
                    for (String email : userEmails) {
                        if (this.getUserFromTable(email)!=null)
                            usersTo.add(this.getUserFromTable(email));
                    }

                    Message reply = this.findOne(replyID);
                    Message message = new Message(id, from, usersTo, messageStr, reply, date);
                    messagesList.add(message);
                }
            }
            return messagesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messagesList;
    }

    public User getUserFromTable(String email){
        String sql = "SELECT * FROM users WHERE email='" + email + "'";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(resultSet.next()){
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                user = new User(firstName, lastName, email,password);
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void save(Message entity) {
        if (entity == null)
            throw new EntityNullException();

        String sql = "insert into messages (id, fromtbl,totbl, messagetbl,replytbl, datetbl ) values (?, ?, ?, ? , ? ,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(entity.getId().toString()));
            ps.setString(2, String.valueOf(entity.getFrom().getEmail()));
            String usersTo = "";
            for(User user: entity.getTo()){
                usersTo += user.getEmail();
                usersTo += " ";
            }
            usersTo = usersTo.substring(0,usersTo.length()-1);

            ps.setString(3, String.valueOf(usersTo));
            ps.setString(4, String.valueOf(entity.getMessage()));
            if(entity.getReply() == null){
                ps.setInt(5,0);
            }
            else ps.setLong(5, Long.valueOf(entity.getReply().getId()));
            ps.setString(6, entity.getDate().format(Constants.DATE_TIME_FORMATTER));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long getEntitiesCount() {
        String sql = "SELECT COUNT(id) FROM messages";
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            resultSet.next();
            size = resultSet.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Long.valueOf(size);

    }

    @Override
    public Message findOne(Long id) {
        if(id == 0){
            return null;
        }
        String sql = "SELECT * FROM messages WHERE id='" + id + "'";
        Message message = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(resultSet.next()){
                String fromStr = resultSet.getString(2);
                String toStr = resultSet.getString(3);
                String messageStr = resultSet.getString(4);
                String date = resultSet.getString(6);

                User from = this.getUserFromTable(fromStr);
                List<String> userEmails = Arrays.asList(toStr.split(" "));
                List<User> usersTo = new ArrayList<>();
                for(String email: userEmails)
                    usersTo.add(this.getUserFromTable(email));

                message = new Message(id,from,usersTo,messageStr,null,date);
                return message;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void delete(Long aLong) {}

    @Override
    public void update(Message entity) {}


    @Override
    public List<Message> getConversation(String email1, String email2) {
        List<Message> messages =new ArrayList<>();

        String sql = """
               SELECT * FROM messages
               WHERE fromtbl = ? AND totbl LIKE ? OR fromtbl = ? AND totbl LIKE ?
                """;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email1);
            statement.setString(2, "%" + email2 + "%");
            statement.setString(3, email2);
            statement.setString(4, "%" + email1 + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String fromStr = resultSet.getString("fromtbl");
                String toStr = resultSet.getString("totbl");
                String messageStr = resultSet.getString("messagetbl");
                Long replyID = resultSet.getLong("replytbl");
                String date = resultSet.getString("datetbl");

                User from = this.getUserFromTable(fromStr);
                if(from != null) {

                    List<String> toSplit = Arrays.asList(toStr.split(" "));
                    List<User> to = new ArrayList<>();
                    for (String email : toSplit) {
                        if(this.getUserFromTable(email) != null)
                            to.add(this.getUserFromTable(email));
                    }

                    Message reply = this.findOne(replyID);
                    Message message = new Message(id, from, to, messageStr, reply, date);
                    messages.add(message);
                }
            }
            return messages;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return messages;
    }

    @Override
    public List<Message> getFriends(String email) {
        return null;
    }

    @Override
    public List<FriendRequestModel> sentFriendships(String email) {
        return null;
    }

    @Override
    public Page<UserModel> getFriends(Pageable<UserModel> pageable, String email) {
        return null;
    }

    @Override
    public Page<Message> getConversation(Pageable<Message> pageable, String email1, String email2) {
        return null;
    }

    /*@Override
    public Page<Message> getConversation(Pageable<Message> pageable, String email1, String email2) {
        List<Message> messages = new ArrayList<>();

        String sql = """
                SELECT * FROM messages
               WHERE ("fromtbl" = (?) AND "totbl" LIKE (?) OR "fromtbl" = (?) AND "totbl" LIKE (?))
               ORDER BY datetbl
               LIMIT (?) OFFSET(?)
                """;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email1);
            statement.setString(2, email2);
            statement.setString(3, email2);
            statement.setString(4, email1);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String fromStr = resultSet.getString("fromtbl");
                String toStr = resultSet.getString("totbl");
                String messageStr = resultSet.getString("messagetbl");
                Long replyID = resultSet.getLong("replytbl");
                String date = resultSet.getString("datetbl");

                User from = this.getUserFromTable(fromStr);
                if(from != null) {

                    List<String> toSplit = Arrays.asList(toStr.split(";"));
                    List<User> to = new ArrayList<>();
                    for (String email : toSplit) {
                        if(this.getUserFromTable(email) != null)
                            to.add(this.getUserFromTable(email));
                    }

                    Message reply = this.findOne(replyID);
                    Message message = new Message(id, from, to, messageStr, reply, date);
                    messages.add(message);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return new PageImplementation<>(pageable, messages);
    }
*/
}