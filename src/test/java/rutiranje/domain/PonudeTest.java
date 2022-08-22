package rutiranje.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import rutiranje.web.rest.TestUtil;

class PonudeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ponude.class);
        Ponude ponude1 = new Ponude();
        ponude1.setId(1L);
        Ponude ponude2 = new Ponude();
        ponude2.setId(ponude1.getId());
        assertThat(ponude1).isEqualTo(ponude2);
        ponude2.setId(2L);
        assertThat(ponude1).isNotEqualTo(ponude2);
        ponude1.setId(null);
        assertThat(ponude1).isNotEqualTo(ponude2);
    }
}
