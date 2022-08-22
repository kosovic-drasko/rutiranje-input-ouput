package rutiranje.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rutiranje.domain.Ponude;
import rutiranje.repository.PonudeRepository;
import rutiranje.service.PonudeQueryService;
import rutiranje.service.PonudeService;
import rutiranje.service.criteria.PonudeCriteria;
import rutiranje.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link rutiranje.domain.Ponude}.
 */
@RestController
@RequestMapping("/api")
public class PonudeResource {

    private final Logger log = LoggerFactory.getLogger(PonudeResource.class);

    private static final String ENTITY_NAME = "ponude";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PonudeService ponudeService;

    private final PonudeRepository ponudeRepository;

    private final PonudeQueryService ponudeQueryService;

    public PonudeResource(PonudeService ponudeService, PonudeRepository ponudeRepository, PonudeQueryService ponudeQueryService) {
        this.ponudeService = ponudeService;
        this.ponudeRepository = ponudeRepository;
        this.ponudeQueryService = ponudeQueryService;
    }

    /**
     * {@code POST  /ponudes} : Create a new ponude.
     *
     * @param ponude the ponude to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ponude, or with status {@code 400 (Bad Request)} if the ponude has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ponudes")
    public ResponseEntity<Ponude> createPonude(@Valid @RequestBody Ponude ponude) throws URISyntaxException {
        log.debug("REST request to save Ponude : {}", ponude);
        if (ponude.getId() != null) {
            throw new BadRequestAlertException("A new ponude cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ponude result = ponudeService.save(ponude);
        return ResponseEntity
            .created(new URI("/api/ponudes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ponudes/:id} : Updates an existing ponude.
     *
     * @param id the id of the ponude to save.
     * @param ponude the ponude to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ponude,
     * or with status {@code 400 (Bad Request)} if the ponude is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ponude couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ponudes/{id}")
    public ResponseEntity<Ponude> updatePonude(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Ponude ponude
    ) throws URISyntaxException {
        log.debug("REST request to update Ponude : {}, {}", id, ponude);
        if (ponude.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ponude.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ponudeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Ponude result = ponudeService.update(ponude);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ponude.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ponudes/:id} : Partial updates given fields of an existing ponude, field will ignore if it is null
     *
     * @param id the id of the ponude to save.
     * @param ponude the ponude to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ponude,
     * or with status {@code 400 (Bad Request)} if the ponude is not valid,
     * or with status {@code 404 (Not Found)} if the ponude is not found,
     * or with status {@code 500 (Internal Server Error)} if the ponude couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ponudes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Ponude> partialUpdatePonude(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Ponude ponude
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ponude partially : {}, {}", id, ponude);
        if (ponude.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ponude.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ponudeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Ponude> result = ponudeService.partialUpdate(ponude);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ponude.getId().toString())
        );
    }

    /**
     * {@code GET  /ponudes} : get all the ponudes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ponudes in body.
     */
    @GetMapping("/ponudes")
    public ResponseEntity<List<Ponude>> getAllPonudes(
        PonudeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Ponudes by criteria: {}", criteria);
        Page<Ponude> page = ponudeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ponudes/count} : count all the ponudes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ponudes/count")
    public ResponseEntity<Long> countPonudes(PonudeCriteria criteria) {
        log.debug("REST request to count Ponudes by criteria: {}", criteria);
        return ResponseEntity.ok().body(ponudeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ponudes/:id} : get the "id" ponude.
     *
     * @param id the id of the ponude to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ponude, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ponudes/{id}")
    public ResponseEntity<Ponude> getPonude(@PathVariable Long id) {
        log.debug("REST request to get Ponude : {}", id);
        Optional<Ponude> ponude = ponudeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ponude);
    }

    /**
     * {@code DELETE  /ponudes/:id} : delete the "id" ponude.
     *
     * @param id the id of the ponude to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ponudes/{id}")
    public ResponseEntity<Void> deletePonude(@PathVariable Long id) {
        log.debug("REST request to delete Ponude : {}", id);
        ponudeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
