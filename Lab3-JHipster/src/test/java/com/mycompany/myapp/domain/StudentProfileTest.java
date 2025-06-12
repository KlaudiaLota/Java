package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.StudentProfileTestSamples.*;
import static com.mycompany.myapp.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentProfile.class);
        StudentProfile studentProfile1 = getStudentProfileSample1();
        StudentProfile studentProfile2 = new StudentProfile();
        assertThat(studentProfile1).isNotEqualTo(studentProfile2);

        studentProfile2.setId(studentProfile1.getId());
        assertThat(studentProfile1).isEqualTo(studentProfile2);

        studentProfile2 = getStudentProfileSample2();
        assertThat(studentProfile1).isNotEqualTo(studentProfile2);
    }

    @Test
    void studentTest() {
        StudentProfile studentProfile = getStudentProfileRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        studentProfile.setStudent(studentBack);
        assertThat(studentProfile.getStudent()).isEqualTo(studentBack);
        assertThat(studentBack.getProfile()).isEqualTo(studentProfile);

        studentProfile.student(null);
        assertThat(studentProfile.getStudent()).isNull();
        assertThat(studentBack.getProfile()).isNull();
    }
}
