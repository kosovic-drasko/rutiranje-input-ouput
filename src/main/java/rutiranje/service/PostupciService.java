package rutiranje.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rutiranje.domain.Postupci;
import rutiranje.repository.PostupciRepository;

/**
 * Service Implementation for managing {@link Postupci}.
 */
@Service
@Transactional
public class PostupciService {

    private final Logger log = LoggerFactory.getLogger(PostupciService.class);

    private final PostupciRepository postupciRepository;

    public PostupciService(PostupciRepository postupciRepository) {
        this.postupciRepository = postupciRepository;
    }

    /**
     * Save a postupci.
     *
     * @param postupci the entity to save.
     * @return the persisted entity.
     */
    public Postupci save(Postupci postupci) {
        log.debug("Request to save Postupci : {}", postupci);
        return postupciRepository.save(postupci);
    }

    /**
     * Update a postupci.
     *
     * @param postupci the entity to save.
     * @return the persisted entity.
     */
    public Postupci update(Postupci postupci) {
        log.debug("Request to save Postupci : {}", postupci);
        return postupciRepository.save(postupci);
    }

    /**
     * Partially update a postupci.
     *
     * @param postupci the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Postupci> partialUpdate(Postupci postupci) {
        log.debug("Request to partially update Postupci : {}", postupci);

        return postupciRepository
            .findById(postupci.getId())
            .map(existingPostupci -> {
                if (postupci.getSifraPostupka() != null) {
                    existingPostupci.setSifraPostupka(postupci.getSifraPostupka());
                }
                if (postupci.getOpis() != null) {
                    existingPostupci.setOpis(postupci.getOpis());
                }

                return existingPostupci;
            })
            .map(postupciRepository::save);
    }

    /**
     * Get all the postupcis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Postupci> findAll(Pageable pageable) {
        log.debug("Request to get all Postupcis");
        return postupciRepository.findAll(pageable);
    }

    /**
     * Get one postupci by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Postupci> findOne(Long id) {
        log.debug("Request to get Postupci : {}", id);
        return postupciRepository.findById(id);
    }

    /**
     * Delete the postupci by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Postupci : {}", id);
        postupciRepository.deleteById(id);
    }
}
