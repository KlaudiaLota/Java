package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.StudentProfileRepository;
import com.mycompany.myapp.service.StudentProfileService;
import com.mycompany.myapp.service.dto.StudentProfileDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.StudentProfile}.
 */
@RestController
@RequestMapping("/api/student-profiles")
public class StudentProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentProfileResource.class);

    private static final String ENTITY_NAME = "studentProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StudentProfileService studentProfileService;

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileResource(StudentProfileService studentProfileService, StudentProfileRepository studentProfileRepository) {
        this.studentProfileService = studentProfileService;
        this.studentProfileRepository = studentProfileRepository;
    }

    /**
     * {@code POST  /student-profiles} : Create a new studentProfile.
     *
     * @param studentProfileDTO the studentProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentProfileDTO, or with status {@code 400 (Bad Request)} if the studentProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<StudentProfileDTO>> createStudentProfile(@RequestBody StudentProfileDTO studentProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StudentProfile : {}", studentProfileDTO);
        if (studentProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new studentProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return studentProfileService
            .save(studentProfileDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/student-profiles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /student-profiles/:id} : Updates an existing studentProfile.
     *
     * @param id the id of the studentProfileDTO to save.
     * @param studentProfileDTO the studentProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentProfileDTO,
     * or with status {@code 400 (Bad Request)} if the studentProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<StudentProfileDTO>> updateStudentProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StudentProfileDTO studentProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentProfile : {}, {}", id, studentProfileDTO);
        if (studentProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return studentProfileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return studentProfileService
                    .update(studentProfileDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /student-profiles/:id} : Partial updates given fields of an existing studentProfile, field will ignore if it is null
     *
     * @param id the id of the studentProfileDTO to save.
     * @param studentProfileDTO the studentProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentProfileDTO,
     * or with status {@code 400 (Bad Request)} if the studentProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the studentProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<StudentProfileDTO>> partialUpdateStudentProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StudentProfileDTO studentProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StudentProfile partially : {}, {}", id, studentProfileDTO);
        if (studentProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return studentProfileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<StudentProfileDTO> result = studentProfileService.partialUpdate(studentProfileDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /student-profiles} : get all the studentProfiles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of studentProfiles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<StudentProfileDTO>>> getAllStudentProfiles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("student-is-null".equals(filter)) {
            LOG.debug("REST request to get all StudentProfiles where student is null");
            return studentProfileService.findAllWhereStudentIsNull().collectList().map(ResponseEntity::ok);
        }
        LOG.debug("REST request to get a page of StudentProfiles");
        return studentProfileService
            .countAll()
            .zipWith(studentProfileService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /student-profiles/:id} : get the "id" studentProfile.
     *
     * @param id the id of the studentProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<StudentProfileDTO>> getStudentProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentProfile : {}", id);
        Mono<StudentProfileDTO> studentProfileDTO = studentProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studentProfileDTO);
    }

    /**
     * {@code DELETE  /student-profiles/:id} : delete the "id" studentProfile.
     *
     * @param id the id of the studentProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStudentProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentProfile : {}", id);
        return studentProfileService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
