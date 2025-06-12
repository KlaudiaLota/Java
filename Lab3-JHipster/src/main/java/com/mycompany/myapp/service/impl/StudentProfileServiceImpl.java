package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.StudentProfileRepository;
import com.mycompany.myapp.service.StudentProfileService;
import com.mycompany.myapp.service.dto.StudentProfileDTO;
import com.mycompany.myapp.service.mapper.StudentProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.StudentProfile}.
 */
@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentProfileServiceImpl.class);

    private final StudentProfileRepository studentProfileRepository;

    private final StudentProfileMapper studentProfileMapper;

    public StudentProfileServiceImpl(StudentProfileRepository studentProfileRepository, StudentProfileMapper studentProfileMapper) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    public Mono<StudentProfileDTO> save(StudentProfileDTO studentProfileDTO) {
        LOG.debug("Request to save StudentProfile : {}", studentProfileDTO);
        return studentProfileRepository.save(studentProfileMapper.toEntity(studentProfileDTO)).map(studentProfileMapper::toDto);
    }

    @Override
    public Mono<StudentProfileDTO> update(StudentProfileDTO studentProfileDTO) {
        LOG.debug("Request to update StudentProfile : {}", studentProfileDTO);
        return studentProfileRepository.save(studentProfileMapper.toEntity(studentProfileDTO)).map(studentProfileMapper::toDto);
    }

    @Override
    public Mono<StudentProfileDTO> partialUpdate(StudentProfileDTO studentProfileDTO) {
        LOG.debug("Request to partially update StudentProfile : {}", studentProfileDTO);

        return studentProfileRepository
            .findById(studentProfileDTO.getId())
            .map(existingStudentProfile -> {
                studentProfileMapper.partialUpdate(existingStudentProfile, studentProfileDTO);

                return existingStudentProfile;
            })
            .flatMap(studentProfileRepository::save)
            .map(studentProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StudentProfileDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all StudentProfiles");
        return studentProfileRepository.findAllBy(pageable).map(studentProfileMapper::toDto);
    }

    /**
     *  Get all the studentProfiles where Student is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<StudentProfileDTO> findAllWhereStudentIsNull() {
        LOG.debug("Request to get all studentProfiles where Student is null");
        return studentProfileRepository.findAllWhereStudentIsNull().map(studentProfileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return studentProfileRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StudentProfileDTO> findOne(Long id) {
        LOG.debug("Request to get StudentProfile : {}", id);
        return studentProfileRepository.findById(id).map(studentProfileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete StudentProfile : {}", id);
        return studentProfileRepository.deleteById(id);
    }
}
