package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.InstructorRepository;
import com.mycompany.myapp.service.InstructorService;
import com.mycompany.myapp.service.dto.InstructorDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Instructor}.
 */
@RestController
@RequestMapping("/api/instructors")
public class InstructorResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstructorResource.class);

    private static final String ENTITY_NAME = "instructor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstructorService instructorService;

    private final InstructorRepository instructorRepository;

    public InstructorResource(InstructorService instructorService, InstructorRepository instructorRepository) {
        this.instructorService = instructorService;
        this.instructorRepository = instructorRepository;
    }

    /**
     * {@code POST  /instructors} : Create a new instructor.
     *
     * @param instructorDTO the instructorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instructorDTO, or with status {@code 400 (Bad Request)} if the instructor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InstructorDTO>> createInstructor(@RequestBody InstructorDTO instructorDTO) throws URISyntaxException {
        LOG.debug("REST request to save Instructor : {}", instructorDTO);
        if (instructorDTO.getId() != null) {
            throw new BadRequestAlertException("A new instructor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return instructorService
            .save(instructorDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/instructors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /instructors/:id} : Updates an existing instructor.
     *
     * @param id the id of the instructorDTO to save.
     * @param instructorDTO the instructorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instructorDTO,
     * or with status {@code 400 (Bad Request)} if the instructorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instructorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InstructorDTO>> updateInstructor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InstructorDTO instructorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Instructor : {}, {}", id, instructorDTO);
        if (instructorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instructorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return instructorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return instructorService
                    .update(instructorDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /instructors/:id} : Partial updates given fields of an existing instructor, field will ignore if it is null
     *
     * @param id the id of the instructorDTO to save.
     * @param instructorDTO the instructorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instructorDTO,
     * or with status {@code 400 (Bad Request)} if the instructorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the instructorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the instructorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InstructorDTO>> partialUpdateInstructor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InstructorDTO instructorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Instructor partially : {}, {}", id, instructorDTO);
        if (instructorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instructorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return instructorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<InstructorDTO> result = instructorService.partialUpdate(instructorDTO);

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
     * {@code GET  /instructors} : get all the instructors.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instructors in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<InstructorDTO>>> getAllInstructors(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Instructors");
        return instructorService
            .countAll()
            .zipWith(instructorService.findAll(pageable).collectList())
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
     * {@code GET  /instructors/:id} : get the "id" instructor.
     *
     * @param id the id of the instructorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instructorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InstructorDTO>> getInstructor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Instructor : {}", id);
        Mono<InstructorDTO> instructorDTO = instructorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instructorDTO);
    }

    /**
     * {@code DELETE  /instructors/:id} : delete the "id" instructor.
     *
     * @param id the id of the instructorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInstructor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Instructor : {}", id);
        return instructorService
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
