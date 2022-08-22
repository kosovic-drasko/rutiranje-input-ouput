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
import rutiranje.domain.Postupci;
import rutiranje.repository.PostupciRepository;
import rutiranje.service.criteria.PostupciCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Postupci} entities in the database.
 * The main input is a {@link PostupciCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Postupci} or a {@link Page} of {@link Postupci} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PostupciQueryService extends QueryService<Postupci> {

    private final Logger log = LoggerFactory.getLogger(PostupciQueryService.class);

    private final PostupciRepository postupciRepository;

    public PostupciQueryService(PostupciRepository postupciRepository) {
        this.postupciRepository = postupciRepository;
    }

    /**
     * Return a {@link List} of {@link Postupci} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Postupci> findByCriteria(PostupciCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Postupci> specification = createSpecification(criteria);
        return postupciRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Postupci} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Postupci> findByCriteria(PostupciCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Postupci> specification = createSpecification(criteria);
        return postupciRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PostupciCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Postupci> specification = createSpecification(criteria);
        return postupciRepository.count(specification);
    }

    /**
     * Function to convert {@link PostupciCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Postupci> createSpecification(PostupciCriteria criteria) {
        Specification<Postupci> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Postupci_.id));
            }
            if (criteria.getSifraPostupka() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSifraPostupka(), Postupci_.sifraPostupka));
            }
            if (criteria.getOpis() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOpis(), Postupci_.opis));
            }
        }
        return specification;
    }
}
