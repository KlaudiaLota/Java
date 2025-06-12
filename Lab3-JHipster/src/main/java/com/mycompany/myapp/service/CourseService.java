package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CourseDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Course}.
 */
public interface CourseService {
    /**
     * Save a course.
     *
     * @param courseDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CourseDTO> save(CourseDTO courseDTO);

    /**
     * Updates a course.
     *
     * @param courseDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CourseDTO> update(CourseDTO courseDTO);

    /**
     * Partially updates a course.
     *
     * @param courseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CourseDTO> partialUpdate(CourseDTO courseDTO);

    /**
     * Get all the courses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CourseDTO> findAll(Pageable pageable);

    /**
     * Get all the courses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CourseDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of courses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" course.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CourseDTO> findOne(Long id);

    /**
     * Delete the "id" course.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
