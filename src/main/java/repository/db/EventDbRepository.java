package repository.db;

import com.example.socialnetworkguiapplication.FriendRequestModel;
import com.example.socialnetworkguiapplication.UserModel;
import domain.Event;
import domain.Message;
import domain.validators.ValidationException;
import domain.validators.Validator;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.Repository;
import repository.paging.Page;
import repository.paging.Pageable;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class EventDbRepository implements Repository<Integer, Event> {
    private String url;
    private String username;
    private String password;
    private Validator<Event> validator;

    public EventDbRepository(String url, String username, String password, Validator<Event> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Long getEntitiesCount() {
        String sql = "SELECT COUNT(id) FROM events";
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            size = resultSet.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (long) size;
    }

    @Override
    public Event findOne(Integer id) throws EntityNullException, NotExistenceException {
        if(id==null)
            throw new EntityNullException();
        String sql = "SELECT * FROM events WHERE id='" + id + "'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if(!resultSet.next())
                throw new NotExistenceException();
            else{
                String description = resultSet.getString("description");
                String date = resultSet.getString("date");
                return new Event(id,description,date);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> getAllEntities() {
        Set<Event> eventsList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id=resultSet.getInt("id");
                String description = resultSet.getString("description");
                String date = resultSet.getString("date");
                Event event = new Event(id,description, date);
                eventsList.add(event);
            }
            return eventsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsList;
    }

    @Override
    public void save(Event entity) throws ValidationException, EntityNullException, ExistenceException {
        if (entity == null)
            throw new EntityNullException();

        validator.validate(entity);

        try{
            findOne(entity.getId());
            throw new ExistenceException();
        }catch(NotExistenceException exc) {
            String sql = "insert into events (id,description,date) values (?, ?, ?)";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, (entity).getId());
                ps.setString(2, (entity).getDescription());
                ps.setString(3, (entity).getDate());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(Integer id) throws EntityNullException, NotExistenceException {
        findOne(id);
        String sql = "delete from events where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Event entity) throws EntityNullException, ValidationException, NotExistenceException {
        validator.validate(entity);
        findOne(entity.getId());
        String sql = "update events set date=? where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getDate());
            ps.setInt(2, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEventAttendee(Integer eventId,String userEmail) throws EntityNullException, NotExistenceException{
        findOne(eventId);
        if(userEmail==null)
            throw new EntityNullException();
        String sql = "insert into event_attendees (event_id,user_email) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getEventAttendees(Integer eventId) throws EntityNullException, NotExistenceException {
        findOne(eventId);
        Set<String> attendees = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from event_attendees where event_id = '" + eventId + "'");
             ResultSet resultSet = statement.executeQuery()) {
             while (resultSet.next()) {
                 String email=resultSet.getString("userEmail");
                 attendees.add(email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendees;
    }

    @Override
    public List<Event> getConversation(String email1, String email2) {
        return null;
    }

    @Override
    public List<Event> getFriends(String email) {
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

    public Iterable<Event> getSubscribedEvents(String userEmail) {
        Set<Event> subscribedEvents = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * from event_attendees as EA\n" +
                     "    inner join events as E\n" +
                     "        on EA.event_id=E.id\n" +
                     "where EA.user_email = '" + userEmail + "'");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id=resultSet.getInt("id");
                String description=resultSet.getString("description");
                String date=resultSet.getString("date");
                subscribedEvents.add(new Event(id,description,date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscribedEvents;
    }

    public void removeEventAttendee(Integer eventId, String userEmail) {
        findOne(eventId);
        if(userEmail==null)
            throw new EntityNullException();
        String sql = "delete from event_attendees where event_id=? and user_email=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSubscribedEventsDates(String userEmail){
        List<String> subscribedEventsDates=new ArrayList<>();
        Set<Event> subscribedEvents= (Set<Event>) getSubscribedEvents(userEmail);
        for (Event event:subscribedEvents) {
            subscribedEventsDates.add(event.getDate());
        }
        return subscribedEventsDates;
    }

    public Set<Event> getNextDayEvents(String userEmail) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String tomorrow = simpleDateFormat.format(calendar.getTime());
        Set<Event> upcomingEvents = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * from event_attendees as EA\n" +
                             "    inner join events as E\n" +
                             "        on EA.event_id=E.id\n" +
                             "where EA.user_email = '" + userEmail + "'" +
                             "and E.date ='" + tomorrow +"'");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id=resultSet.getInt("id");
                String description=resultSet.getString("description");
                String date=resultSet.getString("date");
                upcomingEvents.add(new Event(id,description,date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upcomingEvents;
    }
}
