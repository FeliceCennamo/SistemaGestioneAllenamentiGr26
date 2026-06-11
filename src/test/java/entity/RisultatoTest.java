package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RisultatoTest {


    private long startId;

    @BeforeEach
    void setUp() {
        pulisciDatabase(); // eventuale pulizia precedente (opzionale)
        // Registra l'ID massimo attuale prima del test
        startId = GestorePersistenza.eseguiQuery("SELECT COALESCE(MAX(id), 0) FROM Risultato", Long.class, Map.of()).get(0);
        // ... resto del setup
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
    }

    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            // 1. Elimina i riferimenti nella tabella di join
            em.createNativeQuery(
                    "DELETE se FROM sessione_esercizio se " +
                            "WHERE se.esercizio_id IN (SELECT id FROM esercizi WHERE nome LIKE 'TestEs%')"
            ).executeUpdate();
            // 2. Elimina gli esercizi di test
            em.createNativeQuery("DELETE FROM esercizi WHERE nome LIKE 'TestEs%'").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Errore durante la pulizia del database", e);
        } finally {
            em.close();
        }
    }

    // ---------- Tests for RisultatoRipetizioni ----------
    @Test
    void testRisultatoRipetizioni_ConstructorsAndGetters() {
        // Default constructor
        RisultatoRipetizioni r1 = new RisultatoRipetizioni();
        assertNull(r1.getNota());
        assertNull(r1.getRisultato());

        // Constructor with parameters
        RisultatoRipetizioni r2 = new RisultatoRipetizioni("Nota test", 10);
        assertEquals("Nota test", r2.getNota());
        assertEquals(10, r2.getRisultato());
    }

    @Test
    void testRisultatoRipetizioni_SetRisultato_Valid() {
        RisultatoRipetizioni r = new RisultatoRipetizioni("Nota", 5);
        r.setRisultato(20);
        assertEquals(20, r.getRisultato());
    }

    @Test
    void testRisultatoRipetizioni_SetRisultato_InvalidType_ThrowsClassCastException() {
        RisultatoRipetizioni r = new RisultatoRipetizioni();
        assertThrows(ClassCastException.class, () -> r.setRisultato("not an integer"));
        assertThrows(ClassCastException.class, () -> r.setRisultato(Duration.ofMinutes(5)));
    }

    @Test
    void testRisultatoRipetizioni_SetNota() {
        RisultatoRipetizioni r = new RisultatoRipetizioni();
        r.setNota("Nuova nota");
        assertEquals("Nuova nota", r.getNota());
    }

    @Test
    void testRisultatoRipetizioni_Persistence() {
        RisultatoRipetizioni originale = new RisultatoRipetizioni("Persist test", 100);
        RisultatoRipetizioni salvato = GestorePersistenza.salva(originale);
        assertNotNull(salvato.getId());

        RisultatoRipetizioni trovato = GestorePersistenza.trovaPerId(RisultatoRipetizioni.class, salvato.getId());
        assertNotNull(trovato);
        assertEquals("Persist test", trovato.getNota());
        assertEquals(100, trovato.getRisultato());
    }

    @Test
    void testRisultatoRipetizioni_UpdateNota() {
        RisultatoRipetizioni r = GestorePersistenza.salva(new RisultatoRipetizioni("Vecchia nota", 50));
        r.setNota("Nota aggiornata");
        GestorePersistenza.salva(r);

        RisultatoRipetizioni reloaded = GestorePersistenza.trovaPerId(RisultatoRipetizioni.class, r.getId());
        assertEquals("Nota aggiornata", reloaded.getNota());
    }

    @Test
    void testRisultatoRipetizioni_UpdateRisultato() {
        RisultatoRipetizioni r = GestorePersistenza.salva(new RisultatoRipetizioni("Nota", 10));
        r.setRisultato(99);
        GestorePersistenza.salva(r);

        RisultatoRipetizioni reloaded = GestorePersistenza.trovaPerId(RisultatoRipetizioni.class, r.getId());
        assertEquals(99, reloaded.getRisultato());
    }

    // ---------- Tests for RisultatoTempo ----------
    @Test
    void testRisultatoTempo_ConstructorsAndGetters() {
        // Default constructor
        RisultatoTempo r1 = new RisultatoTempo();
        assertNull(r1.getNota());
        assertNull(r1.getRisultato());

        // Constructor with parameters
        Duration durata = Duration.ofMinutes(30);
        RisultatoTempo r2 = new RisultatoTempo("Nota tempo", durata);
        assertEquals("Nota tempo", r2.getNota());
        assertEquals(durata, r2.getRisultato());
    }

    @Test
    void testRisultatoTempo_SetRisultato_Valid() {
        RisultatoTempo r = new RisultatoTempo("Nota", Duration.ofSeconds(45));
        Duration nuovaDurata = Duration.ofHours(1);
        r.setRisultato(nuovaDurata);
        assertEquals(nuovaDurata, r.getRisultato());
    }

    @Test
    void testRisultatoTempo_SetRisultato_InvalidType_ThrowsIllegalArgumentException() {
        RisultatoTempo r = new RisultatoTempo();
        assertThrows(ClassCastException.class, () -> r.setRisultato("not a duration"));
        assertThrows(ClassCastException.class, () -> r.setRisultato(123));
    }

    @Test
    void testRisultatoTempo_SetNota() {
        RisultatoTempo r = new RisultatoTempo();
        r.setNota("Nuova nota tempo");
        assertEquals("Nuova nota tempo", r.getNota());
    }

    @Test
    void testRisultatoTempo_Persistence() {
        Duration durata = Duration.ofMinutes(15);
        RisultatoTempo originale = new RisultatoTempo("Persist tempo", durata);
        RisultatoTempo salvato = GestorePersistenza.salva(originale);
        assertNotNull(salvato.getId());

        RisultatoTempo trovato = GestorePersistenza.trovaPerId(RisultatoTempo.class, salvato.getId());
        assertNotNull(trovato);
        assertEquals("Persist tempo", trovato.getNota());
        assertEquals(durata, trovato.getRisultato());
    }

    @Test
    void testRisultatoTempo_UpdateNota() {
        RisultatoTempo r = GestorePersistenza.salva(new RisultatoTempo("Vecchia", Duration.ofSeconds(10)));
        r.setNota("Aggiornata");
        GestorePersistenza.salva(r);

        RisultatoTempo reloaded = GestorePersistenza.trovaPerId(RisultatoTempo.class, r.getId());
        assertEquals("Aggiornata", reloaded.getNota());
    }

    @Test
    void testRisultatoTempo_UpdateRisultato() {
        RisultatoTempo r = GestorePersistenza.salva(new RisultatoTempo("Nota", Duration.ofMinutes(5)));
        Duration nuova = Duration.ofDays(2);
        r.setRisultato(nuova);
        GestorePersistenza.salva(r);

        RisultatoTempo reloaded = GestorePersistenza.trovaPerId(RisultatoTempo.class, r.getId());
        assertEquals(nuova, reloaded.getRisultato());
    }

    // ---------- Polymorphic queries ----------
    @Test
    void testPolymorphicQuery_AllRisultati() {
        RisultatoRipetizioni rip = GestorePersistenza.salva(new RisultatoRipetizioni("Rip", 10));
        RisultatoTempo tempo = GestorePersistenza.salva(new RisultatoTempo("Tempo", Duration.ofMinutes(20)));

        List<Risultato> tutti = GestorePersistenza.eseguiQuery("SELECT r FROM Risultato r", Risultato.class, Map.of());
        assertFalse(tutti.isEmpty());

        List<Risultato> soloRipetizioni = GestorePersistenza.eseguiQuery(
                "SELECT r FROM Risultato r WHERE TYPE(r) = RisultatoRipetizioni",
                Risultato.class, Map.of());
        assertFalse(soloRipetizioni.isEmpty());
        assertTrue(soloRipetizioni.stream().anyMatch(r -> r.getId().equals(rip.getId())));
        List<Risultato> soloTempo = GestorePersistenza.eseguiQuery(
                "SELECT r FROM Risultato r WHERE TYPE(r) = RisultatoTempo",
                Risultato.class, Map.of());
        assertFalse(soloTempo.isEmpty());
        assertTrue(soloTempo.stream().anyMatch(r -> r.getId().equals(tempo.getId())));
    }

    // ---------- Null values ----------
    @Test
    void testRisultatoRipetizioni_NullValues() {
        RisultatoRipetizioni r = new RisultatoRipetizioni(null, null);
        RisultatoRipetizioni saved = GestorePersistenza.salva(r);
        assertNotNull(saved.getId());

        RisultatoRipetizioni found = GestorePersistenza.trovaPerId(RisultatoRipetizioni.class, saved.getId());
        assertNull(found.getNota());
        assertNull(found.getRisultato());
    }

    @Test
    void testRisultatoTempo_NullValues() {
        RisultatoTempo r = new RisultatoTempo(null, null);
        RisultatoTempo saved = GestorePersistenza.salva(r);
        assertNotNull(saved.getId());

        RisultatoTempo found = GestorePersistenza.trovaPerId(RisultatoTempo.class, saved.getId());
        assertNull(found.getNota());
        assertNull(found.getRisultato());
    }

    // ---------- Edge: setRisultato with null (allowed) ----------
    @Test
    void testRisultatoRipetizioni_SetRisultato_Null() {
        RisultatoRipetizioni r = new RisultatoRipetizioni("Nota", 5);
        r.setRisultato(null);
        assertNull(r.getRisultato());
    }

    @Test
    void testRisultatoTempo_SetRisultato_Null() {
        RisultatoTempo r = new RisultatoTempo("Nota", Duration.ofMinutes(1));
        r.setRisultato(null);
        assertNull(r.getRisultato());
    }
}