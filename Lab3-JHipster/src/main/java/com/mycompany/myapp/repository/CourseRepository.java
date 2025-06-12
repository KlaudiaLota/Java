package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course, Long>, CourseRepositoryInternal {
    Flux<Course> findAllBy(Pageable pageable);

    @Override
    Mono<Course> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Course> findAllWithEagerRelationships();

    @Override
    Flux<Course> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM course entity JOIN rel_course__student joinTable ON entity.id = joinTable.student_id WHERE joinTable.student_id = :id"
    )
    Flux<Course> findByStudent(Long id);

    @Query("SELECT * FROM course entity WHERE entity.instructor_id = :id")
    Flux<Course> findByInstructor(Long id);

    @Query("SELECT * FROM course entity WHERE entity.instructor_id IS NULL")
    Flux<Course> findAllWhereInstructorIsNull();

    @Override
    <S extends Course> Mono<S> save(S entity);

    @Override
    Flux<Course> findAll();

    @Override
    Mono<Course> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CourseRepositoryInternal {
    <S extends Course> Mono<S> save(S entity);

    Flux<Course> findAllBy(Pageable pageable);

    Flux<Course> findAll();

    Mono<Course> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Course> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Course> findOneWithEagerRelationships(Long id);

    Flux<Course> findAllWithEagerRelationships();

    Flux<Course> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
