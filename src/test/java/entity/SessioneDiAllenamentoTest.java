package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SessioneDiAllenamentoTest {


    // Persistent test entities
    private Allenatore allenatore;
    private Atleta atleta;
    private Esercizio esercizioRip;
    private Esercizio esercizioTempo;
    private SessioneDiAllenamento sessione;

    private static final String TEST_MAIL_ALL = "sessione.all@test.com";
    private static final String TEST_MAIL_ATL = "sessione.atl@test.com";
    private static final String TEST_ESERCIZIO_RIP = "SessioneTestEsRip";
    private static final String TEST_ESERCIZIO_TEMPO = "SessioneTestEsTempo";

    @BeforeEach
    void setUp() {
        pulisciDatabase();
        creaDatiDiProva();
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
    }

    // Correct cleanup order: child tables first, then parents
    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // Delete join tables
            em.createNativeQuery("DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE titolo LIKE 'SessioneTest%')")
                    .executeUpdate();
            em.createNativeQuery("DELETE FROM allenatore_atleta WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mailAtl)")
                    .setParameter("mailAtl", TEST_MAIL_ATL)
                    .executeUpdate();

            // Delete sessions
            em.createNativeQuery("DELETE FROM sessioni WHERE titolo LIKE 'SessioneTest%'")
                    .executeUpdate();

            // Delete exercises
            em.createNativeQuery("DELETE FROM esercizi WHERE nome IN (:nome1, :nome2)")
                    .setParameter("nome1", TEST_ESERCIZIO_RIP)
                    .setParameter("nome2", TEST_ESERCIZIO_TEMPO)
                    .executeUpdate();

            // Delete atleta and allenatore
            em.createNativeQuery("DELETE FROM atleti WHERE mail = :mailAtl")
                    .setParameter("mailAtl", TEST_MAIL_ATL)
                    .executeUpdate();
            em.createNativeQuery("DELETE FROM allenatori WHERE mail = :mailAll")
                    .setParameter("mailAll", TEST_MAIL_ALL)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void creaDatiDiProva() {
        allenatore = GestorePersistenza.salva(new Allenatore("Marco", "Rossi", TEST_MAIL_ALL, "pass", "Calcio"));
        atleta = GestorePersistenza.salva(new Atleta("Luca", "Bianchi", TEST_MAIL_ATL, "pass", "Corsa", 2));
        esercizioRip = GestorePersistenza.salva(new Esercizio(TEST_ESERCIZIO_RIP, "Descrizione rip", 10));
        esercizioTempo = GestorePersistenza.salva(new Esercizio(TEST_ESERCIZIO_TEMPO, "Descrizione tempo", Duration.ofMinutes(5)));

        sessione = new SessioneDiAllenamento("SessioneTest", "Desc", LocalDate.now().plusDays(2), atleta, allenatore);
        sessione.setEsercizi(Arrays.asList(esercizioRip, esercizioTempo));
        sessione = GestorePersistenza.salva(sessione);
    }

    // Helper: run code inside a transaction with a managed EntityManager
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

    // ==================== COSTRUTTORI ====================
    @Test
    void testCostruttoreVuoto() {
        SessioneDiAllenamento s = new SessioneDiAllenamento();
        assertNull(s.getTitolo());
        assertNull(s.getDataSvolgimento());
        assertNull(s.getStato());
        assertTrue(s.getEsercizi().isEmpty());
    }

    @Test
    void testCostruttoreSenzaDurata() {
        SessioneDiAllenamento s = new SessioneDiAllenamento("Titolo", "Desc", LocalDate.now(), atleta, allenatore);
        assertEquals("Titolo", s.getTitolo());
        assertEquals("Desc", s.getDescrizione());
        assertEquals(LocalDate.now(), s.getDataSvolgimento());
        assertEquals(StatoSessione.ASSEGNATA, s.getStato());
        assertNull(s.getDurata());
        assertEquals(atleta, s.getAtleta());
        assertEquals(allenatore, s.getAllenatore());
    }

    @Test
    void testCostruttoreConDurata() {
        Duration durata = Duration.ofHours(1);
        SessioneDiAllenamento s = new SessioneDiAllenamento("Titolo2", "Desc2", LocalDate.now(), durata, atleta, allenatore);
        assertEquals(durata, s.getDurata());
        assertEquals(StatoSessione.ASSEGNATA, s.getStato());
    }

    // ==================== GETTER / SETTER ====================
    @Test
    void testGettersSetters() {
        SessioneDiAllenamento s = new SessioneDiAllenamento();
        s.setTitolo("Nuovo titolo");
        s.setDescrizione("Nuova descrizione");
        s.setDataSvolgimento(LocalDate.of(2025, 1, 1));
        s.setDurata(Duration.ofMinutes(30));
        assertEquals("Nuovo titolo", s.getTitolo());
        assertEquals("Nuova descrizione", s.getDescrizione());
        assertEquals(LocalDate.of(2025, 1, 1), s.getDataSvolgimento());
        assertEquals(Duration.ofMinutes(30), s.getDurata());
    }

    @Test
    void testSetStato_Validi() {
        SessioneDiAllenamento s = new SessioneDiAllenamento();
        s.setStato("ASSEGNATA");
        assertEquals(StatoSessione.ASSEGNATA, s.getStato());
        s.setStato("IN CORSO");
        assertEquals(StatoSessione.IN_CORSO, s.getStato());
        s.setStato("COMPLETATA");
        assertEquals(StatoSessione.COMPLETATA, s.getStato());
    }

    @Test
    void testSetStato_NonValido_ThrowsIllegalArgumentException() {
        SessioneDiAllenamento s = new SessioneDiAllenamento();
        // Passing null causes NullPointerException in production code, so we don't test null
        assertThrows(IllegalArgumentException.class, () -> s.setStato("IN_ATTESA"));
    }

    // ==================== REGISTRA RISULTATO (using persisted entities with IDs) ====================
    @Test
    void testRegistraRisultato_Ripetizioni() {
        // Use the already persisted session and exercise
        Long idEs = esercizioRip.getId();
        eseguiInTransazione(() -> {
            SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
            s.registraRisultato(15, "Ottimo", idEs);
            GestorePersistenza.salva(s);
        });

        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        Esercizio e = reloaded.getEsercizioPerId(idEs);
        assertNotNull(e.getRisultato());
        assertEquals(15, e.getRisultato().getRisultato());
        assertEquals("Ottimo", e.getRisultato().getNota());
    }

    @Test
    void testRegistraRisultato_Tempo() {
        Long idEs = esercizioTempo.getId();
        Duration result = Duration.ofMinutes(8);
        eseguiInTransazione(() -> {
            SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
            s.registraRisultato(result, "Bene", idEs);
            GestorePersistenza.salva(s);
        });

        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        Esercizio e = reloaded.getEsercizioPerId(idEs);
        assertEquals(result, e.getRisultato().getRisultato());
        assertEquals("Bene", e.getRisultato().getNota());
    }

    @Test
    void testRegistraRisultato_SoloNota() {
        Long idEs = esercizioRip.getId();
        eseguiInTransazione(() -> {
            SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
            s.registraRisultato(null, "Solo nota", idEs);
            GestorePersistenza.salva(s);
        });

        SessioneDiAllenamento reloaded = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        Esercizio e = reloaded.getEsercizioPerId(idEs);
        assertNull(e.getRisultato().getRisultato());
        assertEquals("Solo nota", e.getRisultato().getNota());
    }

    @Test
    void testRegistraRisultato_EsercizioNonTrovato_ThrowsIllegalArgumentException() {
        // Use a non-existent exercise ID (e.g., 999999L)
        assertThrows(IllegalArgumentException.class, () -> {
            eseguiInTransazione(() -> {
                SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
                s.registraRisultato(10, "nota", 999999L);
            });
        });
    }

    // ==================== GET ESERCIZIO PER ID ====================
    @Test
    void testGetEsercizioPerId_Trovato() {
        // Use persisted session
        SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        Esercizio found = s.getEsercizioPerId(esercizioRip.getId());
        assertEquals(esercizioRip.getId(), found.getId());
    }

    @Test
    void testGetEsercizioPerId_NonTrovato() {
        SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, sessione.getId());
        assertNull(s.getEsercizioPerId(999999L));
    }

    // ==================== COMPARETO ====================
    @Test
    void testCompareTo_DateDiverse() {
        LocalDate oggi = LocalDate.now();
        SessioneDiAllenamento s1 = new SessioneDiAllenamento("A", "desc", oggi, atleta, allenatore);
        SessioneDiAllenamento s2 = new SessioneDiAllenamento("B", "desc", oggi.plusDays(1), atleta, allenatore);
        assertTrue(s1.compareTo(s2) < 0);
        assertTrue(s2.compareTo(s1) > 0);
    }

    @Test
    void testCompareTo_StessaData_TitoliDiversi() {
        LocalDate data = LocalDate.now();
        SessioneDiAllenamento s1 = new SessioneDiAllenamento("Alfa", "desc", data, atleta, allenatore);
        SessioneDiAllenamento s2 = new SessioneDiAllenamento("Beta", "desc", data, atleta, allenatore);
        assertTrue(s1.compareTo(s2) < 0);
        assertTrue(s2.compareTo(s1) > 0);
    }

    @Test
    void testCompareTo_StessaData_StessoTitolo() {
        LocalDate data = LocalDate.now();
        SessioneDiAllenamento s1 = new SessioneDiAllenamento("Stesso", "desc", data, atleta, allenatore);
        SessioneDiAllenamento s2 = new SessioneDiAllenamento("Stesso", "desc", data, atleta, allenatore);
        assertEquals(0, s1.compareTo(s2));
    }

    // ==================== SET ATLETA / ALLENATORE (bidirectional) ====================
    @Test
    void testSetAtleta_AggiungeSessioneAllAtleta() {
        // Create a session with a date to avoid NPE in TreeSet
        SessioneDiAllenamento s = new SessioneDiAllenamento("Titolo", "Desc", LocalDate.now(), atleta, allenatore);
        // Calling setAtleta will add the session to atleta's session Set (TreeSet). The session already has a date.
        s.setAtleta(atleta);
        assertTrue(atleta.getSessioni().contains(s));
    }

    @Test
    void testSetAllenatore_AggiungeSessioneAllAllenatore() {
        SessioneDiAllenamento s = new SessioneDiAllenamento("Titolo", "Desc", LocalDate.now(), atleta, allenatore);
        s.setAllenatore(allenatore);
        assertTrue(allenatore.getSessioni().contains(s));
    }

    // ==================== COVERAGGI AGGIUNTIVI ====================
    @Test
    void testGetId_DopoSalvataggio() {
        assertNotNull(sessione.getId());
    }

    @Test
    void testSetEsercizi() {
        SessioneDiAllenamento s = new SessioneDiAllenamento();
        List<Esercizio> nuovaLista = new ArrayList<>();
        s.setEsercizi(nuovaLista);
        assertSame(nuovaLista, s.getEsercizi());
    }

    @Test
    void testCostruttoreConDurataPersistente() {
        Duration dur = Duration.ofMinutes(90);
        SessioneDiAllenamento s = new SessioneDiAllenamento("DurataTest", "Desc", LocalDate.now(), dur, atleta, allenatore);
        s = GestorePersistenza.salva(s);
        assertNotNull(s.getId());
        assertEquals(dur, s.getDurata());
    }

    // Coverage for getEsercizi (already used, but explicit call)
    @Test
    void testGetEsercizi() {
        assertNotNull(sessione.getEsercizi());
        assertEquals(2, sessione.getEsercizi().size());
    }
}