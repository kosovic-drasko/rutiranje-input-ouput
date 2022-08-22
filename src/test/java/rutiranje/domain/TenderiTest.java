package rutiranje.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import rutiranje.web.rest.TestUtil;

class TenderiTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tenderi.class);
        Tenderi tenderi1 = new Tenderi();
        tenderi1.setId(1L);
        Tenderi tenderi2 = new Tenderi();
        tenderi2.setId(tenderi1.getId());
        assertThat(tenderi1).isEqualTo(tenderi2);
        tenderi2.setId(2L);
        assertThat(tenderi1).isNotEqualTo(tenderi2);
        tenderi1.setId(null);
        assertThat(tenderi1).isNotEqualTo(tenderi2);
    }
}
