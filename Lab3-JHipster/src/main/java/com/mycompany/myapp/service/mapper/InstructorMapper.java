package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Instructor;
import com.mycompany.myapp.service.dto.InstructorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Instructor} and its DTO {@link InstructorDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstructorMapper extends EntityMapper<InstructorDTO, Instructor> {}
