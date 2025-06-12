package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.CourseRepository;
import com.mycompany.myapp.service.CourseService;
import com.mycompany.myapp.service.dto.CourseDTO;
import com.mycompany.myapp.service.mapper.CourseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Course}.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public Mono<CourseDTO> save(CourseDTO courseDTO) {
        LOG.debug("Request to save Course : {}", courseDTO);
        return courseRepository.save(courseMapper.toEntity(courseDTO)).map(courseMapper::toDto);
    }

    @Override
    public Mono<CourseDTO> update(CourseDTO courseDTO) {
        LOG.debug("Request to update Course : {}", courseDTO);
        return courseRepository.save(courseMapper.toEntity(courseDTO)).map(courseMapper::toDto);
    }

    @Override
    public Mono<CourseDTO> partialUpdate(CourseDTO courseDTO) {
        LOG.debug("Request to partially update Course : {}", courseDTO);

        return courseRepository
            .findById(courseDTO.getId())
            .map(existingCourse -> {
                courseMapper.partialUpdate(existingCourse, courseDTO);

                return existingCourse;
            })
            .flatMap(courseRepository::save)
            .map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CourseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Courses");
        return courseRepository.findAllBy(pageable).map(courseMapper::toDto);
    }

    public Flux<CourseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseRepository.findAllWithEagerRelationships(pageable).map(courseMapper::toDto);
    }

    public Mono<Long> countAll() {
        return courseRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CourseDTO> findOne(Long id) {
        LOG.debug("Request to get Course : {}", id);
        return courseRepository.findOneWithEagerRelationships(id).map(courseMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Course : {}", id);
        return courseRepository.deleteById(id);
    }
}
