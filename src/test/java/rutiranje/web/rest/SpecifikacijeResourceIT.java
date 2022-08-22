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
import rutiranje.domain.Specifikacije;
import rutiranje.repository.SpecifikacijeRepository;
import rutiranje.service.criteria.SpecifikacijeCriteria;

/**
 * Integration tests for the {@link SpecifikacijeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpecifikacijeResourceIT {

    private static final Integer DEFAULT_SIFRA_POSTUPKA = 1;
    private static final Integer UPDATED_SIFRA_POSTUPKA = 2;
    private static final Integer SMALLER_SIFRA_POSTUPKA = 1 - 1;

    private static final String DEFAULT_NAZIV = "AAAAAAAAAA";
    private static final String UPDATED_NAZIV = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/specifikacijes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpecifikacijeRepository specifikacijeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpecifikacijeMockMvc;

    private Specifikacije specifikacije;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specifikacije createEntity(EntityManager em) {
        Specifikacije specifikacije = new Specifikacije().sifraPostupka(DEFAULT_SIFRA_POSTUPKA).naziv(DEFAULT_NAZIV);
        return specifikacije;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specifikacije createUpdatedEntity(EntityManager em) {
        Specifikacije specifikacije = new Specifikacije().sifraPostupka(UPDATED_SIFRA_POSTUPKA).naziv(UPDATED_NAZIV);
        return specifikacije;
    }

    @BeforeEach
    public void initTest() {
        specifikacije = createEntity(em);
    }

    @Test
    @Transactional
    void createSpecifikacije() throws Exception {
        int databaseSizeBeforeCreate = specifikacijeRepository.findAll().size();
        // Create the Specifikacije
        restSpecifikacijeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specifikacije)))
            .andExpect(status().isCreated());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeCreate + 1);
        Specifikacije testSpecifikacije = specifikacijeList.get(specifikacijeList.size() - 1);
        assertThat(testSpecifikacije.getSifraPostupka()).isEqualTo(DEFAULT_SIFRA_POSTUPKA);
        assertThat(testSpecifikacije.getNaziv()).isEqualTo(DEFAULT_NAZIV);
    }

    @Test
    @Transactional
    void createSpecifikacijeWithExistingId() throws Exception {
        // Create the Specifikacije with an existing ID
        specifikacije.setId(1L);

        int databaseSizeBeforeCreate = specifikacijeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpecifikacijeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specifikacije)))
            .andExpect(status().isBadRequest());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSifraPostupkaIsRequired() throws Exception {
        int databaseSizeBeforeTest = specifikacijeRepository.findAll().size();
        // set the field null
        specifikacije.setSifraPostupka(null);

        // Create the Specifikacije, which fails.

        restSpecifikacijeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specifikacije)))
            .andExpect(status().isBadRequest());

        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpecifikacijes() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList
        restSpecifikacijeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specifikacije.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].naziv").value(hasItem(DEFAULT_NAZIV)));
    }

    @Test
    @Transactional
    void getSpecifikacije() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get the specifikacije
        restSpecifikacijeMockMvc
            .perform(get(ENTITY_API_URL_ID, specifikacije.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(specifikacije.getId().intValue()))
            .andExpect(jsonPath("$.sifraPostupka").value(DEFAULT_SIFRA_POSTUPKA))
            .andExpect(jsonPath("$.naziv").value(DEFAULT_NAZIV));
    }

    @Test
    @Transactional
    void getSpecifikacijesByIdFiltering() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        Long id = specifikacije.getId();

        defaultSpecifikacijeShouldBeFound("id.equals=" + id);
        defaultSpecifikacijeShouldNotBeFound("id.notEquals=" + id);

        defaultSpecifikacijeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSpecifikacijeShouldNotBeFound("id.greaterThan=" + id);

        defaultSpecifikacijeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSpecifikacijeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsEqualToSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka equals to DEFAULT_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.equals=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka equals to UPDATED_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.equals=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka not equals to DEFAULT_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.notEquals=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka not equals to UPDATED_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.notEquals=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsInShouldWork() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka in DEFAULT_SIFRA_POSTUPKA or UPDATED_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.in=" + DEFAULT_SIFRA_POSTUPKA + "," + UPDATED_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka equals to UPDATED_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.in=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsNullOrNotNull() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka is not null
        defaultSpecifikacijeShouldBeFound("sifraPostupka.specified=true");

        // Get all the specifikacijeList where sifraPostupka is null
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.specified=false");
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka is greater than or equal to DEFAULT_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.greaterThanOrEqual=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka is greater than or equal to UPDATED_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.greaterThanOrEqual=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka is less than or equal to DEFAULT_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.lessThanOrEqual=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka is less than or equal to SMALLER_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.lessThanOrEqual=" + SMALLER_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsLessThanSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka is less than DEFAULT_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.lessThan=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka is less than UPDATED_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.lessThan=" + UPDATED_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesBySifraPostupkaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where sifraPostupka is greater than DEFAULT_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldNotBeFound("sifraPostupka.greaterThan=" + DEFAULT_SIFRA_POSTUPKA);

        // Get all the specifikacijeList where sifraPostupka is greater than SMALLER_SIFRA_POSTUPKA
        defaultSpecifikacijeShouldBeFound("sifraPostupka.greaterThan=" + SMALLER_SIFRA_POSTUPKA);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesByNazivIsEqualToSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where naziv equals to DEFAULT_NAZIV
        defaultSpecifikacijeShouldBeFound("naziv.equals=" + DEFAULT_NAZIV);

        // Get all the specifikacijeList where naziv equals to UPDATED_NAZIV
        defaultSpecifikacijeShouldNotBeFound("naziv.equals=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesByNazivIsNotEqualToSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where naziv not equals to DEFAULT_NAZIV
        defaultSpecifikacijeShouldNotBeFound("naziv.notEquals=" + DEFAULT_NAZIV);

        // Get all the specifikacijeList where naziv not equals to UPDATED_NAZIV
        defaultSpecifikacijeShouldBeFound("naziv.notEquals=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesByNazivIsInShouldWork() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where naziv in DEFAULT_NAZIV or UPDATED_NAZIV
        defaultSpecifikacijeShouldBeFound("naziv.in=" + DEFAULT_NAZIV + "," + UPDATED_NAZIV);

        // Get all the specifikacijeList where naziv equals to UPDATED_NAZIV
        defaultSpecifikacijeShouldNotBeFound("naziv.in=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesByNazivIsNullOrNotNull() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where naziv is not null
        defaultSpecifikacijeShouldBeFound("naziv.specified=true");

        // Get all the specifikacijeList where naziv is null
        defaultSpecifikacijeShouldNotBeFound("naziv.specified=false");
    }

    @Test
    @Transactional
    void getAllSpecifikacijesByNazivContainsSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where naziv contains DEFAULT_NAZIV
        defaultSpecifikacijeShouldBeFound("naziv.contains=" + DEFAULT_NAZIV);

        // Get all the specifikacijeList where naziv contains UPDATED_NAZIV
        defaultSpecifikacijeShouldNotBeFound("naziv.contains=" + UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void getAllSpecifikacijesByNazivNotContainsSomething() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        // Get all the specifikacijeList where naziv does not contain DEFAULT_NAZIV
        defaultSpecifikacijeShouldNotBeFound("naziv.doesNotContain=" + DEFAULT_NAZIV);

        // Get all the specifikacijeList where naziv does not contain UPDATED_NAZIV
        defaultSpecifikacijeShouldBeFound("naziv.doesNotContain=" + UPDATED_NAZIV);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpecifikacijeShouldBeFound(String filter) throws Exception {
        restSpecifikacijeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specifikacije.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].naziv").value(hasItem(DEFAULT_NAZIV)));

        // Check, that the count call also returns 1
        restSpecifikacijeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpecifikacijeShouldNotBeFound(String filter) throws Exception {
        restSpecifikacijeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpecifikacijeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpecifikacije() throws Exception {
        // Get the specifikacije
        restSpecifikacijeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSpecifikacije() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();

        // Update the specifikacije
        Specifikacije updatedSpecifikacije = specifikacijeRepository.findById(specifikacije.getId()).get();
        // Disconnect from session so that the updates on updatedSpecifikacije are not directly saved in db
        em.detach(updatedSpecifikacije);
        updatedSpecifikacije.sifraPostupka(UPDATED_SIFRA_POSTUPKA).naziv(UPDATED_NAZIV);

        restSpecifikacijeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpecifikacije.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpecifikacije))
            )
            .andExpect(status().isOk());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
        Specifikacije testSpecifikacije = specifikacijeList.get(specifikacijeList.size() - 1);
        assertThat(testSpecifikacije.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testSpecifikacije.getNaziv()).isEqualTo(UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void putNonExistingSpecifikacije() throws Exception {
        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();
        specifikacije.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpecifikacijeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, specifikacije.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(specifikacije))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpecifikacije() throws Exception {
        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();
        specifikacije.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecifikacijeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(specifikacije))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpecifikacije() throws Exception {
        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();
        specifikacije.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecifikacijeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(specifikacije)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpecifikacijeWithPatch() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();

        // Update the specifikacije using partial update
        Specifikacije partialUpdatedSpecifikacije = new Specifikacije();
        partialUpdatedSpecifikacije.setId(specifikacije.getId());

        partialUpdatedSpecifikacije.naziv(UPDATED_NAZIV);

        restSpecifikacijeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpecifikacije.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpecifikacije))
            )
            .andExpect(status().isOk());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
        Specifikacije testSpecifikacije = specifikacijeList.get(specifikacijeList.size() - 1);
        assertThat(testSpecifikacije.getSifraPostupka()).isEqualTo(DEFAULT_SIFRA_POSTUPKA);
        assertThat(testSpecifikacije.getNaziv()).isEqualTo(UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void fullUpdateSpecifikacijeWithPatch() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();

        // Update the specifikacije using partial update
        Specifikacije partialUpdatedSpecifikacije = new Specifikacije();
        partialUpdatedSpecifikacije.setId(specifikacije.getId());

        partialUpdatedSpecifikacije.sifraPostupka(UPDATED_SIFRA_POSTUPKA).naziv(UPDATED_NAZIV);

        restSpecifikacijeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpecifikacije.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpecifikacije))
            )
            .andExpect(status().isOk());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
        Specifikacije testSpecifikacije = specifikacijeList.get(specifikacijeList.size() - 1);
        assertThat(testSpecifikacije.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testSpecifikacije.getNaziv()).isEqualTo(UPDATED_NAZIV);
    }

    @Test
    @Transactional
    void patchNonExistingSpecifikacije() throws Exception {
        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();
        specifikacije.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpecifikacijeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, specifikacije.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(specifikacije))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpecifikacije() throws Exception {
        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();
        specifikacije.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecifikacijeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(specifikacije))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpecifikacije() throws Exception {
        int databaseSizeBeforeUpdate = specifikacijeRepository.findAll().size();
        specifikacije.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecifikacijeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(specifikacije))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Specifikacije in the database
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpecifikacije() throws Exception {
        // Initialize the database
        specifikacijeRepository.saveAndFlush(specifikacije);

        int databaseSizeBeforeDelete = specifikacijeRepository.findAll().size();

        // Delete the specifikacije
        restSpecifikacijeMockMvc
            .perform(delete(ENTITY_API_URL_ID, specifikacije.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Specifikacije> specifikacijeList = specifikacijeRepository.findAll();
        assertThat(specifikacijeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
