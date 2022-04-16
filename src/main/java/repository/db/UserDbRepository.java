package repository.db;
import domain.User;
import domain.validators.Validator;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;

import java.sql.*;
import java.util.*;

public class UserDbRepository implements Repository<String, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    private Validator<String> emailValidator;
    private String loggedId;

    public UserDbRepository(String url, String username, String password, Validator<User> validator, Validator<String> emailValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.emailValidator=emailValidator;
        this.loggedId = "";
    }

    @Override
    public Iterable<User> getAllEntities() {
        Set<User> usersList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, email,password);
                usersList.add(user);
            }
            return usersList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    @Override
    public void save(User entity) {
        if (entity == null)
            throw new EntityNullException();

        validator.validate(entity);

        try{
            findOne(entity.getEmail());
            throw new ExistenceException();
        }catch(NotExistenceException exc) {
            String sql = "insert into users (firstname, lastname, email, password ) values (?, ?, ?, ?)";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, (entity).getFirstName());
                ps.setString(2, (entity).getLastName());
                ps.setString(3, (entity).getEmail());
                ps.setString(4, (entity).getPassword());

                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(String userId) {
        findOne(userId);
        String sql = "delete from users where email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User entity) {
        validator.validate(entity);
        findOne(entity.getEmail());
        String sql = "update users set password=? where email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getPassword());
            ps.setString(2, entity.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long getEntitiesCount(){
      String sql = "SELECT COUNT(email) FROM users";
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
    public User findOne(String userEmail) {
        emailValidator.validate(userEmail);
        if(userEmail.equals(""))
            throw new EntityNullException();
        String sql = "SELECT * FROM users WHERE email='" + userEmail + "'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(!resultSet.next())
                throw new NotExistenceException();
            else{
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                return new User(firstName, lastName, userEmail,password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}