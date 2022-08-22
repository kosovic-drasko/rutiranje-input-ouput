package rutiranje.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import rutiranje.domain.Ponude;

/**
 * Spring Data SQL repository for the Ponude entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PonudeRepository extends JpaRepository<Ponude, Long>, JpaSpecificationExecutor<Ponude> {}
