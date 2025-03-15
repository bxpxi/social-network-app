package org.example.socialnetworkfx.socialnetworkfx.repository.paging;

import java.util.stream.Stream;

public class PageImplementation<T> implements Page<T> {
    private Pageable pageable;
    private Stream<T> elements;

    public PageImplementation(Pageable pageable, Stream<T> elements) {
        this.pageable = pageable;
        this.elements = elements;
    }

    @Override
    public Pageable getPageable() {
        return this.pageable;
    }

    @Override
    public Stream<T> getElements() {
        return this.elements;
    }

    @Override
    public Pageable getNextPageable() {
        return new PageableImplementation(this.pageable.getPageNumber() + 1, this.pageable.getPageSize());
    }

    @Override
    public Pageable getPreviousPageable() {
        return new PageableImplementation(this.pageable.getPageNumber() - 1, this.pageable.getPageSize());
    }
}
