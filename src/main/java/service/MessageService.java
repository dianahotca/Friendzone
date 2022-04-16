package service;

import domain.Entity;
import domain.Message;
import domain.User;
import repository.Repository;
import repository.paging.Page;
import repository.paging.Pageable;

import java.util.List;
import java.util.Observable;

public class MessageService <ID, E extends Entity<ID>> extends Observable implements Service<ID, E> {
    private Repository<ID, E> messageRepository;

    public MessageService(Repository<ID, E> messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Iterable<E> getAll() {
        return messageRepository.getAllEntities();
    }

    @Override
    public void add(E e) {
        messageRepository.save(e);
        setChanged();
        notifyObservers();
    }

    public Long size(){
        return messageRepository.getEntitiesCount();
    }

    @Override
    public void remove(E e) {
    }
    @Override
    public void update(E e) {

    }

    @Override
    public E findOne(ID id) {
        return messageRepository.findOne(id);
    }

    public List<Message> conversation(String email1, String email2){
        return (List<Message>) messageRepository.getConversation(email1,email2);
    }

    /*public Page<Message> conversation(Pageable<Message> pageable, String email1, String email2){
        return (Page<Message>) messageRepository.getConversation(pageable ,email1, email2);
    }*/
}
