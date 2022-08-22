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
import rutiranje.domain.Specifikacije;
import rutiranje.repository.SpecifikacijeRepository;
import rutiranje.service.criteria.SpecifikacijeCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Specifikacije} entities in the database.
 * The main input is a {@link SpecifikacijeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Specifikacije} or a {@link Page} of {@link Specifikacije} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpecifikacijeQueryService extends QueryService<Specifikacije> {

    private final Logger log = LoggerFactory.getLogger(SpecifikacijeQueryService.class);

    private final SpecifikacijeRepository specifikacijeRepository;

    public SpecifikacijeQueryService(SpecifikacijeRepository specifikacijeRepository) {
        this.specifikacijeRepository = specifikacijeRepository;
    }

    /**
     * Return a {@link List} of {@link Specifikacije} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Specifikacije> findByCriteria(SpecifikacijeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Specifikacije> specification = createSpecification(criteria);
        return specifikacijeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Specifikacije} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Specifikacije> findByCriteria(SpecifikacijeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Specifikacije> specification = createSpecification(criteria);
        return specifikacijeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpecifikacijeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Specifikacije> specification = createSpecification(criteria);
        return specifikacijeRepository.count(specification);
    }

    /**
     * Function to convert {@link SpecifikacijeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Specifikacije> createSpecification(SpecifikacijeCriteria criteria) {
        Specification<Specifikacije> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Specifikacije_.id));
            }
            if (criteria.getSifraPostupka() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSifraPostupka(), Specifikacije_.sifraPostupka));
            }
            if (criteria.getNaziv() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNaziv(), Specifikacije_.naziv));
            }
        }
        return specification;
    }
}
