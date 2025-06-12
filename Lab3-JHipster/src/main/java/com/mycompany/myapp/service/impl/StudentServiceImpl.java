package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.StudentRepository;
import com.mycompany.myapp.service.StudentService;
import com.mycompany.myapp.service.dto.StudentDTO;
import com.mycompany.myapp.service.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Student}.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public Mono<StudentDTO> save(StudentDTO studentDTO) {
        LOG.debug("Request to save Student : {}", studentDTO);
        return studentRepository.save(studentMapper.toEntity(studentDTO)).map(studentMapper::toDto);
    }

    @Override
    public Mono<StudentDTO> update(StudentDTO studentDTO) {
        LOG.debug("Request to update Student : {}", studentDTO);
        return studentRepository.save(studentMapper.toEntity(studentDTO)).map(studentMapper::toDto);
    }

    @Override
    public Mono<StudentDTO> partialUpdate(StudentDTO studentDTO) {
        LOG.debug("Request to partially update Student : {}", studentDTO);

        return studentRepository
            .findById(studentDTO.getId())
            .map(existingStudent -> {
                studentMapper.partialUpdate(existingStudent, studentDTO);

                return existingStudent;
            })
            .flatMap(studentRepository::save)
            .map(studentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StudentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Students");
        return studentRepository.findAllBy(pageable).map(studentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return studentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StudentDTO> findOne(Long id) {
        LOG.debug("Request to get Student : {}", id);
        return studentRepository.findById(id).map(studentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Student : {}", id);
        return studentRepository.deleteById(id);
    }
}
