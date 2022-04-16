package service;

import domain.Entity;
import repository.Repository;

public class MessageService <ID, E extends Entity<ID>> implements Service<ID, E> {
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
}
