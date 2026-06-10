package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;


import static org.junit.jupiter.api.Assertions.*;

class GestoreUtentiTest {

    private GestoreUtenti gu;

    private static final String TEST_MAIL_ATLETA = "gu.atleta@test.com";
    private static final String TEST_MAIL_ALTRO_ATLETA = "gu.altro@test.com";
    private static final String TEST_MAIL_ALLENATORE = "gu.allenatore@test.com";

    private Allenatore a1;
    private Atleta at1;

    @BeforeEach
    void setUp() {
        gu = GestoreUtenti.getInstance();
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
            em.createNativeQuery("DELETE FROM allenatore_atleta WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2))")
                    .setParameter("mail1", TEST_MAIL_ATLETA)
                    .setParameter("mail2", TEST_MAIL_ALTRO_ATLETA).executeUpdate();
            em.createNativeQuery("DELETE FROM sessione_esercizio WHERE sessione_id IN (SELECT id FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2)))")
                    .setParameter("mail1", TEST_MAIL_ATLETA)
                    .setParameter("mail2", TEST_MAIL_ALTRO_ATLETA).executeUpdate();
            em.createNativeQuery("DELETE FROM sessioni WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2))")
                    .setParameter("mail1", TEST_MAIL_ATLETA)
                    .setParameter("mail2", TEST_MAIL_ALTRO_ATLETA).executeUpdate();
            em.createNativeQuery("DELETE FROM atleta_obiettivo WHERE atleta_id IN (SELECT id FROM atleti WHERE mail IN (:mail1, :mail2))")
                    .setParameter("mail1", TEST_MAIL_ATLETA)
                    .setParameter("mail2", TEST_MAIL_ALTRO_ATLETA).executeUpdate();
            em.createNativeQuery("DELETE FROM atleti WHERE mail IN (:mail1, :mail2)")
                    .setParameter("mail1", TEST_MAIL_ATLETA)
                    .setParameter("mail2", TEST_MAIL_ALTRO_ATLETA).executeUpdate();
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
         at1 = GestorePersistenza.salva(new Atleta("Mario", "Rossi", TEST_MAIL_ATLETA, "pass1", "Corsa", 3,
                new HashSet<>(Arrays.asList("Obiettivo1", "Obiettivo2"))));
         a1 = GestorePersistenza.salva(new Allenatore("Anna", "Bianchi", TEST_MAIL_ALLENATORE, "pass3", "Atletica"));
    }

    // ------------------------- cercaAtleta -------------------------
    @Test
    void testCercaAtleta_ValidId_ReturnsAtleta() throws ResourceNotFoundException {
        Atleta a = gu.cercaAtleta(at1.getId());
        assertNotNull(a);
        assertEquals(TEST_MAIL_ATLETA, a.getMail());
    }

    @Test
    void testCercaAtleta_InvalidId_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> gu.cercaAtleta(999999L));
    }

    // ------------------------- cercaAllenatore -------------------------
    @Test
    void testCercaAllenatore_ValidId_ReturnsAllenatore() throws ResourceNotFoundException {
        Allenatore al = gu.cercaAllenatore(a1.getId());
        assertNotNull(al);
        assertEquals(TEST_MAIL_ALLENATORE, al.getMail());
    }

    @Test
    void testCercaAllenatore_InvalidId_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> gu.cercaAllenatore(999999L));
    }


}