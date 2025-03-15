package org.example.socialnetworkfx.socialnetworkfx.repository.paging;

import java.util.stream.Stream;

public interface Page<E> {
    Pageable getPageable();
    Pageable getNextPageable();
    Pageable getPreviousPageable();
    Stream<E> getElements();
}
