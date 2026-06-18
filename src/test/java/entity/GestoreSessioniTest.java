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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GestoreSessioniTest {
    
    private GestoreSessioni gs;

    // References to persisted objects (no manual ID queries)
    private Allenatore allenatore;
    private Atleta atleta;
    private Esercizio esercizioRip;
    private Esercizio esercizioTempo;
    private SessioneDiAllenamento sessione;

    @BeforeEach
    void setUp() {
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
        allenatore = GestorePersistenza.salva(new Allenatore("Mario", "Rossi", "gs.all@test.com", "pass", "Calcio"));
        atleta = GestorePersistenza.salva(new Atleta("Luigi", "Verdi", "gs.atl@test.com", "pass", "Corsa", 3));
        esercizioRip = GestorePersistenza.salva(new Esercizio("TestRip", "Descrizione rip", 10));
        esercizioTempo = GestorePersistenza.salva(new Esercizio("TestTempo", "Descrizione tempo", Duration.ofMinutes(5)));

        // Create sessione
        sessione = new SessioneDiAllenamento(
                "Sessione test", "Descrizione sessione", LocalDate.now().plusDays(1), atleta, allenatore);
        sessione.setEsercizi(Arrays.asList(esercizioRip, esercizioTempo));
        sessione = GestorePersistenza.salva(sessione);
    }

    // Helper per eseguire codice in transazione (utile per settare risultati)
    private void eseguiInTransazione(Runnable runnable) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        em.getTransaction().begin();
        try {
            runnable.run();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
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
        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
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

        assertTrue(gs.isSessionCompleted(sessione.getId()));
    }

    @Test
    void testCompletaSessione_PartialResults() throws IllegalAccessException {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        risultati.put(esercizioRip.getId(), 10);
        note.put(esercizioRip.getId(), "Ok");

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.IN_CORSO, reloaded.getStato());

        Esercizio eRip = reloaded.getEsercizi().stream()
                .filter(e -> e.getId().equals(esercizioRip.getId())).findFirst().orElseThrow();
        assertEquals(10, eRip.getRisultato().getRisultato());
        assertEquals("Ok", eRip.getRisultato().getNota());

        Esercizio eTempo = reloaded.getEsercizi().stream()
                .filter(e -> e.getId().equals(esercizioTempo.getId())).findFirst().orElseThrow();
        assertNull(eTempo.getRisultato());
        assertFalse(gs.isSessionCompleted(sessione.getId()));
    }

    @Test
    void testCompletaSessione_NoResults() throws IllegalAccessException {
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.IN_CORSO, reloaded.getStato());
        for (Esercizio e : reloaded.getEsercizi()) {
            assertNull(e.getRisultato());
        }
        assertFalse(gs.isSessionCompleted(sessione.getId()));
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

        /*
        Se l'esercizio riceve solo la nota, l'esercizio non è completato, in quanto deve necessariamente ricevere il risultato
         */

        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        note.put(esercizioRip.getId(), "Solo nota");

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.IN_CORSO, reloaded.getStato());

        Esercizio e = reloaded.getEsercizi().stream()
                .filter(ex -> ex.getId().equals(esercizioRip.getId())).findFirst().orElseThrow();
        assertNull(e.getRisultato());
        assertFalse(gs.isSessionCompleted(sessione.getId()));
    }

    @Test
    void testCompletaSessione_SessioneGiaCompletata() throws IllegalAccessException{
        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();
        risultati.put(esercizioRip.getId(), 15);
        risultati.put(esercizioTempo.getId(), 8); // 8 minuti
        note.put(esercizioRip.getId(), "Buono");
        note.put(esercizioTempo.getId(), "Ottimo");

        gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note);

        assertThrows(IllegalAccessException.class, () ->
                gs.completaSessione(atleta.getId(), sessione.getId(), risultati, note));
    }

    // ==================== getIdSessioniPerUtente ====================
    @Test
    void testGetIdSessioniPerUtente_AtletaConSessioni() {
        List<Long> ids = gs.getIdSessioniForUtente(atleta.getId());
        assertEquals(1, ids.size());
        assertTrue(ids.contains(sessione.getId()));
    }

    @Test
    void testGetIdSessioniPerUtente_UtenteSenzaSessioni() {
        List<Long> ids = gs.getIdSessioniForUtente(999999L);
        assertTrue(ids.isEmpty());
    }

    // ==================== getDettaglioSessionePerId ====================
    @Test
    void testGetDettaglioSessionePerId_Valid() {
        Map<String, Object> dettaglio = gs.getDettaglioSessionePerId(sessione.getId());

        assertEquals(sessione.getTitolo(), dettaglio.get("titolo"));
        assertEquals(allenatore.getNome() + " " + allenatore.getCognome(), dettaglio.get("allenatore"));
        assertEquals(sessione.getDescrizione(), dettaglio.get("descrizione"));
        assertEquals(sessione.getStato().toString(), dettaglio.get("stato"));
        assertEquals(sessione.getDataSvolgimento(), dettaglio.get("data"));
        assertEquals(sessione.getAllenatore().getMail(), dettaglio.get("email_allenatore"));
    }

    @Test
    void testGetDettaglioSessionePerId_InvalidId_ThrowsException() {
        assertThrows(exceptions.ResourceNotFoundException.class,
                () -> gs.getDettaglioSessionePerId(999999L));
    }

    // ==================== getIdEserciziPerSessione ====================
    @Test
    void testGetIdEserciziPerSessione_Valid() {
        List<Long> ids = gs.getIdEserciziForSessione(sessione.getId());
        assertEquals(2, ids.size());
        assertTrue(ids.contains(esercizioRip.getId()));
        assertTrue(ids.contains(esercizioTempo.getId()));
    }

    @Test
    void testGetIdEserciziPerSessione_InvalidId_ThrowsException() {
        // GestoreSessioni.dettaglioSessione lancia ResourceNotFoundException
        assertThrows(exceptions.ResourceNotFoundException.class,
                () -> gs.getIdEserciziForSessione(999999L));
    }

    // ==================== getDettaglioEsercizioPerId ====================
    @Test
    void testGetDettaglioEsercizioPerId_ConRisultatoNull() {
        Map<String, Object> dettaglio = gs.getDettaglioEsercizioPerId(sessione.getId(), esercizioRip.getId());

        assertEquals(esercizioRip.getDescrizione(), dettaglio.get("descrizione"));
        assertEquals(esercizioRip.getNome(), dettaglio.get("nome"));
        assertEquals(sessione.getStato().toString(), dettaglio.get("stato"));
        assertNull(dettaglio.get("nota"));
        assertNull(dettaglio.get("risultato"));
        assertEquals(esercizioRip.getRisultatoAtteso(), dettaglio.get("risultato_atteso"));
    }

    @Test
    void testGetDettaglioEsercizioPerId_ConRisultatoValido() {
        // Imposta un risultato sull'esercizio
        eseguiInTransazione(() -> {
            Esercizio e = GestorePersistenza.trovaPerId(Esercizio.class, esercizioRip.getId());
            e.setRisultato(20, "Ottimo");
            GestorePersistenza.salva(e);
        });

        Map<String, Object> dettaglio = gs.getDettaglioEsercizioPerId(sessione.getId(), esercizioRip.getId());

        assertEquals("Ottimo", dettaglio.get("nota"));
        assertEquals(20, dettaglio.get("risultato"));
        assertEquals(esercizioRip.getRisultatoAtteso(), dettaglio.get("risultato_atteso"));
    }

    @Test
    void testGetDettaglioEsercizioPerId_SessioneInesistente_ThrowsException() {
        assertThrows(exceptions.ResourceNotFoundException.class,
                () -> gs.getDettaglioEsercizioPerId(999999L, esercizioRip.getId()));
    }

    @Test
    void testGetDettaglioEsercizioPerId_EsercizioInesistente_ThrowsNullPointerException() {
        // getEsercizioPerId restituisce null se l'esercizio non è trovato, poi e.getDescrizione() causa NPE
        assertThrows(NullPointerException.class,
                () -> gs.getDettaglioEsercizioPerId(sessione.getId(), 999999L));
    }

}

