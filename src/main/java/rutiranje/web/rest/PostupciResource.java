package rutiranje.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rutiranje.domain.Postupci;
import rutiranje.repository.PostupciRepository;
import rutiranje.service.PostupciQueryService;
import rutiranje.service.PostupciService;
import rutiranje.service.criteria.PostupciCriteria;
import rutiranje.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link rutiranje.domain.Postupci}.
 */
@RestController
@RequestMapping("/api")
public class PostupciResource {

    private final Logger log = LoggerFactory.getLogger(PostupciResource.class);

    private static final String ENTITY_NAME = "postupci";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostupciService postupciService;

    private final PostupciRepository postupciRepository;

    private final PostupciQueryService postupciQueryService;

    public PostupciResource(
        PostupciService postupciService,
        PostupciRepository postupciRepository,
        PostupciQueryService postupciQueryService
    ) {
        this.postupciService = postupciService;
        this.postupciRepository = postupciRepository;
        this.postupciQueryService = postupciQueryService;
    }

    /**
     * {@code POST  /postupcis} : Create a new postupci.
     *
     * @param postupci the postupci to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postupci, or with status {@code 400 (Bad Request)} if the postupci has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/postupcis")
    public ResponseEntity<Postupci> createPostupci(@RequestBody Postupci postupci) throws URISyntaxException {
        log.debug("REST request to save Postupci : {}", postupci);
        if (postupci.getId() != null) {
            throw new BadRequestAlertException("A new postupci cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Postupci result = postupciService.save(postupci);
        return ResponseEntity
            .created(new URI("/api/postupcis/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /postupcis/:id} : Updates an existing postupci.
     *
     * @param id the id of the postupci to save.
     * @param postupci the postupci to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postupci,
     * or with status {@code 400 (Bad Request)} if the postupci is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postupci couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/postupcis/{id}")
    public ResponseEntity<Postupci> updatePostupci(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Postupci postupci
    ) throws URISyntaxException {
        log.debug("REST request to update Postupci : {}, {}", id, postupci);
        if (postupci.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postupci.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postupciRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Postupci result = postupciService.update(postupci);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postupci.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /postupcis/:id} : Partial updates given fields of an existing postupci, field will ignore if it is null
     *
     * @param id the id of the postupci to save.
     * @param postupci the postupci to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postupci,
     * or with status {@code 400 (Bad Request)} if the postupci is not valid,
     * or with status {@code 404 (Not Found)} if the postupci is not found,
     * or with status {@code 500 (Internal Server Error)} if the postupci couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/postupcis/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Postupci> partialUpdatePostupci(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Postupci postupci
    ) throws URISyntaxException {
        log.debug("REST request to partial update Postupci partially : {}, {}", id, postupci);
        if (postupci.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postupci.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postupciRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Postupci> result = postupciService.partialUpdate(postupci);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postupci.getId().toString())
        );
    }

    /**
     * {@code GET  /postupcis} : get all the postupcis.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postupcis in body.
     */
    @GetMapping("/postupcis")
    public ResponseEntity<List<Postupci>> getAllPostupcis(
        PostupciCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Postupcis by criteria: {}", criteria);
        Page<Postupci> page = postupciQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /postupcis/count} : count all the postupcis.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/postupcis/count")
    public ResponseEntity<Long> countPostupcis(PostupciCriteria criteria) {
        log.debug("REST request to count Postupcis by criteria: {}", criteria);
        return ResponseEntity.ok().body(postupciQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /postupcis/:id} : get the "id" postupci.
     *
     * @param id the id of the postupci to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postupci, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/postupcis/{id}")
    public ResponseEntity<Postupci> getPostupci(@PathVariable Long id) {
        log.debug("REST request to get Postupci : {}", id);
        Optional<Postupci> postupci = postupciService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postupci);
    }

    /**
     * {@code DELETE  /postupcis/:id} : delete the "id" postupci.
     *
     * @param id the id of the postupci to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/postupcis/{id}")
    public ResponseEntity<Void> deletePostupci(@PathVariable Long id) {
        log.debug("REST request to delete Postupci : {}", id);
        postupciService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
