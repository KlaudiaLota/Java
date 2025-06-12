package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.Instructor;
import com.mycompany.myapp.domain.Student;
import com.mycompany.myapp.service.dto.CourseDTO;
import com.mycompany.myapp.service.dto.InstructorDTO;
import com.mycompany.myapp.service.dto.StudentDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "students", source = "students", qualifiedByName = "studentFirstNameSet")
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "instructorId")
    CourseDTO toDto(Course s);

    @Mapping(target = "removeStudent", ignore = true)
    Course toEntity(CourseDTO courseDTO);

    @Named("studentFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    StudentDTO toDtoStudentFirstName(Student student);

    @Named("studentFirstNameSet")
    default Set<StudentDTO> toDtoStudentFirstNameSet(Set<Student> student) {
        return student.stream().map(this::toDtoStudentFirstName).collect(Collectors.toSet());
    }

    @Named("instructorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstructorDTO toDtoInstructorId(Instructor instructor);
}
