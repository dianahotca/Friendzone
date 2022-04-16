package repository.db;
import com.example.socialnetworkguiapplication.FriendRequestModel;
import com.example.socialnetworkguiapplication.UserModel;
import domain.*;
import domain.Friendship;
import domain.validators.Validator;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;
import repository.paging.Page;
import repository.paging.PageImplementation;
import repository.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Tuple<String,String>, Friendship> , FriendshipPagingRepository{
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;


    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<String,String> userIds) {
        String sql = "SELECT * FROM friendships WHERE (leftv = ? and rightv=?) or (leftv=? and rightv=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, userIds.getLeft());
                ps.setString(2, userIds.getRight());
                ps.setString(3, userIds.getRight());
                ps.setString(4, userIds.getLeft());
                ResultSet resultSet = ps.executeQuery();
                if(resultSet.next()==false)
                    throw new NotExistenceException();
                String leftEmail=resultSet.getString(1);
                String rightEmail=resultSet.getString(2);
                String date=resultSet.getString(3);
                return new Friendship(new Tuple<>(leftEmail,rightEmail),date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Iterable<Friendship> getAllEntities() {
        Set<Friendship> friendshipsList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String left = resultSet.getString("leftv");
                String right = resultSet.getString("rightv");
                String date = resultSet.getString("datef");
                Tuple<String,String> id=new Tuple<>(left, right);
                Friendship friendship = new Friendship(id, date);
                friendshipsList.add(friendship);
            }
            return friendshipsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipsList;
    }

   /* @Override
    public Page<Friendship> getAllEntities(Pageable<Friendship> pageable) {
        Set<Friendship> friendshipsList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String left = resultSet.getString("leftv");
                String right = resultSet.getString("rightv");
                String date = resultSet.getString("datef");
                Tuple<String,String> id=new Tuple<>(left, right);
                Friendship friendship = new Friendship(id, date);
                friendshipsList.add(friendship);
            }
            return new PageImplementation<>(pageable, friendshipsList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PageImplementation<>(pageable, friendshipsList);
    }*/


    @Override
    public void save(Friendship entity) {
        if (entity == null)
            throw new EntityNullException();

        validator.validate(entity);

        try{
            findOne(entity.getUserEmails());
            throw new ExistenceException();
        }catch(NotExistenceException exc) {
            String sql = "insert into friendships (leftv, rightv, datef ) values (?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, entity.getId().getLeft());
                ps.setString(2, entity.getId().getRight());
                ps.setString(3, String.valueOf(entity.getDate()));
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(Tuple<String,String> userIds) {
        findOne(userIds);

        String sql = "delete from friendships where (leftv = ? and rightv=?) or (leftv=? and rightv=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userIds.getLeft());
            ps.setString(2, userIds.getRight());
            ps.setString(3, userIds.getRight());
            ps.setString(4, userIds.getLeft());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Friendship entity) {
        findOne(entity.getUserEmails());

        String sql = "update friendships set datef = ? where (leftv = ? and rightv=?) or (leftv=? and rightv=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ((Friendship) entity).getDate());
            ps.setString(2, entity.getId().getLeft());
            ps.setString(3, entity.getId().getRight());
            ps.setString(4, entity.getId().getRight());
            ps.setString(5, entity.getId().getLeft());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Friendship> getConversation(String email1, String email2) {
        return null;
    }

    @Override
    public List<Friendship> getFriends(String email) {
        List<Friendship> friendshipsList = new ArrayList<>();
        String sql = """
                select * from friendships
                where rightv = ? or leftv = ?
                """;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, email);
            statement.setString(2, email);
             ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String left = resultSet.getString("leftv");
                String right = resultSet.getString("rightv");
                String date = resultSet.getString("datef");
                Tuple<String,String> id=new Tuple<>(left, right);
                Friendship friendship = new Friendship(id, date);
                friendshipsList.add(friendship);
            }
            return friendshipsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipsList;
    }

    @Override
    public List<FriendRequestModel> sentFriendships(String email) {
        return null;
    }


    @Override
    public Long getEntitiesCount() {
        String sql = "SELECT COUNT(*) FROM friendships";
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
    public Page<UserModel> getFriends(Pageable<UserModel> pageable, String email) {
        List<UserModel> friendshipsList = new ArrayList<>();
        UserModel userModel = null;
        String sql = """
                select * from friendships
                where rightv = ? or leftv = ?
                 LIMIT (?) OFFSET (?)
                """;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, email);
            statement.setString(2, email);
            statement.setLong(3, pageable.getPageSize());
            statement.setLong(4, (long) pageable.getPageSize() * (pageable.getPageNumber() - 1));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String left = resultSet.getString("leftv");
                String right = resultSet.getString("rightv");
                String date = resultSet.getString("datef");

                if(left.equals(email))
                    userModel = new UserModel(right,getUserFromTable(right).getFirstName(),getUserFromTable(right).getLastName(),date);
                else
                    userModel = new UserModel(left,getUserFromTable(left).getFirstName(),getUserFromTable(left).getLastName(),date);

                friendshipsList.add(userModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PageImplementation<>(pageable,friendshipsList);
    }

    @Override
    public Page<Message> getConversation(Pageable<Message> pageable, String email1, String email2) {
        return null;
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
}