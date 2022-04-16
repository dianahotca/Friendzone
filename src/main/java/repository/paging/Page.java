package repository.paging;

import java.util.List;

public interface Page<E> {
    Pageable<E> getPageable();

    Pageable<E> nextPageable();

    Pageable<E> previousPageable();

    List<E> getContent();
}
