package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AllenatoreTest {

    private static final String TEST_MAIL_ALLENATORE = "test.all@example.com";
    private static final String TEST_MAIL_ATLETA1 = "test.atl1@example.com";
    private static final String TEST_MAIL_ATLETA2 = "test.atl2@example.com";

    private Allenatore a;
    private Atleta at1;
    private Atleta at2;

    @BeforeEach
    void setUp() {
        pulisciDatabase();
        creaDatiDiProva();
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();  // solo pulizia, senza ricreare i dati
    }

    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM allenatore_atleta WHERE allenatore_id IN (SELECT id FROM allenatori WHERE mail = :mail)")
                    .setParameter("mail", TEST_MAIL_ALLENATORE).executeUpdate();
            em.createNativeQuery("DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2)) OR allenatore_id IN (SELECT id FROM allenatori WHERE mail = :mail))")
                    .setParameter("mail1", TEST_MAIL_ATLETA1)
                    .setParameter("mail2", TEST_MAIL_ATLETA2)
                    .setParameter("mail", TEST_MAIL_ALLENATORE).executeUpdate();
            em.createNativeQuery("DELETE FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2)) OR allenatore_id IN (SELECT id FROM allenatori WHERE mail = :mail)")
                    .setParameter("mail1", TEST_MAIL_ATLETA1)
                    .setParameter("mail2", TEST_MAIL_ATLETA2)
                    .setParameter("mail", TEST_MAIL_ALLENATORE).executeUpdate();
            em.createNativeQuery("DELETE FROM atleta_obiettivo WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2))")
                    .setParameter("mail1", TEST_MAIL_ATLETA1)
                    .setParameter("mail2", TEST_MAIL_ATLETA2).executeUpdate();
            em.createNativeQuery("DELETE FROM atleti WHERE mail IN (:mail1, :mail2)")
                    .setParameter("mail1", TEST_MAIL_ATLETA1)
                    .setParameter("mail2", TEST_MAIL_ATLETA2).executeUpdate();
            em.createNativeQuery("DELETE FROM allenatori WHERE mail = :mail")
                    .setParameter("mail", TEST_MAIL_ALLENATORE).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    // Helper per eseguire operazioni con EntityManager aperto
    private void eseguiInTransazione(Consumer<EntityManager> consumer) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        em.getTransaction().begin();
        try {
            consumer.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private <T> T eseguiInTransazioneConRisultato(Function<EntityManager, T> function) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        em.getTransaction().begin();
        try {
            T result = function.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void creaDatiDiProva() {
        a = GestorePersistenza.salva(new Allenatore("Mario", "Rossi", TEST_MAIL_ALLENATORE, "pass123", "Nuoto"));
        at1 = GestorePersistenza.salva(new Atleta("Luigi", "Verdi", TEST_MAIL_ATLETA1, "pass456", "Corsa", 3));
        at2 = GestorePersistenza.salva(new Atleta("Anna", "Neri", TEST_MAIL_ATLETA2, "pass789", "Ciclismo", 2));
    }

    private Long getConteggioAtletiPerAllenatore() {
        // Ora possiamo usare direttamente a.getId() perché l'oggetto è gestito e ha l'ID
        return GestorePersistenza.eseguiQuery(
                "SELECT COUNT(a) FROM Allenatore al JOIN al.atleti a WHERE al.id = :id",
                Long.class, Map.of("id", a.getId())).get(0);
    }

    // ---------- Costruttori ----------
    @Test
    void testCostruttoriEGetters() {
        Allenatore a = new Allenatore();
        assertNull(a.getNome());
        assertTrue(a.getAtleti().isEmpty());

        Allenatore a2 = new Allenatore("Giulia", "Bianchi", "g.b@test.it", "pwd");
        assertEquals("Giulia", a2.getNome());
        assertEquals("Bianchi", a2.getCognome());
        assertEquals("g.b@test.it", a2.getMail());
        assertEquals("pwd", a2.getPassword());
        assertNull(a2.getDisciplinaPrevalente());

        Allenatore a3 = new Allenatore("Paolo", "Gialli", "p.g@test.it", "pwd", "Pallavolo");
        assertEquals("Paolo", a3.getNome());
        assertEquals("Gialli", a3.getCognome());
        assertEquals("p.g@test.it", a3.getMail());
        assertEquals("pwd", a3.getPassword());
        assertEquals("Pallavolo", a3.getDisciplinaPrevalente());
    }

    // ---------- addAtleta e getAtleti ----------
    @Test
    void testAddAtletaEGetAtleti() {
        assertEquals(0L, getConteggioAtletiPerAllenatore());

        // Aggiungi atleta1
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            Atleta atleta = em.find(Atleta.class, at1.getId());
            managed.addAtleta(atleta);
        });
        assertEquals(1L, getConteggioAtletiPerAllenatore());

        // Aggiungi atleta2
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            Atleta atleta = em.find(Atleta.class, at2.getId());
            managed.addAtleta(atleta);
        });
        assertEquals(2L, getConteggioAtletiPerAllenatore());
    }

    // ---------- removeAtleta ----------
    @Test
    void testRemoveAtleta() {
        // Aggiungi entrambi gli atleti
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            managed.addAtleta(em.find(Atleta.class, at1.getId()));
            managed.addAtleta(em.find(Atleta.class, at2.getId()));
        });
        assertEquals(2L, getConteggioAtletiPerAllenatore());

        // Rimuovi atleta1
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            managed.removeAtleta(em.find(Atleta.class, at1.getId()));
        });
        assertEquals(1L, getConteggioAtletiPerAllenatore());
    }

    // ---------- getAtleta con id valido ----------
    @Test
    void testGetAtleta_ValidId() {
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            managed.addAtleta(em.find(Atleta.class, at1.getId()));
        });

        Atleta found = eseguiInTransazioneConRisultato(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            return managed.getAtleta(at1.getId());
        });

        assertNotNull(found);
        assertEquals(TEST_MAIL_ATLETA1, found.getMail());
    }

    // ---------- getAtleta con id non valido ----------
    @Test
    void testGetAtleta_InvalidId_ThrowsException() {
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            managed.addAtleta(em.find(Atleta.class, at1.getId()));
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            eseguiInTransazioneConRisultato(em -> {
                Allenatore managed = em.find(Allenatore.class, a.getId());
                return managed.getAtleta(999999L);
            });
        });
    }

    // ---------- getSessioni ----------
    @Test
    void testGetSessioni() {
        eseguiInTransazione(em -> {
            Allenatore managedAllenatore = em.find(Allenatore.class, a.getId());
            Atleta managedAtleta = em.find(Atleta.class, at1.getId());
            SessioneDiAllenamento sessione = new SessioneDiAllenamento(
                    "Test sessione", "Descrizione", LocalDate.now(), managedAtleta, managedAllenatore);
            em.persist(sessione);
        });

        Long count = GestorePersistenza.eseguiQuery(
                "SELECT COUNT(s) FROM SessioneDiAllenamento s WHERE s.allenatore.id = :id",
                Long.class, Map.of("id", a.getId())).get(0);
        assertEquals(1L, count);
    }

    // ---------- removeAtleta quando l'atleta non è presente ----------
    @Test
    void testRemoveAtleta_AtletaNotPresent_NoSideEffects() {
        assertEquals(0L, getConteggioAtletiPerAllenatore());

        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, a.getId());
            Atleta atleta2 = em.find(Atleta.class, at2.getId());
            managed.removeAtleta(atleta2);
        });

        assertEquals(0L, getConteggioAtletiPerAllenatore());
    }

    // ---------- getAtleti (solo controllo non null) ----------
    @Test
    void testGetAtleti_ReturnsNotNull() {
        assertNotNull(a.getAtleti());
    }
}