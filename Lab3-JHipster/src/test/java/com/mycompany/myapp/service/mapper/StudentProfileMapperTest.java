package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.StudentProfileAsserts.*;
import static com.mycompany.myapp.domain.StudentProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentProfileMapperTest {

    private StudentProfileMapper studentProfileMapper;

    @BeforeEach
    void setUp() {
        studentProfileMapper = new StudentProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStudentProfileSample1();
        var actual = studentProfileMapper.toEntity(studentProfileMapper.toDto(expected));
        assertStudentProfileAllPropertiesEquals(expected, actual);
    }
}
