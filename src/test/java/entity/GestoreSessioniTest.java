package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GestoreSessioniTest {

    private GestorePersistenza gp;
    private GestoreSessioni gs;

    // References to persisted objects (no manual ID queries)
    private Allenatore allenatore;
    private Atleta atleta;
    private Esercizio esercizioRip;
    private Esercizio esercizioTempo;
    private SessioneDiAllenamento sessione;

    @BeforeEach
    void setUp() {
        gp = new GestorePersistenza();
        gs = GestoreSessioni.getInstance();
        pulisciDatabase();
        creaDatiDiProva();
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
    }

    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // Elimina le righe nella tabella di join sessione_esercizio
            em.createNativeQuery("DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mailAtleta))")
                    .setParameter("mailAtleta", "gs.atl@test.com")
                    .executeUpdate();

            // Elimina le sessioni associate all'atleta di test
            em.createNativeQuery("DELETE FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mailAtleta)")
                    .setParameter("mailAtleta", "gs.atl@test.com")
                    .executeUpdate();

            // Elimina gli esercizi di test (tramite nome)
            em.createNativeQuery("DELETE FROM esercizi WHERE nome IN ('TestRip', 'TestTempo')")
                    .executeUpdate();

            // Elimina le righe nella tabella di associazione allenatore_atleta (se esistono)
            em.createNativeQuery("DELETE FROM allenatore_atleta WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mailAtleta)")
                    .setParameter("mailAtleta", "gs.atl@test.com")
                    .executeUpdate();

            // Elimina gli obiettivi dell'atleta di test (tabella atleta_obiettivo)
            em.createNativeQuery("DELETE FROM atleta_obiettivo WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mailAtleta)")
                    .setParameter("mailAtleta", "gs.atl@test.com")
                    .executeUpdate();

            // Elimina l'atleta di test
            em.createNativeQuery("DELETE FROM atleti WHERE mail = :mailAtleta")
                    .setParameter("mailAtleta", "gs.atl@test.com")
                    .executeUpdate();

            // Elimina l'allenatore di test
            em.createNativeQuery("DELETE FROM allenatori WHERE mail = :mailAllenatore")
                    .setParameter("mailAllenatore", "gs.all@test.com")
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void creaDatiDiProva() {
        // Create and save entities using the new salva that returns managed objects
        allenatore = gp.salva(new Allenatore("Mario", "Rossi", "gs.all@test.com", "pass", "Calcio"));
        atleta = gp.salva(new Atleta("Luigi", "Verdi", "gs.atl@test.com", "pass", "Corsa", 3));
        esercizioRip = gp.salva(new Esercizio("TestRip", "Descrizione rip", 10));
        esercizioTempo = gp.salva(new Esercizio("TestTempo", "Descrizione tempo", Duration.ofMinutes(5)));

        // Create sessione
        sessione = new SessioneDiAllenamento(
                "Sessione test", "Descrizione sessione", LocalDate.now().plusDays(1), atleta, allenatore);
        sessione.setEsercizi(Arrays.asList(esercizioRip, esercizioTempo));
        sessione = gp.salva(sessione);
    }

    // ------------------------- getSessione -------------------------
    @Test
    void testGetSessione_ValidId() throws ResourceNotFoundException {
        SessioneDiAllenamento found = gs.getSessione(sessione.getId());
        assertNotNull(found);
        assertEquals(sessione.getTitolo(), found.getTitolo());
    }

    @Test
    void testGetSessione_InvalidId_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> gs.getSessione(999999L));
    }

    // ------------------------- dettaglioSessione -------------------------
    @Test
    void testDettaglioSessione_Valid() {
        List<Esercizio> esercizi = gs.dettaglioSessione(sessione.getId());
        assertNotNull(esercizi);
        assertEquals(2, esercizi.size());
        assertTrue(esercizi.stream().anyMatch(e -> e.getId().equals(esercizioRip.getId())));
        assertTrue(esercizi.stream().anyMatch(e -> e.getId().equals(esercizioTempo.getId())));
    }

    @Test
    void testDettaglioSessione_InvalidId_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> gs.dettaglioSessione(999999L));
    }

    // ------------------------- cercaSessioni -------------------------
    @Test
    void testCercaSessioni_Tutte() {
        Set<SessioneDiAllenamento> sessioni = gs.cercaSessioni();
        assertFalse(sessioni.isEmpty());
        assertTrue(sessioni.stream().anyMatch(s -> s.getId().equals(sessione.getId())));
    }

    @Test
    void testCercaSessioni_ByAtleta_Valid() {
        Set<SessioneDiAllenamento> sessioni = gs.cercaSessioni(atleta.getId());
        assertEquals(1, sessioni.size());
    }

    @Test
    void testCercaSessioni_ByAtleta_InvalidId() {
        Set<SessioneDiAllenamento> sessioni = gs.cercaSessioni(999999L);
        assertTrue(sessioni.isEmpty());
    }

    // ------------------------- completaSessione -------------------------
    @Test
    void testCompletaSessione_Success_AllResults() throws IllegalAccessException {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        risultati.put(esercizioRip.getId(), 15);
        risultati.put(esercizioTempo.getId(), 8); // 8 minuti
        note.put(esercizioRip.getId(), "Buono");
        note.put(esercizioTempo.getId(), "Ottimo");

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        // Reload sessione to verify
        SessioneDiAllenamento reloaded = gp.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.COMPLETATA, reloaded.getStato());

        for (Esercizio e : reloaded.getEsercizi()) {
            assertNotNull(e.getRisultato());
            assertNotNull(e.getRisultato().getRisultato());
            if (e.getTipo() == TipoEsercizio.RIPETIZIONI) {
                assertEquals(15, e.getRisultato().getRisultato());
            } else {
                assertEquals(Duration.ofMinutes(8), e.getRisultato().getRisultato());
            }
        }
    }

    @Test
    void testCompletaSessione_PartialResults() throws IllegalAccessException {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        risultati.put(esercizioRip.getId(), 10);
        note.put(esercizioRip.getId(), "Ok");

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        SessioneDiAllenamento reloaded = gp.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.IN_CORSO, reloaded.getStato());

        Esercizio eRip = reloaded.getEsercizi().stream()
                .filter(e -> e.getId().equals(esercizioRip.getId())).findFirst().orElseThrow();
        assertEquals(10, eRip.getRisultato().getRisultato());
        assertEquals("Ok", eRip.getRisultato().getNota());

        Esercizio eTempo = reloaded.getEsercizi().stream()
                .filter(e -> e.getId().equals(esercizioTempo.getId())).findFirst().orElseThrow();
        assertNull(eTempo.getRisultato().getRisultato());
    }

    @Test
    void testCompletaSessione_NoResults() throws IllegalAccessException {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        SessioneDiAllenamento reloaded = gp.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.IN_CORSO, reloaded.getStato());
        for (Esercizio e : reloaded.getEsercizi()) {
            assertNull(e.getRisultato().getRisultato());
        }
    }

    @Test
    void testCompletaSessione_WrongAtleta_ThrowsException() {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        assertThrows(IllegalAccessException.class, () ->
                gs.completaSessione(999999L, sessione.getId(), risultati, note));
    }

    @Test
    void testCompletaSessione_SessioneNotFound_NoException() {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        // The method catches ResourceNotFoundException and returns (prints stack trace)
        assertDoesNotThrow(() -> gs.completaSessione(atleta.getId(), 999999L, risultati, note));
    }

    @Test
    void testCompletaSessione_WithOnlyNote() throws IllegalAccessException {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        note.put(esercizioRip.getId(), "Solo nota");

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        SessioneDiAllenamento reloaded = gp.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.IN_CORSO, reloaded.getStato());

        Esercizio e = reloaded.getEsercizi().stream()
                .filter(ex -> ex.getId().equals(esercizioRip.getId())).findFirst().orElseThrow();
        assertNull(e.getRisultato().getRisultato());
        assertEquals("Solo nota", e.getRisultato().getNota());
    }
}