package controller;

import database.GestorePersistenza;
import database.JpaUtil;
import entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ControlSessionTest {

    private GestorePersistenza gp;
    private Controller controller;

    // Dati di test
    private Allenatore allenatore;
    private Atleta atleta;           // ID NON sarà 77 (hardcoded nel controller)
    private Esercizio esercizioRip;
    private Esercizio esercizioTempo;
    private SessioneDiAllenamento sessione;

    // Identificatori univoci per la pulizia
    private static final String TEST_MAIL_ALLENATORE = "ctrl.all@test.com";
    private static final String TEST_MAIL_ATLETA = "ctrl.atl@test.com";
    private static final String TEST_NOME_ES_RIP = "CtrlEsRip";
    private static final String TEST_NOME_ES_TEMPO = "CtrlEsTempo";

    @BeforeEach
    void setUp() {
        gp = new GestorePersistenza();
        controller = Controller.getInstance();
        pulisciDatabase();
        creaDatiDiProva();
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
    }

    // Pulizia selettiva: elimina solo i record creati per il test
    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // Dissocia esercizi dalle sessioni
            em.createNativeQuery("DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE titolo LIKE 'CtrlTest%')")
                    .executeUpdate();

            // Elimina sessioni di test
            em.createNativeQuery("DELETE FROM sessioni WHERE titolo LIKE 'CtrlTest%'")
                    .executeUpdate();

            // Elimina esercizi di test
            em.createNativeQuery("DELETE FROM esercizi WHERE nome IN (:nome1, :nome2)")
                    .setParameter("nome1", TEST_NOME_ES_RIP)
                    .setParameter("nome2", TEST_NOME_ES_TEMPO)
                    .executeUpdate();

            // Elimina associazioni allenatore-atleta
            em.createNativeQuery("DELETE FROM allenatore_atleta WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mailAtl)")
                    .setParameter("mailAtl", TEST_MAIL_ATLETA)
                    .executeUpdate();

            // Elimina atleta e allenatore
            em.createNativeQuery("DELETE FROM atleti WHERE mail = :mailAtl")
                    .setParameter("mailAtl", TEST_MAIL_ATLETA)
                    .executeUpdate();
            em.createNativeQuery("DELETE FROM allenatori WHERE mail = :mailAll")
                    .setParameter("mailAll", TEST_MAIL_ALLENATORE)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
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

    private void creaDatiDiProva() {
        // Crea e salva allenatore e atleta
        allenatore = gp.salva(new Allenatore("Mario", "Rossi", TEST_MAIL_ALLENATORE, "pass", "Calcio"));
        atleta = gp.salva(new Atleta("Luca", "Bianchi", TEST_MAIL_ATLETA, "pass", "Corsa", 3));

        // Crea esercizi
        esercizioRip = gp.salva(new Esercizio(TEST_NOME_ES_RIP, "Descrizione rip", 10));
        esercizioTempo = gp.salva(new Esercizio(TEST_NOME_ES_TEMPO, "Descrizione tempo", Duration.ofMinutes(5)));

        // Crea sessione e associa esercizi
        sessione = new SessioneDiAllenamento("CtrlTest", "Descrizione sessione", LocalDate.now().plusDays(2), atleta, allenatore);
        sessione.setEsercizi(Arrays.asList(esercizioRip, esercizioTempo));
        sessione = gp.salva(sessione);
    }

    // ==================== getIdSessioniPerUtente ====================
    @Test
    void testGetIdSessioniPerUtente_AtletaConSessioni() {
        Set<Long> ids = controller.getIdSessioniPerUtente(atleta.getId());
        assertEquals(1, ids.size());
        assertTrue(ids.contains(sessione.getId()));
    }

    @Test
    void testGetIdSessioniPerUtente_UtenteSenzaSessioni() {
        Set<Long> ids = controller.getIdSessioniPerUtente(999999L);
        assertTrue(ids.isEmpty());
    }

    // ==================== getIdUtenteAutenticato ====================
    @Test
    void testGetIdUtenteAutenticato() {
        assertEquals(77L, controller.getIdUtenteAutenticato());
    }

    // ==================== getIdEserciziPerSessione ====================
    @Test
    void testGetIdEserciziPerSessione_Valid() {
        List<Long> ids = controller.getIdEserciziPerSessione(sessione.getId());
        assertEquals(2, ids.size());
        assertTrue(ids.contains(esercizioRip.getId()));
        assertTrue(ids.contains(esercizioTempo.getId()));
    }

    @Test
    void testGetIdEserciziPerSessione_InvalidId_ThrowsException() {
        // GestoreSessioni.dettaglioSessione lancia ResourceNotFoundException
        assertThrows(exceptions.ResourceNotFoundException.class,
                () -> controller.getIdEserciziPerSessione(999999L));
    }

    // ==================== getSessionePerId ====================
    @Test
    void testGetSessionePerId_Valid() {
        SessioneDiAllenamento s = controller.getSessionePerId(sessione.getId());
        assertNotNull(s);
        assertEquals(sessione.getId(), s.getId());
    }

    @Test
    void testGetSessionePerId_InvalidId_ThrowsException() {
        assertThrows(exceptions.ResourceNotFoundException.class,
                () -> controller.getSessionePerId(999999L));
    }

    // ==================== completaSessione ====================
    @Test
    void testCompletaSessione_AtletaNonAutenticato_CatchBlock() {
        // La sessione appartiene a un atleta con ID != 77, quindi gestore.completaSessione lancerà IllegalAccessException
        // Il controller la cattura e stampa – nessuna eccezione propagata
        Map<Long, String[]> risultatiRow = new HashMap<>();
        risultatiRow.put(esercizioRip.getId(), new String[]{"Nota buona", "15"});
        risultatiRow.put(esercizioTempo.getId(), new String[]{"Nota tempo", "8"});

        assertDoesNotThrow(() -> controller.completaSessione(sessione.getId(), risultatiRow));
        // Verifica che la sessione NON sia completata (perché l'eccezione è stata catturata)
        SessioneDiAllenamento reloaded = gp.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertEquals(StatoSessione.ASSEGNATA, reloaded.getStato()); // rimasta assegnata
    }

    @Test
    void testCompletaSessione_RisultatoNegativo_ThrowsNumberFormatException() {
        Map<Long, String[]> risultatiRow = new HashMap<>();
        risultatiRow.put(esercizioRip.getId(), new String[]{"Nota", "-5"});

        assertThrows(NumberFormatException.class,
                () -> controller.completaSessione(sessione.getId(), risultatiRow));
    }

    @Test
    void testCompletaSessione_FormatoNumeroNonValido_ThrowsNumberFormatException() {
        Map<Long, String[]> risultatiRow = new HashMap<>();
        risultatiRow.put(esercizioRip.getId(), new String[]{"Nota", "abc"});

        assertThrows(NumberFormatException.class,
                () -> controller.completaSessione(sessione.getId(), risultatiRow));
    }

    // ==================== getDettaglioEsercizioPerId ====================
    @Test
    void testGetDettaglioEsercizioPerId_ConRisultatoNull() {
        Map<String, Object> dettaglio = controller.getDettaglioEsercizioPerId(sessione.getId(), esercizioRip.getId());

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
            Esercizio e = gp.trovaPerId(Esercizio.class, esercizioRip.getId());
            e.setRisultato(20, "Ottimo");
            gp.salva(e);
        });

        Map<String, Object> dettaglio = controller.getDettaglioEsercizioPerId(sessione.getId(), esercizioRip.getId());

        assertEquals("Ottimo", dettaglio.get("nota"));
        assertEquals(20, dettaglio.get("risultato"));
        assertEquals(esercizioRip.getRisultatoAtteso(), dettaglio.get("risultato_atteso"));
    }

    @Test
    void testGetDettaglioEsercizioPerId_SessioneInesistente_ThrowsException() {
        assertThrows(exceptions.ResourceNotFoundException.class,
                () -> controller.getDettaglioEsercizioPerId(999999L, esercizioRip.getId()));
    }

    @Test
    void testGetDettaglioEsercizioPerId_EsercizioInesistente_ThrowsNullPointerException() {
        // getEsercizioPerId restituisce null se l'esercizio non è trovato, poi e.getDescrizione() causa NPE
        assertThrows(NullPointerException.class,
                () -> controller.getDettaglioEsercizioPerId(sessione.getId(), 999999L));
    }

    // ==================== getDettaglioSessionePerId ====================
    @Test
    void testGetDettaglioSessionePerId_Valid() {
        Map<String, Object> dettaglio = controller.getDettaglioSessionePerId(sessione.getId());

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
                () -> controller.getDettaglioSessionePerId(999999L));
    }
}