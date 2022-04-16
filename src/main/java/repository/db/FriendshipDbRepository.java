package repository.db;
import com.example.socialnetworkguiapplication.FriendRequestListener;
import domain.*;
import domain.Friendship;
import domain.validators.Validator;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Tuple<String,String>, Friendship>{
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
}