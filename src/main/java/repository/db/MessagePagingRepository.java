package repository.db;

import domain.Message;
import domain.User;
import repository.paging.Page;
import repository.paging.Pageable;

public interface MessagePagingRepository {

    Page<Message> getConversation(Pageable<Message> pageable, String email1, String email2);
}
