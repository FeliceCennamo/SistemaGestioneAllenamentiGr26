package database;

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private GestorePersistenza gestorePersistenza;

    @BeforeEach
    public void setup() {
        gestorePersistenza = new GestorePersistenza();
        cleanUpTestData();  // ensure a clean state before each test
    }

    @AfterEach
    public void tearDown() {
        cleanUpTestData();  // remove test data after each test
    }

    private void cleanUpTestData() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // --- Pulizia atleta (invariata) ---
            em.createNativeQuery(
                    "DELETE FROM atleta_obiettivo WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = 'luca.bianchi@test.com')"
            ).executeUpdate();
            em.createNativeQuery(
                    "DELETE FROM allenatore_atleta WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = 'luca.bianchi@test.com')"
            ).executeUpdate();
            em.createNativeQuery(
                    "DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = 'luca.bianchi@test.com'))"
            ).executeUpdate();
            em.createNativeQuery(
                    "DELETE FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail = 'luca.bianchi@test.com')"
            ).executeUpdate();
            em.createNativeQuery(
                    "DELETE FROM atleti WHERE mail = 'luca.bianchi@test.com'"
            ).executeUpdate();

            // --- Pulizia risultati: rispettiamo l'ordine delle dipendenze ---
            // 1. Rimuovere i collegamenti in sessione_esercizio verso gli esercizi target
            em.createNativeQuery(
                    "DELETE FROM sessione_esercizio WHERE esercizio_id IN (" +
                            "SELECT id FROM esercizi WHERE risultato IN (" +
                            "SELECT id FROM risultati WHERE " +
                            "nota IN ('NotaTest85693','testTutti1','testTutti2','queryParam','perOttieniTutti') " +
                            "OR (nota IS NULL AND (ripetizioni IS NULL OR tempo IS NULL))))"
            ).executeUpdate();

            // 2. Eliminare gli esercizi che puntano ai risultati da cancellare
            em.createNativeQuery(
                    "DELETE FROM esercizi WHERE risultato IN (" +
                            "SELECT id FROM risultati WHERE " +
                            "nota IN ('NotaTest85693','testTutti1','testTutti2','queryParam','perOttieniTutti') " +
                            "OR (nota IS NULL AND (ripetizioni IS NULL OR tempo IS NULL)))"
            ).executeUpdate();

            // 3. Infine cancellare i risultati
            em.createNativeQuery(
                    "DELETE FROM risultati WHERE " +
                            "nota IN ('NotaTest85693','testTutti1','testTutti2','queryParam','perOttieniTutti') " +
                            "OR (nota IS NULL AND (ripetizioni IS NULL OR tempo IS NULL))"
            ).executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            // Eventualmente logga l'errore
        } finally {
            em.close();
        }
    }

    @Test
    public void testSalvaRisultato() {
        Risultato risultato_rip = new RisultatoRipetizioni("NotaTest85693", 10);
        Risultato risultato_tempo = new RisultatoTempo("NotaTest85693", Duration.ofMinutes(10));

        gestorePersistenza.salva(risultato_rip);
        gestorePersistenza.salva(risultato_tempo);

        List<Risultato> find_tempo = gestorePersistenza.eseguiQuery("SELECT r FROM Risultato r WHERE r.nota = :nota AND TYPE(r) = RisultatoTempo", Risultato.class, Map.of("nota", "NotaTest85693"));
        List<Risultato> find_ripetizioni = gestorePersistenza.eseguiQuery("SELECT r FROM Risultato r WHERE r.nota = :nota AND TYPE(r) = RisultatoRipetizioni", Risultato.class, Map.of("nota", "NotaTest85693"));

        assertNotNull(find_tempo);
        assertNotNull(find_tempo.getFirst());
        assertEquals("NotaTest85693", find_tempo.getFirst().getNota());
        assertEquals(Duration.ofMinutes(10), find_tempo.getFirst().getRisultato());

        assertNotNull(find_ripetizioni);
        assertNotNull(find_ripetizioni.getFirst());
        assertEquals("NotaTest85693", find_ripetizioni.getFirst().getNota());
        assertEquals(10, find_ripetizioni.getFirst().getRisultato());
    }


    @Test
    public void testSalvaRisultatoNull() {
        Risultato risultato_rip = new RisultatoRipetizioni(null, null);
        Risultato risultato_tempo = new RisultatoTempo(null, null);

        gestorePersistenza.salva(risultato_rip);
        gestorePersistenza.salva(risultato_tempo);

        List<Risultato> find_tempo = gestorePersistenza.eseguiQuery("SELECT r FROM Risultato r WHERE r.nota IS NULL AND TYPE(r) = RisultatoTempo", Risultato.class, Map.of());
        List<Risultato> find_ripetizioni = gestorePersistenza.eseguiQuery("SELECT r FROM Risultato r WHERE r.nota IS NULL AND TYPE(r) = RisultatoRipetizioni", Risultato.class, Map.of());

        assertNotNull(find_tempo);
        assertNotNull(find_tempo.getFirst());
        assertNull(find_tempo.getFirst().getNota());
        assertNull(find_tempo.getFirst().getRisultato());

        assertNotNull(find_ripetizioni);
        assertNotNull(find_ripetizioni.getFirst());
        assertNull(find_ripetizioni.getFirst().getNota());
        assertNull(find_ripetizioni.getFirst().getRisultato());
    }

    @Test
    public void testSalvaTutti() {
        Risultato r1 = new RisultatoRipetizioni("testTutti1", 5);
        Risultato r2 = new RisultatoTempo("testTutti2", Duration.ofSeconds(30));
        gestorePersistenza.salvaTutti(r1, r2);

        List<Risultato> risultati = gestorePersistenza.ottieniTutti(Risultato.class);
        assertTrue(risultati.stream().anyMatch(r -> "testTutti1".equals(r.getNota())));
        assertTrue(risultati.stream().anyMatch(r -> "testTutti2".equals(r.getNota())));
    }

    @Test
    public void testTrovaPerId_Exists() {
        Atleta atleta = new Atleta("Luca", "Bianchi", "luca.bianchi@test.com", "password456", "Calcio", 5);
        gestorePersistenza.salva(atleta);

        List<Atleta> find = gestorePersistenza.eseguiQuery("SELECT a FROM Atleta a WHERE a.mail = :mail AND a.password = :password", Atleta.class, Map.of("mail", "luca.bianchi@test.com", "password", "password456"));

        Long id = find.getFirst().getId();
        Atleta trovato = gestorePersistenza.trovaPerId(Atleta.class, id);
        assertNotNull(trovato);
        assertEquals("Luca", trovato.getNome());
        assertEquals("Bianchi", trovato.getCognome());
        assertEquals("Calcio", trovato.getDisciplina());
        assertEquals(5, trovato.getLivello());
    }

    @Test
    public void testTrovaPerId_NotExists() {
        Risultato trovato = gestorePersistenza.trovaPerId(Risultato.class, 999999L);
        assertNull(trovato);
    }

    @Test
    public void testOttieniTutti() {
        // Clean up previous data? Not needed; just ensure at least some exist
        Risultato r = new RisultatoRipetizioni("perOttieniTutti", 3);
        gestorePersistenza.salva(r);

        List<Risultato> tutti = gestorePersistenza.ottieniTutti(Risultato.class);
        assertNotNull(tutti);
        assertFalse(tutti.isEmpty());
        assertTrue(tutti.stream().anyMatch(ris -> "perOttieniTutti".equals(ris.getNota())));
    }

    @Test
    public void testEseguiQuery_WithParameters() {
        RisultatoRipetizioni r1 = new RisultatoRipetizioni("queryParam", 7);
        RisultatoRipetizioni r2 = new RisultatoRipetizioni("queryParam", 8);
        gestorePersistenza.salvaTutti(r1, r2);

        Map<String, Object> params = new HashMap<>();
        params.put("nota", "queryParam");
        params.put("minRip", 7);

        List<RisultatoRipetizioni> result = gestorePersistenza.eseguiQuery(
                "SELECT r FROM RisultatoRipetizioni r WHERE r.nota = :nota AND r.ripetizioni >= :minRip",
                RisultatoRipetizioni.class, params);

        assertEquals(2, result.size());
    }

    @Test
    public void testEseguiQuery_NoParameters() {
        List<Risultato> result = gestorePersistenza.eseguiQuery(
                "SELECT r FROM Risultato r",
                Risultato.class, Collections.emptyMap());
        assertNotNull(result);
    }

    @Test
    public void testGetFiglie() {
        Set<EntityType<?>> figlie = gestorePersistenza.getFiglie(Allenatore.class);
        assertNotNull(figlie);
        assertTrue(figlie.isEmpty());

        Set<EntityType<?>> figlieUtente = gestorePersistenza.getFiglie(entity.Utente.class);
        assertNotNull(figlieUtente);
    }

    @Test
    public void testSalva_ThrowsRuntimeExceptionWhenNullArgument() {
        // Passing null to salva should cause IllegalArgumentException (a RuntimeException)
        assertThrows(RuntimeException.class, () -> {
            gestorePersistenza.salva(null);
        });
    }

    @Test
    public void testSalvaTutti_ThrowsRuntimeExceptionWhenNullElementInVarargs() {
        // Create a valid entity
        Risultato validRisultato = new RisultatoRipetizioni("validNote", 10);

        // Passing a null as one of the arguments should cause RuntimeException
        assertThrows(RuntimeException.class, () -> {
            gestorePersistenza.salvaTutti(validRisultato, null);
        });
    }
}

