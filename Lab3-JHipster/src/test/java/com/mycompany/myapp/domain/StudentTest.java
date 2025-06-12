package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CourseTestSamples.*;
import static com.mycompany.myapp.domain.StudentProfileTestSamples.*;
import static com.mycompany.myapp.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StudentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Student.class);
        Student student1 = getStudentSample1();
        Student student2 = new Student();
        assertThat(student1).isNotEqualTo(student2);

        student2.setId(student1.getId());
        assertThat(student1).isEqualTo(student2);

        student2 = getStudentSample2();
        assertThat(student1).isNotEqualTo(student2);
    }

    @Test
    void profileTest() {
        Student student = getStudentRandomSampleGenerator();
        StudentProfile studentProfileBack = getStudentProfileRandomSampleGenerator();

        student.setProfile(studentProfileBack);
        assertThat(student.getProfile()).isEqualTo(studentProfileBack);

        student.profile(null);
        assertThat(student.getProfile()).isNull();
    }

    @Test
    void courseTest() {
        Student student = getStudentRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        student.addCourse(courseBack);
        assertThat(student.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getStudents()).containsOnly(student);

        student.removeCourse(courseBack);
        assertThat(student.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getStudents()).doesNotContain(student);

        student.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(student.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getStudents()).containsOnly(student);

        student.setCourses(new HashSet<>());
        assertThat(student.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getStudents()).doesNotContain(student);
    }
}
