package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.StudentProfile;
import com.mycompany.myapp.service.dto.StudentProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudentProfile} and its DTO {@link StudentProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudentProfileMapper extends EntityMapper<StudentProfileDTO, StudentProfile> {}
