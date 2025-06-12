package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.InstructorRepository;
import com.mycompany.myapp.service.InstructorService;
import com.mycompany.myapp.service.dto.InstructorDTO;
import com.mycompany.myapp.service.mapper.InstructorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Instructor}.
 */
@Service
@Transactional
public class InstructorServiceImpl implements InstructorService {

    private static final Logger LOG = LoggerFactory.getLogger(InstructorServiceImpl.class);

    private final InstructorRepository instructorRepository;

    private final InstructorMapper instructorMapper;

    public InstructorServiceImpl(InstructorRepository instructorRepository, InstructorMapper instructorMapper) {
        this.instructorRepository = instructorRepository;
        this.instructorMapper = instructorMapper;
    }

    @Override
    public Mono<InstructorDTO> save(InstructorDTO instructorDTO) {
        LOG.debug("Request to save Instructor : {}", instructorDTO);
        return instructorRepository.save(instructorMapper.toEntity(instructorDTO)).map(instructorMapper::toDto);
    }

    @Override
    public Mono<InstructorDTO> update(InstructorDTO instructorDTO) {
        LOG.debug("Request to update Instructor : {}", instructorDTO);
        return instructorRepository.save(instructorMapper.toEntity(instructorDTO)).map(instructorMapper::toDto);
    }

    @Override
    public Mono<InstructorDTO> partialUpdate(InstructorDTO instructorDTO) {
        LOG.debug("Request to partially update Instructor : {}", instructorDTO);

        return instructorRepository
            .findById(instructorDTO.getId())
            .map(existingInstructor -> {
                instructorMapper.partialUpdate(existingInstructor, instructorDTO);

                return existingInstructor;
            })
            .flatMap(instructorRepository::save)
            .map(instructorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstructorDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Instructors");
        return instructorRepository.findAllBy(pageable).map(instructorMapper::toDto);
    }

    public Mono<Long> countAll() {
        return instructorRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstructorDTO> findOne(Long id) {
        LOG.debug("Request to get Instructor : {}", id);
        return instructorRepository.findById(id).map(instructorMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Instructor : {}", id);
        return instructorRepository.deleteById(id);
    }
}
