package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AtletaTest {

    private GestorePersistenza gp;

    private static final String TEST_MAIL_ATLETA = "test.atleta@example.com";
    private static final String TEST_MAIL_ALLENATORE = "test.all@example.com";

    private Long idAtleta;
    private Long idAllenatore;

    @BeforeEach
    void setUp() {
        gp = new GestorePersistenza();
        pulisciDatabase();
        creaDatiDiProva();
        idAtleta = getIdAtleta();
        idAllenatore = getIdAllenatore();
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
    }

    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // Delete from join tables
            em.createNativeQuery("DELETE FROM allenatore_atleta WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mail)")
                    .setParameter("mail", TEST_MAIL_ATLETA).executeUpdate();

            em.createNativeQuery("DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mail))")
                    .setParameter("mail", TEST_MAIL_ATLETA).executeUpdate();

            em.createNativeQuery("DELETE FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mail)")
                    .setParameter("mail", TEST_MAIL_ATLETA).executeUpdate();

            em.createNativeQuery("DELETE FROM atleta_obiettivo WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = :mail)")
                    .setParameter("mail", TEST_MAIL_ATLETA).executeUpdate();

            em.createNativeQuery("DELETE FROM atleti WHERE mail = :mail")
                    .setParameter("mail", TEST_MAIL_ATLETA).executeUpdate();

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
            Atleta a = new Atleta("Mario", "Rossi", TEST_MAIL_ATLETA, "pass123", "Corsa", 3,
                    new HashSet<>(Arrays.asList("Migliorare tempo", "Aumentare resistenza")));
            Allenatore al = new Allenatore("Anna", "Verdi", TEST_MAIL_ALLENATORE, "pass456", "Atletica");
            em.persist(a);
            em.persist(al);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private Long getIdAtleta() {
        List<Atleta> list = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.mail = :mail", Atleta.class,
                Map.of("mail", TEST_MAIL_ATLETA));
        return list.isEmpty() ? null : list.get(0).getId();
    }

    private Long getIdAllenatore() {
        List<Allenatore> list = gp.eseguiQuery("SELECT a FROM Allenatore a WHERE a.mail = :mail", Allenatore.class,
                Map.of("mail", TEST_MAIL_ALLENATORE));
        return list.isEmpty() ? null : list.get(0).getId();
    }

    // Transaction helpers
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

    // ---------- Constructors ----------
    @Test
    void testCostruttoriEGettersBase() {
        Atleta a = new Atleta();
        assertNull(a.getNome());
        assertNull(a.getDisciplina());
        assertEquals(0, a.getLivello());
        assertNull(a.getObiettivo());

        Atleta a2 = new Atleta("Luca", "Bianchi", "luca@test.it", "pwd", "Nuoto", 2);
        assertEquals("Luca", a2.getNome());
        assertEquals("Bianchi", a2.getCognome());
        assertEquals("luca@test.it", a2.getMail());
        assertEquals("pwd", a2.getPassword());
        assertEquals("Nuoto", a2.getDisciplina());
        assertEquals(2, a2.getLivello());
        assertNull(a2.getObiettivo());

        Set<String> obiettivi = new HashSet<>(Arrays.asList("Obb1", "Obb2"));
        Atleta a3 = new Atleta("Giulia", "Neri", "giulia@test.it", "pwd", "Ciclismo", 1, obiettivi);
        assertEquals("Ciclismo", a3.getDisciplina());
        assertEquals(1, a3.getLivello());
        assertEquals(obiettivi, a3.getObiettivo());
    }

    // ---------- Getters and Setters ----------
    @Test
    void testSetDisciplina() {
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            managed.setDisciplina("Pallavolo");
        });
        Atleta reloaded = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.id = :id", Atleta.class,
                Map.of("id", idAtleta)).get(0);
        assertEquals("Pallavolo", reloaded.getDisciplina());
    }

    @Test
    void testSetLivello() {
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            managed.setLivello(5);
        });
        Atleta reloaded = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.id = :id", Atleta.class,
                Map.of("id", idAtleta)).get(0);
        assertEquals(5, reloaded.getLivello());
    }

    @Test
    void testSetObiettivo() {
        Set<String> nuoviObiettivi = new HashSet<>(Arrays.asList("Nuovo obbiettivo 1", "Nuovo obbiettivo 2"));
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            managed.setObiettivo(nuoviObiettivi);
        });

        // Verify using a JPQL query that fetches the collection inside a transaction
        Set<String> obiettiviFromDb = eseguiInTransazioneConRisultato(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            // Force initialization of the lazy collection within the transaction
            Set<String> ob = managed.getObiettivo();
            ob.size(); // triggers loading
            return new HashSet<>(ob);
        });
        assertEquals(nuoviObiettivi, obiettiviFromDb);
    }

    // ---------- getAllenatori and addAllenatore ----------
    @Test
    void testAddAllenatoreEGetAllenatori() {
        Long countBefore = getConteggioAllenatoriPerAtleta();
        assertEquals(0L, countBefore);

        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, idAtleta);
            Allenatore managedAllenatore = em.find(Allenatore.class, idAllenatore);
            managedAtleta.addAllenatore(managedAllenatore);
            managedAllenatore.addAtleta(managedAtleta);
        });

        Long countAfter = getConteggioAllenatoriPerAtleta();
        assertEquals(1L, countAfter);

        Long allenatoreHasAtleta = gp.eseguiQuery(
                "SELECT COUNT(a) FROM Allenatore al JOIN al.atleti a WHERE al.id = :idAllenatore AND a.id = :idAtleta",
                Long.class, Map.of("idAllenatore", idAllenatore, "idAtleta", idAtleta)).get(0);
        assertEquals(1L, allenatoreHasAtleta);
    }

    private Long getConteggioAllenatoriPerAtleta() {
        return gp.eseguiQuery(
                "SELECT COUNT(al) FROM Atleta a JOIN a.allenatori al WHERE a.id = :id",
                Long.class, Map.of("id", idAtleta)).get(0);
    }

    // ---------- getSessioni ----------
    @Test
    void testGetSessioni() {
        Long countBefore = getConteggioSessioniPerAtleta();
        assertEquals(0L, countBefore);

        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, idAtleta);
            Allenatore managedAllenatore = em.find(Allenatore.class, idAllenatore);
            SessioneDiAllenamento sessione = new SessioneDiAllenamento(
                    "Test sessione", "Descrizione", LocalDate.now(), managedAtleta, managedAllenatore);
            em.persist(sessione);
        });

        Long countAfter = getConteggioSessioniPerAtleta();
        assertEquals(1L, countAfter);
    }

    private Long getConteggioSessioniPerAtleta() {
        return gp.eseguiQuery(
                "SELECT COUNT(s) FROM SessioneDiAllenamento s WHERE s.atleta.id = :id",
                Long.class, Map.of("id", idAtleta)).get(0);
    }

    @Test
    void testAddAllenatore_Duplicate_NoEffect() {
        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, idAtleta);
            Allenatore managedAllenatore = em.find(Allenatore.class, idAllenatore);
            managedAtleta.addAllenatore(managedAllenatore);
            managedAllenatore.addAtleta(managedAtleta);
        });
        assertEquals(1L, getConteggioAllenatoriPerAtleta());

        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, idAtleta);
            Allenatore managedAllenatore = em.find(Allenatore.class, idAllenatore);
            managedAtleta.addAllenatore(managedAllenatore);
        });
        assertEquals(1L, getConteggioAllenatoriPerAtleta());
    }

    @Test
    void testGetAllenatori_ReturnsSet() {
        Atleta a = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.id = :id", Atleta.class,
                Map.of("id", idAtleta)).get(0);
        assertNotNull(a.getAllenatori());
    }

    @Test
    void testGetSessioni_ReturnsSet() {
        Atleta a = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.id = :id", Atleta.class,
                Map.of("id", idAtleta)).get(0);
        assertNotNull(a.getSessioni());
    }

    // ---------- Obiettivi tests with lazy loading fixed ----------
    @Test
    void testObiettivi_SetBehavior() {
        Set<String> obiettivi = new HashSet<>(Arrays.asList("A", "B", "C"));
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            managed.setObiettivo(obiettivi);
        });

        // Verify inside a transaction to initialize the lazy collection
        Set<String> retrieved = eseguiInTransazioneConRisultato(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            Set<String> ob = managed.getObiettivo();
            ob.size(); // force initialization
            return new HashSet<>(ob);
        });
        assertEquals(3, retrieved.size());
        assertTrue(retrieved.contains("A"));
        assertTrue(retrieved.contains("B"));
        assertTrue(retrieved.contains("C"));
    }

    // Also test that the initial obiettivi from creaDatiDiProva are present
    @Test
    void testInitialObiettivi() {
        Set<String> initial = eseguiInTransazioneConRisultato(em -> {
            Atleta managed = em.find(Atleta.class, idAtleta);
            Set<String> ob = managed.getObiettivo();
            ob.size();
            return new HashSet<>(ob);
        });
        assertEquals(2, initial.size());
        assertTrue(initial.contains("Migliorare tempo"));
        assertTrue(initial.contains("Aumentare resistenza"));
    }
}