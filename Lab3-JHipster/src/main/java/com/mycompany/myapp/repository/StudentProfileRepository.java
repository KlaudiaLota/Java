package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StudentProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the StudentProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentProfileRepository extends ReactiveCrudRepository<StudentProfile, Long>, StudentProfileRepositoryInternal {
    Flux<StudentProfile> findAllBy(Pageable pageable);

    @Query("SELECT * FROM student_profile entity WHERE entity.id not in (select student_id from student)")
    Flux<StudentProfile> findAllWhereStudentIsNull();

    @Override
    <S extends StudentProfile> Mono<S> save(S entity);

    @Override
    Flux<StudentProfile> findAll();

    @Override
    Mono<StudentProfile> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StudentProfileRepositoryInternal {
    <S extends StudentProfile> Mono<S> save(S entity);

    Flux<StudentProfile> findAllBy(Pageable pageable);

    Flux<StudentProfile> findAll();

    Mono<StudentProfile> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<StudentProfile> findAllBy(Pageable pageable, Criteria criteria);
}
