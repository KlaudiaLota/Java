package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.InstructorDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Instructor}.
 */
public interface InstructorService {
    /**
     * Save a instructor.
     *
     * @param instructorDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InstructorDTO> save(InstructorDTO instructorDTO);

    /**
     * Updates a instructor.
     *
     * @param instructorDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InstructorDTO> update(InstructorDTO instructorDTO);

    /**
     * Partially updates a instructor.
     *
     * @param instructorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InstructorDTO> partialUpdate(InstructorDTO instructorDTO);

    /**
     * Get all the instructors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstructorDTO> findAll(Pageable pageable);

    /**
     * Returns the number of instructors available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" instructor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InstructorDTO> findOne(Long id);

    /**
     * Delete the "id" instructor.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
