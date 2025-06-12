package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.StudentProfileAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.StudentProfile;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.StudentProfileRepository;
import com.mycompany.myapp.service.dto.StudentProfileDTO;
import com.mycompany.myapp.service.mapper.StudentProfileMapper;
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
 * Integration tests for the {@link StudentProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StudentProfileResourceIT {

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_LINKEDIN_URL = "AAAAAAAAAA";
    private static final String UPDATED_LINKEDIN_URL = "BBBBBBBBBB";

    private static final String DEFAULT_GITHUB_URL = "AAAAAAAAAA";
    private static final String UPDATED_GITHUB_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PROFILE_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_PICTURE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/student-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private StudentProfile studentProfile;

    private StudentProfile insertedStudentProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentProfile createEntity() {
        return new StudentProfile()
            .bio(DEFAULT_BIO)
            .linkedinUrl(DEFAULT_LINKEDIN_URL)
            .githubUrl(DEFAULT_GITHUB_URL)
            .profilePictureUrl(DEFAULT_PROFILE_PICTURE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentProfile createUpdatedEntity() {
        return new StudentProfile()
            .bio(UPDATED_BIO)
            .linkedinUrl(UPDATED_LINKEDIN_URL)
            .githubUrl(UPDATED_GITHUB_URL)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(StudentProfile.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        studentProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentProfile != null) {
            studentProfileRepository.delete(insertedStudentProfile).block();
            insertedStudentProfile = null;
        }
        deleteEntities(em);
    }

    @Test
    void createStudentProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);
        var returnedStudentProfileDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(StudentProfileDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the StudentProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStudentProfile = studentProfileMapper.toEntity(returnedStudentProfileDTO);
        assertStudentProfileUpdatableFieldsEquals(returnedStudentProfile, getPersistedStudentProfile(returnedStudentProfile));

        insertedStudentProfile = returnedStudentProfile;
    }

    @Test
    void createStudentProfileWithExistingId() throws Exception {
        // Create the StudentProfile with an existing ID
        studentProfile.setId(1L);
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllStudentProfiles() {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.save(studentProfile).block();

        // Get all the studentProfileList
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
            .value(hasItem(studentProfile.getId().intValue()))
            .jsonPath("$.[*].bio")
            .value(hasItem(DEFAULT_BIO))
            .jsonPath("$.[*].linkedinUrl")
            .value(hasItem(DEFAULT_LINKEDIN_URL))
            .jsonPath("$.[*].githubUrl")
            .value(hasItem(DEFAULT_GITHUB_URL))
            .jsonPath("$.[*].profilePictureUrl")
            .value(hasItem(DEFAULT_PROFILE_PICTURE_URL));
    }

    @Test
    void getStudentProfile() {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.save(studentProfile).block();

        // Get the studentProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, studentProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(studentProfile.getId().intValue()))
            .jsonPath("$.bio")
            .value(is(DEFAULT_BIO))
            .jsonPath("$.linkedinUrl")
            .value(is(DEFAULT_LINKEDIN_URL))
            .jsonPath("$.githubUrl")
            .value(is(DEFAULT_GITHUB_URL))
            .jsonPath("$.profilePictureUrl")
            .value(is(DEFAULT_PROFILE_PICTURE_URL));
    }

    @Test
    void getNonExistingStudentProfile() {
        // Get the studentProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStudentProfile() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.save(studentProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentProfile
        StudentProfile updatedStudentProfile = studentProfileRepository.findById(studentProfile.getId()).block();
        updatedStudentProfile
            .bio(UPDATED_BIO)
            .linkedinUrl(UPDATED_LINKEDIN_URL)
            .githubUrl(UPDATED_GITHUB_URL)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL);
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(updatedStudentProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, studentProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentProfileToMatchAllProperties(updatedStudentProfile);
    }

    @Test
    void putNonExistingStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, studentProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStudentProfileWithPatch() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.save(studentProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentProfile using partial update
        StudentProfile partialUpdatedStudentProfile = new StudentProfile();
        partialUpdatedStudentProfile.setId(studentProfile.getId());

        partialUpdatedStudentProfile.bio(UPDATED_BIO).githubUrl(UPDATED_GITHUB_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStudentProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStudentProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StudentProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentProfile, studentProfile),
            getPersistedStudentProfile(studentProfile)
        );
    }

    @Test
    void fullUpdateStudentProfileWithPatch() throws Exception {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.save(studentProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentProfile using partial update
        StudentProfile partialUpdatedStudentProfile = new StudentProfile();
        partialUpdatedStudentProfile.setId(studentProfile.getId());

        partialUpdatedStudentProfile
            .bio(UPDATED_BIO)
            .linkedinUrl(UPDATED_LINKEDIN_URL)
            .githubUrl(UPDATED_GITHUB_URL)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStudentProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStudentProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StudentProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentProfileUpdatableFieldsEquals(partialUpdatedStudentProfile, getPersistedStudentProfile(partialUpdatedStudentProfile));
    }

    @Test
    void patchNonExistingStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, studentProfileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStudentProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentProfile.setId(longCount.incrementAndGet());

        // Create the StudentProfile
        StudentProfileDTO studentProfileDTO = studentProfileMapper.toDto(studentProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(studentProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the StudentProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStudentProfile() {
        // Initialize the database
        insertedStudentProfile = studentProfileRepository.save(studentProfile).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentProfile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, studentProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentProfileRepository.count().block();
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

    protected StudentProfile getPersistedStudentProfile(StudentProfile studentProfile) {
        return studentProfileRepository.findById(studentProfile.getId()).block();
    }

    protected void assertPersistedStudentProfileToMatchAllProperties(StudentProfile expectedStudentProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStudentProfileAllPropertiesEquals(expectedStudentProfile, getPersistedStudentProfile(expectedStudentProfile));
        assertStudentProfileUpdatableFieldsEquals(expectedStudentProfile, getPersistedStudentProfile(expectedStudentProfile));
    }

    protected void assertPersistedStudentProfileToMatchUpdatableProperties(StudentProfile expectedStudentProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStudentProfileAllUpdatablePropertiesEquals(expectedStudentProfile, getPersistedStudentProfile(expectedStudentProfile));
        assertStudentProfileUpdatableFieldsEquals(expectedStudentProfile, getPersistedStudentProfile(expectedStudentProfile));
    }
}
