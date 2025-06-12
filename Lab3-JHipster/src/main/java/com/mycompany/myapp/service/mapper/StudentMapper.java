package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.Student;
import com.mycompany.myapp.domain.StudentProfile;
import com.mycompany.myapp.service.dto.CourseDTO;
import com.mycompany.myapp.service.dto.StudentDTO;
import com.mycompany.myapp.service.dto.StudentProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Student} and its DTO {@link StudentDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudentMapper extends EntityMapper<StudentDTO, Student> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "studentProfileId")
    @Mapping(target = "courses", source = "courses", qualifiedByName = "courseIdSet")
    StudentDTO toDto(Student s);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "removeCourse", ignore = true)
    Student toEntity(StudentDTO studentDTO);

    @Named("studentProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StudentProfileDTO toDtoStudentProfileId(StudentProfile studentProfile);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("courseIdSet")
    default Set<CourseDTO> toDtoCourseIdSet(Set<Course> course) {
        return course.stream().map(this::toDtoCourseId).collect(Collectors.toSet());
    }
}
