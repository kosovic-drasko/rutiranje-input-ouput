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
import rutiranje.domain.Ponude;
import rutiranje.repository.PonudeRepository;
import rutiranje.service.criteria.PonudeCriteria;

/**
 * Integration tests for the {@link PonudeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PonudeResourceIT {

    private static final Integer DEFAULT_SIFRA_POSTUPKA = 1;
    private static final Integer UPDATED_SIFRA_POSTUPKA = 2;
    private static final Integer SMALLER_SIFRA_POSTUPKA = 1 - 1;

    private static final String DEFAULT_NAZIV = "AAAAAAAAAA";
    private static final String UPDATED_NAZIV = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ponudes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PonudeRepository ponudeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPonudeMockMvc;

    private Ponude ponude;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ponude createEntity(EntityManager em) {
        Ponude ponude = new Ponude().sifraPostupka(DEFAULT_SIFRA_POSTUPKA).naziv(DEFAULT_NAZIV);
        return ponude;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ponude createUpdatedEntity(EntityManager em) {
        Ponude ponude = new Ponude().sifraPostupka(UPDATED_SIFRA_POSTUPKA).naziv(UPDATED_NAZIV);
        return ponude;
    }

    @BeforeEach
    public void initTest() {
        ponude = createEntity(em);
    }

    @Test
    @Transactional
    void createPonude() throws Exception {
        int databaseSizeBeforeCreate = ponudeRepository.findAll().size();
        // Create the Ponude
        restPonudeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isCreated());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeCreate + 1);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPostupka()).isEqualTo(DEFAULT_SIFRA_POSTUPKA);
        assertThat(testPonude.getNaziv()).isEqualTo(DEFAULT_NAZIV);
    }

    @Test
    @Transactional
    void createPonudeWithExistingId() throws Exception {
        // Create the Ponude with an existing ID
        ponude.setId(1L);

        int databaseSizeBeforeCreate = ponudeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPonudeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSifraPostupkaIsRequired() throws Exception {
        int databaseSizeBeforeTest = ponudeRepository.findAll().size();
        // set the field null
        ponude.setSifraPostupka(null);

        // Create the Ponude, which fails.

        restPonudeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isBadRequest());

        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPonudes() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ponude.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].naziv").value(hasItem(DEFAULT_NAZIV)));
    }

    @Test
    @Transactional
    void getPonude() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get the ponude
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL_ID, ponude.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ponude.getId().intValue()))
            .andExpect(jsonPath("$.sifraPostupka").value(DEFAULT_SIFRA_POSTUPKA))
            .andExpect(jsonPath("$.naziv").value(DEFAULT_NAZIV));
    }

    @Test
    @Transactional
    void getPonudesByIdFiltering() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        Long id = ponude.getId();

        defaultPonudeShouldBeFound("id.equals=" + id);
        defaultPonudeShouldNotBeFound("id.notEquals=" + id);

        defaultPonudeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPonudeShouldNotBeFound("id.greaterThan=" + id);

        defaultPonudeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPonudeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka equals to DEFAULT_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.equals=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka equals to UPDATED_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.equals=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka not equals to DEFAULT_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.notEquals=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka not equals to UPDATED_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.notEquals=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsInShouldWork() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka in DEFAULT_SIFRA_POSTUPKA or UPDATED_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.in=" + DEFAULT_SIFRA_POSTUPKA + "," + UPDATED_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka equals to UPDATED_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.in=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsNullOrNotNull() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka is not null
        defaultPonudeShouldBeFound("sifraPostupka.specified=true");

        // Get all the ponudeList where sifraPostupka is null
        defaultPonudeShouldNotBeFound("sifraPostupka.specified=false");
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka is greater than or equal to DEFAULT_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.greaterThanOrEqual=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka is greater than or equal to UPDATED_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.greaterThanOrEqual=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka is less than or equal to DEFAULT_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.lessThanOrEqual=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka is less than or equal to SMALLER_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.lessThanOrEqual=" + SMALLER_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsLessThanSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka is less than DEFAULT_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.lessThan=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka is less than UPDATED_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.lessThan=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPostupkaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPostupka is greater than DEFAULT_SIFRA_POSTUPKA
        defaultPonudeShouldNotBeFound("sifraPostupka.greaterThan=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the ponudeList where sifraPostupka is greater than SMALLER_SIFRA_POSTUPKA
        defaultPonudeShouldBeFound("sifraPostupka.greaterThan=" + SMALLER_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllPonudesByNazivIsEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where naziv equals to DEFAULT_NAZIV
        defaultPonudeShouldBeFound("naziv.equals=" + DEFAULT_NAZIV);

        // Get all the ponudeList where naziv equals to UPDATED_NAZIV
        defaultPonudeShouldNotBeFound("naziv.equals=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllPonudesByNazivIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where naziv not equals to DEFAULT_NAZIV
        defaultPonudeShouldNotBeFound("naziv.notEquals=" + DEFAULT_NAZIV);

        // Get all the ponudeList where naziv not equals to UPDATED_NAZIV
        defaultPonudeShouldBeFound("naziv.notEquals=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllPonudesByNazivIsInShouldWork() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where naziv in DEFAULT_NAZIV or UPDATED_NAZIV
        defaultPonudeShouldBeFound("naziv.in=" + DEFAULT_NAZIV + "," + UPDATED_NAZIV);

        // Get all the ponudeList where naziv equals to UPDATED_NAZIV
        defaultPonudeShouldNotBeFound("naziv.in=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllPonudesByNazivIsNullOrNotNull() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where naziv is not null
        defaultPonudeShouldBeFound("naziv.specified=true");

        // Get all the ponudeList where naziv is null
        defaultPonudeShouldNotBeFound("naziv.specified=false");
    }

    @Test
    @Transactional
    void getAllPonudesByNazivContainsSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where naziv contains DEFAULT_NAZIV
        defaultPonudeShouldBeFound("naziv.contains=" + DEFAULT_NAZIV);

        // Get all the ponudeList where naziv contains UPDATED_NAZIV
        defaultPonudeShouldNotBeFound("naziv.contains=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllPonudesByNazivNotContainsSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where naziv does not contain DEFAULT_NAZIV
        defaultPonudeShouldNotBeFound("naziv.doesNotContain=" + DEFAULT_NAZIV);

        // Get all the ponudeList where naziv does not contain UPDATED_NAZIV
        defaultPonudeShouldBeFound("naziv.doesNotContain=" + UPDATED_NAZIV);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPonudeShouldBeFound(String filter) throws Exception {
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ponude.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].naziv").value(hasItem(DEFAULT_NAZIV)));

        // Check, that the count call also returns 1
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPonudeShouldNotBeFound(String filter) throws Exception {
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPonude() throws Exception {
        // Get the ponude
        restPonudeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPonude() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();

        // Update the ponude
        Ponude updatedPonude = ponudeRepository.findById(ponude.getId()).get();
        // Disconnect from session so that the updates on updatedPonude are not directly saved in db
        em.detach(updatedPonude);
        updatedPonude.sifraPostupka(UPDATED_SIFRA_POSTUPKA).naziv(UPDATED_NAZIV);

        restPonudeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPonude.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPonude))
            )
            .andExpect(status().isOk());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testPonude.getNaziv()).isEqualTo(UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void putNonExistingPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ponude.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePonudeWithPatch() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();

        // Update the ponude using partial update
        Ponude partialUpdatedPonude = new Ponude();
        partialUpdatedPonude.setId(ponude.getId());

        partialUpdatedPonude.sifraPostupka(UPDATED_SIFRA_POSTUPKA);

        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPonude.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPonude))
            )
            .andExpect(status().isOk());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testPonude.getNaziv()).isEqualTo(DEFAULT_NAZIV);
    }

    @Test
    @Transactional
    void fullUpdatePonudeWithPatch() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();

        // Update the ponude using partial update
        Ponude partialUpdatedPonude = new Ponude();
        partialUpdatedPonude.setId(ponude.getId());

        partialUpdatedPonude.sifraPostupka(UPDATED_SIFRA_POSTUPKA).naziv(UPDATED_NAZIV);

        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPonude.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPonude))
            )
            .andExpect(status().isOk());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testPonude.getNaziv()).isEqualTo(UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void patchNonExistingPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ponude.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePonude() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeDelete = ponudeRepository.findAll().size();

        // Delete the ponude
        restPonudeMockMvc
            .perform(delete(ENTITY_API_URL_ID, ponude.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
