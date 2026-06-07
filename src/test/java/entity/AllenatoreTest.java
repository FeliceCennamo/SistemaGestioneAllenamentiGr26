package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AllenatoreTest {

    private GestorePersistenza gp;

    private static final String TEST_MAIL_ALLENATORE = "test.all@example.com";
    private static final String TEST_MAIL_ATLETA1 = "test.atl1@example.com";
    private static final String TEST_MAIL_ATLETA2 = "test.atl2@example.com";

    private Long idAllenatore;
    private Long idAtleta1;
    private Long idAtleta2;

    @BeforeEach
    void setUp() {
        gp = new GestorePersistenza();
        pulisciDatabase();
        creaDatiDiProva();
        idAllenatore = getIdAllenatore();
        idAtleta1 = getIdAtleta(TEST_MAIL_ATLETA1);
        idAtleta2 = getIdAtleta(TEST_MAIL_ATLETA2);
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
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

    private void creaDatiDiProva() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Allenatore a = new Allenatore("Mario", "Rossi", TEST_MAIL_ALLENATORE, "pass123", "Nuoto");
            Atleta at1 = new Atleta("Luigi", "Verdi", TEST_MAIL_ATLETA1, "pass456", "Corsa", 3);
            Atleta at2 = new Atleta("Anna", "Neri", TEST_MAIL_ATLETA2, "pass789", "Ciclismo", 2);
            em.persist(a);
            em.persist(at1);
            em.persist(at2);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private Long getIdAllenatore() {
        List<Allenatore> list = gp.eseguiQuery("SELECT a FROM Allenatore a WHERE a.mail = :mail", Allenatore.class,
                Map.of("mail", TEST_MAIL_ALLENATORE));
        return list.isEmpty() ? null : list.get(0).getId();
    }

    private Long getIdAtleta(String mail) {
        List<Atleta> list = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.mail = :mail", Atleta.class,
                Map.of("mail", mail));
        return list.isEmpty() ? null : list.get(0).getId();
    }

    // Helper to run code inside a transaction with a managed EntityManager
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

    private Long getConteggioAtletiPerAllenatore() {
        return gp.eseguiQuery(
                "SELECT COUNT(a) FROM Allenatore al JOIN al.atleti a WHERE al.id = :id",
                Long.class, Map.of("id", idAllenatore)).get(0);
    }

    // ---------- Constructors ----------
    @Test
    void testCostruttoriEGetters() {
        Allenatore a = new Allenatore();
        assertNull(a.getNome());

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

    // ---------- addAtleta and getAtleti ----------
    @Test
    void testAddAtletaEGetAtleti() {
        assertEquals(0L, getConteggioAtletiPerAllenatore());

        // Add atleta1
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            Atleta atleta = em.find(Atleta.class, idAtleta1);
            managed.addAtleta(atleta);
            // No need for explicit merge because the entity is managed
        });
        assertEquals(1L, getConteggioAtletiPerAllenatore());

        // Add atleta2
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            Atleta atleta = em.find(Atleta.class, idAtleta2);
            managed.addAtleta(atleta);
        });
        assertEquals(2L, getConteggioAtletiPerAllenatore());
    }

    // ---------- removeAtleta ----------
    @Test
    void testRemoveAtleta() {
        // Add both atleti
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            managed.addAtleta(em.find(Atleta.class, idAtleta1));
            managed.addAtleta(em.find(Atleta.class, idAtleta2));
        });
        assertEquals(2L, getConteggioAtletiPerAllenatore());

        // Remove atleta1
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            managed.removeAtleta(em.find(Atleta.class, idAtleta1));
        });
        assertEquals(1L, getConteggioAtletiPerAllenatore());
    }

    // ---------- getAtleta with valid id ----------
    @Test
    void testGetAtleta_ValidId() {
        // Add association
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            managed.addAtleta(em.find(Atleta.class, idAtleta1));
        });

        Atleta found = eseguiInTransazioneConRisultato(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            return managed.getAtleta(idAtleta1);
        });

        assertNotNull(found);
        assertEquals(TEST_MAIL_ATLETA1, found.getMail());
    }

    // ---------- getAtleta with invalid id ----------
    @Test
    void testGetAtleta_InvalidId_ThrowsException() {
        // Add association
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            managed.addAtleta(em.find(Atleta.class, idAtleta1));
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            eseguiInTransazioneConRisultato(em -> {
                Allenatore managed = em.find(Allenatore.class, idAllenatore);
                return managed.getAtleta(999999L);
            });
        });
    }

    // ---------- getSessioni ----------
    @Test
    void testGetSessioni() {
        // Create a session
        eseguiInTransazione(em -> {
            Allenatore managedAllenatore = em.find(Allenatore.class, idAllenatore);
            Atleta managedAtleta = em.find(Atleta.class, idAtleta1);
            SessioneDiAllenamento sessione = new SessioneDiAllenamento(
                    "Test sessione", "Descrizione", LocalDate.now(), managedAtleta, managedAllenatore);
            em.persist(sessione);
        });

        Long count = gp.eseguiQuery(
                "SELECT COUNT(s) FROM SessioneDiAllenamento s WHERE s.allenatore.id = :id",
                Long.class, Map.of("id", idAllenatore)).get(0);
        assertEquals(1L, count);
    }

    // ---------- removeAtleta when atleta not present ----------
    @Test
    void testRemoveAtleta_AtletaNotPresent_NoSideEffects() {
        assertEquals(0L, getConteggioAtletiPerAllenatore());

        // Try to remove a non-associated atleta
        eseguiInTransazione(em -> {
            Allenatore managed = em.find(Allenatore.class, idAllenatore);
            Atleta atleta2 = em.find(Atleta.class, idAtleta2);
            managed.removeAtleta(atleta2); // Should do nothing
        });

        assertEquals(0L, getConteggioAtletiPerAllenatore());
    }

    // ---------- getAtleti (just check not null) ----------
    @Test
    void testGetAtleti_ReturnsNotNull() {
        Allenatore a = gp.eseguiQuery("SELECT a FROM Allenatore a WHERE a.id = :id", Allenatore.class,
                Map.of("id", idAllenatore)).get(0);
        assertNotNull(a.getAtleti());
    }
}