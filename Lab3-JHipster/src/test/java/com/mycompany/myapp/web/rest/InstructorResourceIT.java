package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.InstructorAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Instructor;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.InstructorRepository;
import com.mycompany.myapp.service.dto.InstructorDTO;
import com.mycompany.myapp.service.mapper.InstructorMapper;
import java.time.Instant;
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
 * Integration tests for the {@link InstructorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InstructorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final Instant DEFAULT_HIRE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_HIRE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Float DEFAULT_RATING = 1F;
    private static final Float UPDATED_RATING = 2F;

    private static final String ENTITY_API_URL = "/api/instructors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private InstructorMapper instructorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Instructor instructor;

    private Instructor insertedInstructor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instructor createEntity() {
        return new Instructor()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .bio(DEFAULT_BIO)
            .hireDate(DEFAULT_HIRE_DATE)
            .rating(DEFAULT_RATING);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instructor createUpdatedEntity() {
        return new Instructor()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .bio(UPDATED_BIO)
            .hireDate(UPDATED_HIRE_DATE)
            .rating(UPDATED_RATING);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Instructor.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        instructor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInstructor != null) {
            instructorRepository.delete(insertedInstructor).block();
            insertedInstructor = null;
        }
        deleteEntities(em);
    }

    @Test
    void createInstructor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);
        var returnedInstructorDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InstructorDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Instructor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstructor = instructorMapper.toEntity(returnedInstructorDTO);
        assertInstructorUpdatableFieldsEquals(returnedInstructor, getPersistedInstructor(returnedInstructor));

        insertedInstructor = returnedInstructor;
    }

    @Test
    void createInstructorWithExistingId() throws Exception {
        // Create the Instructor with an existing ID
        instructor.setId(1L);
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllInstructors() {
        // Initialize the database
        insertedInstructor = instructorRepository.save(instructor).block();

        // Get all the instructorList
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
            .value(hasItem(instructor.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].bio")
            .value(hasItem(DEFAULT_BIO))
            .jsonPath("$.[*].hireDate")
            .value(hasItem(DEFAULT_HIRE_DATE.toString()))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING.doubleValue()));
    }

    @Test
    void getInstructor() {
        // Initialize the database
        insertedInstructor = instructorRepository.save(instructor).block();

        // Get the instructor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, instructor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(instructor.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.bio")
            .value(is(DEFAULT_BIO))
            .jsonPath("$.hireDate")
            .value(is(DEFAULT_HIRE_DATE.toString()))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING.doubleValue()));
    }

    @Test
    void getNonExistingInstructor() {
        // Get the instructor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInstructor() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.save(instructor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructor
        Instructor updatedInstructor = instructorRepository.findById(instructor.getId()).block();
        updatedInstructor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .bio(UPDATED_BIO)
            .hireDate(UPDATED_HIRE_DATE)
            .rating(UPDATED_RATING);
        InstructorDTO instructorDTO = instructorMapper.toDto(updatedInstructor);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, instructorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstructorToMatchAllProperties(updatedInstructor);
    }

    @Test
    void putNonExistingInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, instructorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInstructorWithPatch() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.save(instructor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructor using partial update
        Instructor partialUpdatedInstructor = new Instructor();
        partialUpdatedInstructor.setId(instructor.getId());

        partialUpdatedInstructor.firstName(UPDATED_FIRST_NAME).email(UPDATED_EMAIL).bio(UPDATED_BIO).hireDate(UPDATED_HIRE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstructor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstructor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Instructor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstructor, instructor),
            getPersistedInstructor(instructor)
        );
    }

    @Test
    void fullUpdateInstructorWithPatch() throws Exception {
        // Initialize the database
        insertedInstructor = instructorRepository.save(instructor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instructor using partial update
        Instructor partialUpdatedInstructor = new Instructor();
        partialUpdatedInstructor.setId(instructor.getId());

        partialUpdatedInstructor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .bio(UPDATED_BIO)
            .hireDate(UPDATED_HIRE_DATE)
            .rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstructor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstructor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Instructor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstructorUpdatableFieldsEquals(partialUpdatedInstructor, getPersistedInstructor(partialUpdatedInstructor));
    }

    @Test
    void patchNonExistingInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, instructorDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInstructor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instructor.setId(longCount.incrementAndGet());

        // Create the Instructor
        InstructorDTO instructorDTO = instructorMapper.toDto(instructor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(instructorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Instructor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInstructor() {
        // Initialize the database
        insertedInstructor = instructorRepository.save(instructor).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the instructor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, instructor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return instructorRepository.count().block();
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

    protected Instructor getPersistedInstructor(Instructor instructor) {
        return instructorRepository.findById(instructor.getId()).block();
    }

    protected void assertPersistedInstructorToMatchAllProperties(Instructor expectedInstructor) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstructorAllPropertiesEquals(expectedInstructor, getPersistedInstructor(expectedInstructor));
        assertInstructorUpdatableFieldsEquals(expectedInstructor, getPersistedInstructor(expectedInstructor));
    }

    protected void assertPersistedInstructorToMatchUpdatableProperties(Instructor expectedInstructor) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstructorAllUpdatablePropertiesEquals(expectedInstructor, getPersistedInstructor(expectedInstructor));
        assertInstructorUpdatableFieldsEquals(expectedInstructor, getPersistedInstructor(expectedInstructor));
    }
}
