package rutiranje.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rutiranje.domain.Tenderi;
import rutiranje.repository.TenderiRepository;
import rutiranje.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link rutiranje.domain.Tenderi}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TenderiResource {

    private final Logger log = LoggerFactory.getLogger(TenderiResource.class);

    private final TenderiRepository tenderiRepository;

    public TenderiResource(TenderiRepository tenderiRepository) {
        this.tenderiRepository = tenderiRepository;
    }

    /**
     * {@code GET  /tenderis} : get all the tenderis.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tenderis in body.
     */
    @GetMapping("/tenderis")
    public List<Tenderi> getAllTenderis() {
        log.debug("REST request to get all Tenderis");
        return tenderiRepository.findAll();
    }

    /**
     * {@code GET  /tenderis/:id} : get the "id" tenderi.
     *
     * @param id the id of the tenderi to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenderi, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tenderis/{id}")
    public ResponseEntity<Tenderi> getTenderi(@PathVariable Long id) {
        log.debug("REST request to get Tenderi : {}", id);
        Optional<Tenderi> tenderi = tenderiRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tenderi);
    }
}
