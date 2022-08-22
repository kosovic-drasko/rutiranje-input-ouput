package rutiranje.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import rutiranje.IntegrationTest;
import rutiranje.domain.Tenderi;
import rutiranje.repository.TenderiRepository;

/**
 * Integration tests for the {@link TenderiResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TenderiResourceIT {

    private static final String ENTITY_API_URL = "/api/tenderis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TenderiRepository tenderiRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenderiMockMvc;

    private Tenderi tenderi;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenderi createEntity(EntityManager em) {
        Tenderi tenderi = new Tenderi();
        return tenderi;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenderi createUpdatedEntity(EntityManager em) {
        Tenderi tenderi = new Tenderi();
        return tenderi;
    }

    @BeforeEach
    public void initTest() {
        tenderi = createEntity(em);
    }

    @Test
    @Transactional
    void getAllTenderis() throws Exception {
        // Initialize the database
        tenderiRepository.saveAndFlush(tenderi);

        // Get all the tenderiList
        restTenderiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenderi.getId().intValue())));
    }

    @Test
    @Transactional
    void getTenderi() throws Exception {
        // Initialize the database
        tenderiRepository.saveAndFlush(tenderi);

        // Get the tenderi
        restTenderiMockMvc
            .perform(get(ENTITY_API_URL_ID, tenderi.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenderi.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingTenderi() throws Exception {
        // Get the tenderi
        restTenderiMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
