package rutiranje.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import rutiranje.domain.Tenderi;

/**
 * Spring Data SQL repository for the Tenderi entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenderiRepository extends JpaRepository<Tenderi, Long> {}
