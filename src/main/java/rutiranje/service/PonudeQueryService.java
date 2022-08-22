package rutiranje.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rutiranje.domain.*; // for static metamodels
import rutiranje.domain.Ponude;
import rutiranje.repository.PonudeRepository;
import rutiranje.service.criteria.PonudeCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Ponude} entities in the database.
 * The main input is a {@link PonudeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Ponude} or a {@link Page} of {@link Ponude} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PonudeQueryService extends QueryService<Ponude> {

    private final Logger log = LoggerFactory.getLogger(PonudeQueryService.class);

    private final PonudeRepository ponudeRepository;

    public PonudeQueryService(PonudeRepository ponudeRepository) {
        this.ponudeRepository = ponudeRepository;
    }

    /**
     * Return a {@link List} of {@link Ponude} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Ponude> findByCriteria(PonudeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ponude> specification = createSpecification(criteria);
        return ponudeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Ponude} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Ponude> findByCriteria(PonudeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ponude> specification = createSpecification(criteria);
        return ponudeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PonudeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ponude> specification = createSpecification(criteria);
        return ponudeRepository.count(specification);
    }

    /**
     * Function to convert {@link PonudeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ponude> createSpecification(PonudeCriteria criteria) {
        Specification<Ponude> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ponude_.id));
            }
            if (criteria.getSifraPostupka() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSifraPostupka(), Ponude_.sifraPostupka));
            }
            if (criteria.getNaziv() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNaziv(), Ponude_.naziv));
            }
        }
        return specification;
    }
}
