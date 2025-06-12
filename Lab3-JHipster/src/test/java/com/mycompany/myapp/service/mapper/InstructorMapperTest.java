package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.InstructorAsserts.*;
import static com.mycompany.myapp.domain.InstructorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstructorMapperTest {

    private InstructorMapper instructorMapper;

    @BeforeEach
    void setUp() {
        instructorMapper = new InstructorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstructorSample1();
        var actual = instructorMapper.toEntity(instructorMapper.toDto(expected));
        assertInstructorAllPropertiesEquals(expected, actual);
    }
}
