package service;

import domain.Event;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import repository.db.EventDbRepository;

import java.util.Observable;
import java.util.Set;

public class EventService extends Observable implements Service<Integer, Event>{
    private EventDbRepository eventRepository;

    public EventService(EventDbRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Iterable<Event> getAll() {
        return eventRepository.getAllEntities();
    }

    @Override
    public void add(Event event) throws ValidationException, EntityNullException, ExistenceException {
        this.eventRepository.save(event);
        setChanged();
        notifyObservers();
    }

    @Override
    public void remove(Event event) throws EntityNullException, NotExistenceException {
        this.eventRepository.delete(event.getId());
        setChanged();
        notifyObservers();
    }

    @Override
    public Event findOne(Integer integer) throws EntityNullException, NotExistenceException {
        return eventRepository.findOne(integer);
    }

    @Override
    public void update(Event event) throws EntityNullException, NotExistenceException, ValidationException {
        eventRepository.update(event);
        setChanged();
        notifyObservers();
    }

    public void addEventAttendee(Integer eventId,String userEmail) throws EntityNullException, NotExistenceException{
        findOne(eventId);
        eventRepository.addEventAttendee(eventId,userEmail);
        setChanged();
        notifyObservers();
    }

    public Set<String> getEventAttendees(Integer eventId) throws EntityNullException, NotExistenceException{
        return eventRepository.getEventAttendees(eventId);
    }

    public Set<Event> getSubscribedEvents(String loggedId) {
        return (Set<Event>) eventRepository.getSubscribedEvents(loggedId);
    }

    public Long getEventsNumber() {
        return eventRepository.getEntitiesCount();
    }

    public void removeEventAttendee(Integer eventId, String userEmail) {
        findOne(eventId);
        eventRepository.removeEventAttendee(eventId,userEmail);
        setChanged();
        notifyObservers();
    }

    public Set<Event> getNextDayEvents(String userEmail) {
        return eventRepository.getNextDayEvents(userEmail);
    }
}
