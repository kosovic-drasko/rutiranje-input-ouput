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
import rutiranje.domain.Postupci;
import rutiranje.repository.PostupciRepository;
import rutiranje.service.criteria.PostupciCriteria;

/**
 * Integration tests for the {@link PostupciResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostupciResourceIT {

    private static final Integer DEFAULT_SIFRA_POSTUPKA = 1;
    private static final Integer UPDATED_SIFRA_POSTUPKA = 2;
    private static final Integer SMALLER_SIFRA_POSTUPKA = 1 - 1;

    private static final String DEFAULT_OPIS = "AAAAAAAAAA";
    private static final String UPDATED_OPIS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/postupcis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PostupciRepository postupciRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostupciMockMvc;

    private Postupci postupci;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Postupci createEntity(EntityManager em) {
        Postupci postupci = new Postupci().sifraPostupka(DEFAULT_SIFRA_POSTUPKA).opis(DEFAULT_OPIS);
        return postupci;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Postupci createUpdatedEntity(EntityManager em) {
        Postupci postupci = new Postupci().sifraPostupka(UPDATED_SIFRA_POSTUPKA).opis(UPDATED_OPIS);
        return postupci;
    }

    @BeforeEach
    public void initTest() {
        postupci = createEntity(em);
    }

    @Test
    @Transactional
    void createPostupci() throws Exception {
        int databaseSizeBeforeCreate = postupciRepository.findAll().size();
        // Create the Postupci
        restPostupciMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postupci)))
            .andExpect(status().isCreated());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeCreate + 1);
        Postupci testPostupci = postupciList.get(postupciList.size() - 1);
        assertThat(testPostupci.getSifraPostupka()).isEqualTo(DEFAULT_SIFRA_POSTUPKA);
        assertThat(testPostupci.getOpis()).isEqualTo(DEFAULT_OPIS);
    }

    @Test
    @Transactional
    void createPostupciWithExistingId() throws Exception {
        // Create the Postupci with an existing ID
        postupci.setId(1L);

        int databaseSizeBeforeCreate = postupciRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostupciMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postupci)))
            .andExpect(status().isBadRequest());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPostupcis() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList
        restPostupciMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postupci.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].opis").value(hasItem(DEFAULT_OPIS)));
    }

    @Test
    @Transactional
    void getPostupci() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get the postupci
        restPostupciMockMvc
            .perform(get(ENTITY_API_URL_ID, postupci.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(postupci.getId().intValue()))
            .andExpect(jsonPath("$.sifraPostupka").value(DEFAULT_SIFRA_POSTUPKA))
            .andExpect(jsonPath("$.opis").value(DEFAULT_OPIS));
    }

    @Test
    @Transactional
    void getPostupcisByIdFiltering() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        Long id = postupci.getId();

        defaultPostupciShouldBeFound("id.equals=" + id);
        defaultPostupciShouldNotBeFound("id.notEquals=" + id);

        defaultPostupciShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPostupciShouldNotBeFound("id.greaterThan=" + id);

        defaultPostupciShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPostupciShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsEqualToSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka equals to DEFAULT_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.equals=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka equals to UPDATED_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.equals=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka not equals to DEFAULT_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.notEquals=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka not equals to UPDATED_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.notEquals=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsInShouldWork() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka in DEFAULT_SIFRA_POSTUPKA or UPDATED_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.in=" + DEFAULT_SIFRA_POSTUPKA + "," + UPDATED_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka equals to UPDATED_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.in=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsNullOrNotNull() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka is not null
        defaultPostupciShouldBeFound("sifraPostupka.specified=true");

        // Get all the postupciList where sifraPostupka is null
        defaultPostupciShouldNotBeFound("sifraPostupka.specified=false");
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka is greater than or equal to DEFAULT_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.greaterThanOrEqual=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka is greater than or equal to UPDATED_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.greaterThanOrEqual=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka is less than or equal to DEFAULT_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.lessThanOrEqual=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka is less than or equal to SMALLER_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.lessThanOrEqual=" + SMALLER_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsLessThanSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka is less than DEFAULT_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.lessThan=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka is less than UPDATED_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.lessThan=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisBySifraPostupkaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where sifraPostupka is greater than DEFAULT_SIFRA_POSTUPKA
        defaultPostupciShouldNotBeFound("sifraPostupka.greaterThan=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the postupciList where sifraPostupka is greater than SMALLER_SIFRA_POSTUPKA
        defaultPostupciShouldBeFound("sifraPostupka.greaterThan=" + SMALLER_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPostupcisByOpisIsEqualToSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where opis equals to DEFAULT_OPIS
        defaultPostupciShouldBeFound("opis.equals=" + DEFAULT_OPIS);

        // Get all the postupciList where opis equals to UPDATED_OPIS
        defaultPostupciShouldNotBeFound("opis.equals=" + UPDATED_OPIS);
    }

    @Test
    @Transactional
    void getAllPostupcisByOpisIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where opis not equals to DEFAULT_OPIS
        defaultPostupciShouldNotBeFound("opis.notEquals=" + DEFAULT_OPIS);

        // Get all the postupciList where opis not equals to UPDATED_OPIS
        defaultPostupciShouldBeFound("opis.notEquals=" + UPDATED_OPIS);
    }

    @Test
    @Transactional
    void getAllPostupcisByOpisIsInShouldWork() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where opis in DEFAULT_OPIS or UPDATED_OPIS
        defaultPostupciShouldBeFound("opis.in=" + DEFAULT_OPIS + "," + UPDATED_OPIS);

        // Get all the postupciList where opis equals to UPDATED_OPIS
        defaultPostupciShouldNotBeFound("opis.in=" + UPDATED_OPIS);
    }

    @Test
    @Transactional
    void getAllPostupcisByOpisIsNullOrNotNull() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where opis is not null
        defaultPostupciShouldBeFound("opis.specified=true");

        // Get all the postupciList where opis is null
        defaultPostupciShouldNotBeFound("opis.specified=false");
    }

    @Test
    @Transactional
    void getAllPostupcisByOpisContainsSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where opis contains DEFAULT_OPIS
        defaultPostupciShouldBeFound("opis.contains=" + DEFAULT_OPIS);

        // Get all the postupciList where opis contains UPDATED_OPIS
        defaultPostupciShouldNotBeFound("opis.contains=" + UPDATED_OPIS);
    }

    @Test
    @Transactional
    void getAllPostupcisByOpisNotContainsSomething() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        // Get all the postupciList where opis does not contain DEFAULT_OPIS
        defaultPostupciShouldNotBeFound("opis.doesNotContain=" + DEFAULT_OPIS);

        // Get all the postupciList where opis does not contain UPDATED_OPIS
        defaultPostupciShouldBeFound("opis.doesNotContain=" + UPDATED_OPIS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostupciShouldBeFound(String filter) throws Exception {
        restPostupciMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postupci.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].opis").value(hasItem(DEFAULT_OPIS)));

        // Check, that the count call also returns 1
        restPostupciMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostupciShouldNotBeFound(String filter) throws Exception {
        restPostupciMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostupciMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPostupci() throws Exception {
        // Get the postupci
        restPostupciMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPostupci() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();

        // Update the postupci
        Postupci updatedPostupci = postupciRepository.findById(postupci.getId()).get();
        // Disconnect from session so that the updates on updatedPostupci are not directly saved in db
        em.detach(updatedPostupci);
        updatedPostupci.sifraPostupka(UPDATED_SIFRA_POSTUPKA).opis(UPDATED_OPIS);

        restPostupciMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPostupci.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPostupci))
            )
            .andExpect(status().isOk());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
        Postupci testPostupci = postupciList.get(postupciList.size() - 1);
        assertThat(testPostupci.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testPostupci.getOpis()).isEqualTo(UPDATED_OPIS);
    }

    @Test
    @Transactional
    void putNonExistingPostupci() throws Exception {
        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();
        postupci.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostupciMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postupci.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postupci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPostupci() throws Exception {
        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();
        postupci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostupciMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postupci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPostupci() throws Exception {
        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();
        postupci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostupciMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postupci)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostupciWithPatch() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();

        // Update the postupci using partial update
        Postupci partialUpdatedPostupci = new Postupci();
        partialUpdatedPostupci.setId(postupci.getId());

        partialUpdatedPostupci.sifraPostupka(UPDATED_SIFRA_POSTUPKA).opis(UPDATED_OPIS);

        restPostupciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostupci.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPostupci))
            )
            .andExpect(status().isOk());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
        Postupci testPostupci = postupciList.get(postupciList.size() - 1);
        assertThat(testPostupci.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testPostupci.getOpis()).isEqualTo(UPDATED_OPIS);
    }

    @Test
    @Transactional
    void fullUpdatePostupciWithPatch() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();

        // Update the postupci using partial update
        Postupci partialUpdatedPostupci = new Postupci();
        partialUpdatedPostupci.setId(postupci.getId());

        partialUpdatedPostupci.sifraPostupka(UPDATED_SIFRA_POSTUPKA).opis(UPDATED_OPIS);

        restPostupciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostupci.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPostupci))
            )
            .andExpect(status().isOk());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
        Postupci testPostupci = postupciList.get(postupciList.size() - 1);
        assertThat(testPostupci.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testPostupci.getOpis()).isEqualTo(UPDATED_OPIS);
    }

    @Test
    @Transactional
    void patchNonExistingPostupci() throws Exception {
        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();
        postupci.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostupciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postupci.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postupci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPostupci() throws Exception {
        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();
        postupci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostupciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postupci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPostupci() throws Exception {
        int databaseSizeBeforeUpdate = postupciRepository.findAll().size();
        postupci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostupciMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(postupci)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Postupci in the database
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePostupci() throws Exception {
        // Initialize the database
        postupciRepository.saveAndFlush(postupci);

        int databaseSizeBeforeDelete = postupciRepository.findAll().size();

        // Delete the postupci
        restPostupciMockMvc
            .perform(delete(ENTITY_API_URL_ID, postupci.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Postupci> postupciList = postupciRepository.findAll();
        assertThat(postupciList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
