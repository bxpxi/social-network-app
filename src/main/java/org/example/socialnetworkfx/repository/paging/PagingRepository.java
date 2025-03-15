package org.example.socialnetworkfx.socialnetworkfx.repository.paging;

import org.example.socialnetworkfx.socialnetworkfx.domain.Entity;
import org.example.socialnetworkfx.socialnetworkfx.repository.NewRepository;

public interface PagingRepository<ID, E extends Entity<ID>> extends NewRepository<ID, E> {
    Page<E> findAll(Pageable pageable);
}
