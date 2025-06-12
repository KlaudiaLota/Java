package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CourseTestSamples.*;
import static com.mycompany.myapp.domain.InstructorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InstructorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instructor.class);
        Instructor instructor1 = getInstructorSample1();
        Instructor instructor2 = new Instructor();
        assertThat(instructor1).isNotEqualTo(instructor2);

        instructor2.setId(instructor1.getId());
        assertThat(instructor1).isEqualTo(instructor2);

        instructor2 = getInstructorSample2();
        assertThat(instructor1).isNotEqualTo(instructor2);
    }

    @Test
    void courseTest() {
        Instructor instructor = getInstructorRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        instructor.addCourse(courseBack);
        assertThat(instructor.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getInstructor()).isEqualTo(instructor);

        instructor.removeCourse(courseBack);
        assertThat(instructor.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getInstructor()).isNull();

        instructor.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(instructor.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getInstructor()).isEqualTo(instructor);

        instructor.setCourses(new HashSet<>());
        assertThat(instructor.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getInstructor()).isNull();
    }
}
