package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.StudentProfileDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.StudentProfile}.
 */
public interface StudentProfileService {
    /**
     * Save a studentProfile.
     *
     * @param studentProfileDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StudentProfileDTO> save(StudentProfileDTO studentProfileDTO);

    /**
     * Updates a studentProfile.
     *
     * @param studentProfileDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StudentProfileDTO> update(StudentProfileDTO studentProfileDTO);

    /**
     * Partially updates a studentProfile.
     *
     * @param studentProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StudentProfileDTO> partialUpdate(StudentProfileDTO studentProfileDTO);

    /**
     * Get all the studentProfiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<StudentProfileDTO> findAll(Pageable pageable);

    /**
     * Get all the StudentProfileDTO where Student is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<StudentProfileDTO> findAllWhereStudentIsNull();

    /**
     * Returns the number of studentProfiles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" studentProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StudentProfileDTO> findOne(Long id);

    /**
     * Delete the "id" studentProfile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
