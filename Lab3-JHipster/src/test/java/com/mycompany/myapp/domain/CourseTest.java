package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CourseTestSamples.*;
import static com.mycompany.myapp.domain.InstructorTestSamples.*;
import static com.mycompany.myapp.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void studentTest() {
        Course course = getCourseRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        course.addStudent(studentBack);
        assertThat(course.getStudents()).containsOnly(studentBack);

        course.removeStudent(studentBack);
        assertThat(course.getStudents()).doesNotContain(studentBack);

        course.students(new HashSet<>(Set.of(studentBack)));
        assertThat(course.getStudents()).containsOnly(studentBack);

        course.setStudents(new HashSet<>());
        assertThat(course.getStudents()).doesNotContain(studentBack);
    }

    @Test
    void instructorTest() {
        Course course = getCourseRandomSampleGenerator();
        Instructor instructorBack = getInstructorRandomSampleGenerator();

        course.setInstructor(instructorBack);
        assertThat(course.getInstructor()).isEqualTo(instructorBack);

        course.instructor(null);
        assertThat(course.getInstructor()).isNull();
    }
}
