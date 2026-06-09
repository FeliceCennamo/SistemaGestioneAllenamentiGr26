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

    private Atleta atleta;
    private Allenatore allenatore;

    @BeforeEach
    void setUp() {
        gp = new GestorePersistenza();
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
        atleta = gp.salva(new Atleta("Mario", "Rossi", TEST_MAIL_ATLETA, "pass123", "Corsa", 3,
                new HashSet<>(Arrays.asList("Migliorare tempo", "Aumentare resistenza"))));
        allenatore = gp.salva(new Allenatore("Anna", "Verdi", TEST_MAIL_ALLENATORE, "pass456", "Atletica"));
    }

    private Long getConteggioAllenatoriPerAtleta() {
        return gp.eseguiQuery(
                "SELECT COUNT(al) FROM Atleta a JOIN a.allenatori al WHERE a.id = :id",
                Long.class, Map.of("id", atleta.getId())).get(0);
    }

    private Long getConteggioSessioniPerAtleta() {
        return gp.eseguiQuery(
                "SELECT COUNT(s) FROM SessioneDiAllenamento s WHERE s.atleta.id = :id",
                Long.class, Map.of("id", atleta.getId())).get(0);
    }

    // ---------- Costruttori ----------
    @Test
    void testCostruttoriEGettersBase() {
        Atleta a = new Atleta();
        assertNull(a.getNome());
        assertNull(a.getDisciplina());
        assertEquals(0, a.getLivello());
        assertNull(a.getObiettivo());
        assertTrue(a.getAllenatori().isEmpty());

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

    // ---------- Setter ----------
    @Test
    void testSetDisciplina() {
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            managed.setDisciplina("Pallavolo");
        });
        Atleta reloaded = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.id = :id", Atleta.class,
                Map.of("id", atleta.getId())).get(0);
        assertEquals("Pallavolo", reloaded.getDisciplina());
    }

    @Test
    void testSetLivello() {
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            managed.setLivello(5);
        });
        Atleta reloaded = gp.eseguiQuery("SELECT a FROM Atleta a WHERE a.id = :id", Atleta.class,
                Map.of("id", atleta.getId())).get(0);
        assertEquals(5, reloaded.getLivello());
    }

    @Test
    void testSetObiettivo() {
        Set<String> nuoviObiettivi = new HashSet<>(Arrays.asList("Nuovo obbiettivo 1", "Nuovo obbiettivo 2"));
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            managed.setObiettivo(nuoviObiettivi);
        });

        Set<String> obiettiviFromDb = eseguiInTransazioneConRisultato(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            Set<String> ob = managed.getObiettivo();
            ob.size(); // forza inizializzazione
            return new HashSet<>(ob);
        });
        assertEquals(nuoviObiettivi, obiettiviFromDb);
    }

    // ---------- getAllenatori e addAllenatore ----------
    @Test
    void testAddAllenatoreEGetAllenatori() {
        assertEquals(0L, getConteggioAllenatoriPerAtleta());

        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, atleta.getId());
            Allenatore managedAllenatore = em.find(Allenatore.class, allenatore.getId());
            managedAtleta.addAllenatore(managedAllenatore);
            managedAllenatore.addAtleta(managedAtleta);
        });

        assertEquals(1L, getConteggioAllenatoriPerAtleta());

        Long allenatoreHasAtleta = gp.eseguiQuery(
                "SELECT COUNT(a) FROM Allenatore al JOIN al.atleti a WHERE al.id = :idAllenatore AND a.id = :idAtleta",
                Long.class, Map.of("idAllenatore", allenatore.getId(), "idAtleta", atleta.getId())).get(0);
        assertEquals(1L, allenatoreHasAtleta);
    }

    // ---------- getSessioni ----------
    @Test
    void testGetSessioni() {
        assertEquals(0L, getConteggioSessioniPerAtleta());

        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, atleta.getId());
            Allenatore managedAllenatore = em.find(Allenatore.class, allenatore.getId());
            SessioneDiAllenamento sessione = new SessioneDiAllenamento(
                    "Test sessione", "Descrizione", LocalDate.now(), managedAtleta, managedAllenatore);
            em.persist(sessione);
        });

        assertEquals(1L, getConteggioSessioniPerAtleta());
    }

    @Test
    void testAddAllenatore_Duplicate_NoEffect() {
        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, atleta.getId());
            Allenatore managedAllenatore = em.find(Allenatore.class, allenatore.getId());
            managedAtleta.addAllenatore(managedAllenatore);
            managedAllenatore.addAtleta(managedAtleta);
        });
        assertEquals(1L, getConteggioAllenatoriPerAtleta());

        eseguiInTransazione(em -> {
            Atleta managedAtleta = em.find(Atleta.class, atleta.getId());
            Allenatore managedAllenatore = em.find(Allenatore.class, allenatore.getId());
            managedAtleta.addAllenatore(managedAllenatore);
        });
        assertEquals(1L, getConteggioAllenatoriPerAtleta());
    }

    @Test
    void testGetAllenatori_ReturnsSet() {
        assertNotNull(atleta.getAllenatori());
    }

    @Test
    void testGetSessioni_ReturnsSet() {
        assertNotNull(atleta.getSessioni());
    }

    // ---------- Obiettivi ----------
    @Test
    void testObiettivi_SetBehavior() {
        Set<String> obiettivi = new HashSet<>(Arrays.asList("A", "B", "C"));
        eseguiInTransazione(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            managed.setObiettivo(obiettivi);
        });

        Set<String> retrieved = eseguiInTransazioneConRisultato(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            Set<String> ob = managed.getObiettivo();
            ob.size();
            return new HashSet<>(ob);
        });
        assertEquals(3, retrieved.size());
        assertTrue(retrieved.contains("A"));
        assertTrue(retrieved.contains("B"));
        assertTrue(retrieved.contains("C"));
    }

    @Test
    void testInitialObiettivi() {
        Set<String> initial = eseguiInTransazioneConRisultato(em -> {
            Atleta managed = em.find(Atleta.class, atleta.getId());
            Set<String> ob = managed.getObiettivo();
            ob.size();
            return new HashSet<>(ob);
        });
        assertEquals(2, initial.size());
        assertTrue(initial.contains("Migliorare tempo"));
        assertTrue(initial.contains("Aumentare resistenza"));
    }
}