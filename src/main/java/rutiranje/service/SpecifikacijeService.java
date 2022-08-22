package rutiranje.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rutiranje.domain.Specifikacije;
import rutiranje.repository.SpecifikacijeRepository;

/**
 * Service Implementation for managing {@link Specifikacije}.
 */
@Service
@Transactional
public class SpecifikacijeService {

    private final Logger log = LoggerFactory.getLogger(SpecifikacijeService.class);

    private final SpecifikacijeRepository specifikacijeRepository;

    public SpecifikacijeService(SpecifikacijeRepository specifikacijeRepository) {
        this.specifikacijeRepository = specifikacijeRepository;
    }

    /**
     * Save a specifikacije.
     *
     * @param specifikacije the entity to save.
     * @return the persisted entity.
     */
    public Specifikacije save(Specifikacije specifikacije) {
        log.debug("Request to save Specifikacije : {}", specifikacije);
        return specifikacijeRepository.save(specifikacije);
    }

    /**
     * Update a specifikacije.
     *
     * @param specifikacije the entity to save.
     * @return the persisted entity.
     */
    public Specifikacije update(Specifikacije specifikacije) {
        log.debug("Request to save Specifikacije : {}", specifikacije);
        return specifikacijeRepository.save(specifikacije);
    }

    /**
     * Partially update a specifikacije.
     *
     * @param specifikacije the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Specifikacije> partialUpdate(Specifikacije specifikacije) {
        log.debug("Request to partially update Specifikacije : {}", specifikacije);

        return specifikacijeRepository
            .findById(specifikacije.getId())
            .map(existingSpecifikacije -> {
                if (specifikacije.getSifraPostupka() != null) {
                    existingSpecifikacije.setSifraPostupka(specifikacije.getSifraPostupka());
                }
                if (specifikacije.getNaziv() != null) {
                    existingSpecifikacije.setNaziv(specifikacije.getNaziv());
                }

                return existingSpecifikacije;
            })
            .map(specifikacijeRepository::save);
    }

    /**
     * Get all the specifikacijes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Specifikacije> findAll(Pageable pageable) {
        log.debug("Request to get all Specifikacijes");
        return specifikacijeRepository.findAll(pageable);
    }

    /**
     * Get one specifikacije by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Specifikacije> findOne(Long id) {
        log.debug("Request to get Specifikacije : {}", id);
        return specifikacijeRepository.findById(id);
    }

    /**
     * Delete the specifikacije by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Specifikacije : {}", id);
        specifikacijeRepository.deleteById(id);
    }
}
