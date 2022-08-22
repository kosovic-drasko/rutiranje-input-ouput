package rutiranje.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import rutiranje.web.rest.TestUtil;

class PostupciTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Postupci.class);
        Postupci postupci1 = new Postupci();
        postupci1.setId(1L);
        Postupci postupci2 = new Postupci();
        postupci2.setId(postupci1.getId());
        assertThat(postupci1).isEqualTo(postupci2);
        postupci2.setId(2L);
        assertThat(postupci1).isNotEqualTo(postupci2);
        postupci1.setId(null);
        assertThat(postupci1).isNotEqualTo(postupci2);
    }
}
