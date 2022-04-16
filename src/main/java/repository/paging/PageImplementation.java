package repository.paging;

import java.util.List;

public record PageImplementation<E>(Pageable<E> pageable,
                          List<E> content) implements Page<E> {
    @Override
    public Pageable<E> getPageable() {
        return pageable;
    }

    @Override
    public Pageable<E> nextPageable() {
        return new PageableImplementation<>(this.pageable.getPageNumber() + 1, this.pageable.getPageSize());
    }

    @Override
    public Pageable<E> previousPageable() {
        return new PageableImplementation<>(this.pageable.getPageNumber() - 1, this.pageable.getPageSize());
    }

    @Override
    public List<E> getContent() {
        return content;
    }
}
