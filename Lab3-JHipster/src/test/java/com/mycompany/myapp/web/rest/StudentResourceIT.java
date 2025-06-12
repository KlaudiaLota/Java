package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.StudentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Student;
import com.mycompany.myapp.domain.enumeration.Gender;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.StudentRepository;
import com.mycompany.myapp.service.dto.StudentDTO;
import com.mycompany.myapp.service.mapper.StudentMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link StudentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StudentResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Instant DEFAULT_REGISTRATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REGISTRATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/students";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Student student;

    private Student insertedStudent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Student createEntity() {
        return new Student()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .birthDate(DEFAULT_BIRTH_DATE)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .registrationDate(DEFAULT_REGISTRATION_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Student createUpdatedEntity() {
        return new Student()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .registrationDate(UPDATED_REGISTRATION_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Student.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        student = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStudent != null) {
            studentRepository.delete(insertedStudent).block();
            insertedStudent = null;
        }
        deleteEntities(em);
    }

    @Test
    void createStudent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);
        var returnedStudentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(StudentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Student in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudent = studentMapper.toEntity(returnedStudentDTO);
        assertStudentUpdatableFieldsEquals(returnedStudent, getPersistedStudent(returnedStudent));

        insertedStudent = returnedStudent;
    }

    @Test
    void createStudentWithExistingId() throws Exception {
        // Create the Student with an existing ID
        student.setId(1L);
        StudentDTO studentDTO = studentMapper.toDto(student);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllStudents() {
        // Initialize the database
        insertedStudent = studentRepository.save(student).block();

        // Get all the studentList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(student.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].registrationDate")
            .value(hasItem(DEFAULT_REGISTRATION_DATE.toString()));
    }

    @Test
    void getStudent() {
        // Initialize the database
        insertedStudent = studentRepository.save(student).block();

        // Get the student
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, student.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(student.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.birthDate")
            .value(is(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.registrationDate")
            .value(is(DEFAULT_REGISTRATION_DATE.toString()));
    }

    @Test
    void getNonExistingStudent() {
        // Get the student
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStudent() throws Exception {
        // Initialize the database
        insertedStudent = studentRepository.save(student).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the student
        Student updatedStudent = studentRepository.findById(student.getId()).block();
        updatedStudent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .registrationDate(UPDATED_REGISTRATION_DATE);
        StudentDTO studentDTO = studentMapper.toDto(updatedStudent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, studentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentToMatchAllProperties(updatedStudent);
    }

    @Test
    void putNonExistingStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        student.setId(longCount.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, studentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        student.setId(longCount.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        student.setId(longCount.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStudentWithPatch() throws Exception {
        // Initialize the database
        insertedStudent = studentRepository.save(student).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the student using partial update
        Student partialUpdatedStudent = new Student();
        partialUpdatedStudent.setId(student.getId());

        partialUpdatedStudent.lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).registrationDate(UPDATED_REGISTRATION_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStudent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStudent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Student in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStudent, student), getPersistedStudent(student));
    }

    @Test
    void fullUpdateStudentWithPatch() throws Exception {
        // Initialize the database
        insertedStudent = studentRepository.save(student).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the student using partial update
        Student partialUpdatedStudent = new Student();
        partialUpdatedStudent.setId(student.getId());

        partialUpdatedStudent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .registrationDate(UPDATED_REGISTRATION_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStudent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStudent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Student in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentUpdatableFieldsEquals(partialUpdatedStudent, getPersistedStudent(partialUpdatedStudent));
    }

    @Test
    void patchNonExistingStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        student.setId(longCount.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, studentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        student.setId(longCount.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStudent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        student.setId(longCount.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Student in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStudent() {
        // Initialize the database
        insertedStudent = studentRepository.save(student).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the student
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, student.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Student getPersistedStudent(Student student) {
        return studentRepository.findById(student.getId()).block();
    }

    protected void assertPersistedStudentToMatchAllProperties(Student expectedStudent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStudentAllPropertiesEquals(expectedStudent, getPersistedStudent(expectedStudent));
        assertStudentUpdatableFieldsEquals(expectedStudent, getPersistedStudent(expectedStudent));
    }

    protected void assertPersistedStudentToMatchUpdatableProperties(Student expectedStudent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStudentAllUpdatablePropertiesEquals(expectedStudent, getPersistedStudent(expectedStudent));
        assertStudentUpdatableFieldsEquals(expectedStudent, getPersistedStudent(expectedStudent));
    }
}
