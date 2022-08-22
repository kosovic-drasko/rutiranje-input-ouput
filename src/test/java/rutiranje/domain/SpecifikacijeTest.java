package rutiranje.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import rutiranje.web.rest.TestUtil;

class SpecifikacijeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Specifikacije.class);
        Specifikacije specifikacije1 = new Specifikacije();
        specifikacije1.setId(1L);
        Specifikacije specifikacije2 = new Specifikacije();
        specifikacije2.setId(specifikacije1.getId());
        assertThat(specifikacije1).isEqualTo(specifikacije2);
        specifikacije2.setId(2L);
        assertThat(specifikacije1).isNotEqualTo(specifikacije2);
        specifikacije1.setId(null);
        assertThat(specifikacije1).isNotEqualTo(specifikacije2);
    }
}
