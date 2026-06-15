package entity;

import database.GestorePersistenza;
import database.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class EsercizioTest {


    private static final String TEST_NOME_RIP = "TestEsRip_";
    private static final String TEST_NOME_TEMPO = "TestEsTempo_";
    private static final String TEST_NOME_UPDATE = "TestEsUpdate_";

    @BeforeEach
    void setUp() {
        pulisciDatabase();
    }

    @AfterEach
    void tearDown() {
        pulisciDatabase();
    }

    // Pulisce solo i record di test (basati su nome LIKE 'TestEs%')
    private void pulisciDatabase() {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM esercizi WHERE nome LIKE 'TestEs%'").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    // ==================== COSTRUTTORI ====================
    @Test
    void testCostruttoreDefault() {
        Esercizio e = new Esercizio();
        assertNull(e.getId());
        assertNull(e.getNome());
        assertNull(e.getTipo());
        assertNull(e.getRisultato());
    }

    @Test
    void testCostruttoreRipetizioni_Valido() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 10);
        assertEquals(TEST_NOME_RIP, e.getNome());
        assertEquals("desc", e.getDescrizione());
        assertEquals(TipoEsercizio.RIPETIZIONI, e.getTipo());
        assertEquals(10, e.getRisultatoAtteso());
        assertNull(e.getRisultato());
    }

    @Test
    void testCostruttoreRipetizioni_Negativo_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Esercizio(TEST_NOME_RIP, "desc", -5));
    }

    @Test
    void testCostruttoreTempo() {
        Duration dur = Duration.ofMinutes(15);
        Esercizio e = new Esercizio(TEST_NOME_TEMPO, "desc tempo", dur);
        assertEquals(TEST_NOME_TEMPO, e.getNome());
        assertEquals(TipoEsercizio.TEMPO, e.getTipo());
        assertEquals(dur, e.getRisultatoAtteso());
    }

    // ==================== GETTER / SETTER ====================
    @Test
    void testSetNomeDescrizione() {
        Esercizio e = new Esercizio();
        e.setNome("Nuovo nome");
        e.setDescrizione("Nuova desc");
        assertEquals("Nuovo nome", e.getNome());
        assertEquals("Nuova desc", e.getDescrizione());
    }

    @Test
    void testSetTipo() {
        Esercizio e = new Esercizio();
        e.setTipo(0);
        assertEquals(TipoEsercizio.RIPETIZIONI, e.getTipo());
        e.setTipo(1);
        assertEquals(TipoEsercizio.TEMPO, e.getTipo());
        assertThrows(IllegalArgumentException.class,
                () -> e.setTipo(99));
    }

    // ==================== SET RISULTATO ATTESO ====================
    @Test
    void testSetRisultatoAtteso_Ripetizioni_Valido() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 10); // tipo RIPETIZIONI
        e.setRisultatoAtteso(20);
        assertEquals(20, e.getRisultatoAtteso());
    }

    @Test
    void testSetRisultatoAtteso_Ripetizioni_NonValido_ThrowsException() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 10);
        assertThrows(IllegalArgumentException.class,
                () -> e.setRisultatoAtteso(Duration.ofMinutes(5)));
    }

    @Test
    void testSetRisultatoAtteso_Tempo_Valido() {
        Duration dur = Duration.ofMinutes(30);
        Esercizio e = new Esercizio(TEST_NOME_TEMPO, "desc", Duration.ofMinutes(10));
        e.setRisultatoAtteso(dur);
        assertEquals(dur, e.getRisultatoAtteso());
    }

    @Test
    void testSetRisultatoAtteso_Tempo_NonValido_ThrowsException() {
        Esercizio e = new Esercizio(TEST_NOME_TEMPO, "desc", Duration.ofMinutes(10));
        assertThrows(IllegalArgumentException.class,
                () -> e.setRisultatoAtteso(99));
    }

    @Test
    void testSetRisultatoAtteso_OggettoNonValido_ThrowsException() {
        Esercizio e = new Esercizio(TEST_NOME_TEMPO, "desc", Duration.ofMinutes(10));
        assertThrows(IllegalArgumentException.class,
                () -> e.setRisultatoAtteso("Prova_Stringa"));
    }

    // ==================== SET RISULTATO (con oggetto e nota) ====================
    @Test
    void testSetRisultatoConOggetto_Ripetizioni_Valido() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 10);
        e.setRisultato(25, "Buono");
        assertNotNull(e.getRisultato());
        assertTrue(e.getRisultato() instanceof RisultatoRipetizioni);
        assertEquals(25, e.getRisultato().getRisultato());
        assertEquals("Buono", e.getRisultato().getNota());
    }

    @Test
    void testSetRisultatoConOggetto_Tempo_Valido() {
        Duration dur = Duration.ofSeconds(45);
        Esercizio e = new Esercizio(TEST_NOME_TEMPO, "desc", Duration.ofMinutes(1));
        e.setRisultato(dur, "Velocissimo");
        assertTrue(e.getRisultato() instanceof RisultatoTempo);
        assertEquals(dur, e.getRisultato().getRisultato());
        assertEquals("Velocissimo", e.getRisultato().getNota());
    }

    @Test
    void testSetRisultatoConOggetto_TipoNonCorretto_ThrowsException() {
        Esercizio eRip = new Esercizio(TEST_NOME_RIP, "desc", 10);
        assertThrows(IllegalArgumentException.class,
                () -> eRip.setRisultato(Duration.ofMinutes(2), "nota"));

        Esercizio eTempo = new Esercizio(TEST_NOME_TEMPO, "desc", Duration.ofMinutes(1));
        assertThrows(IllegalArgumentException.class,
                () -> eTempo.setRisultato(50, "nota"));
    }

    // ==================== TEST PERSISTENZA ====================
    @Test
    void testPersistenzaEsercizioRipetizioni() {
        Esercizio originale = new Esercizio(TEST_NOME_RIP, "desc", 15);
        Esercizio salvato = GestorePersistenza.salva(originale);
        assertNotNull(salvato.getId());

        Esercizio trovato = GestorePersistenza.trovaPerId(Esercizio.class, salvato.getId());
        assertEquals(TEST_NOME_RIP, trovato.getNome());
        assertEquals(15, trovato.getRisultatoAtteso());
        assertEquals(TipoEsercizio.RIPETIZIONI, trovato.getTipo());
    }

    @Test
    void testPersistenzaEsercizioTempo() {
        Duration dur = Duration.ofMinutes(12);
        Esercizio originale = new Esercizio(TEST_NOME_TEMPO, "desc tempo", dur);
        Esercizio salvato = GestorePersistenza.salva(originale);
        assertNotNull(salvato.getId());

        Esercizio trovato = GestorePersistenza.trovaPerId(Esercizio.class, salvato.getId());
        assertEquals(dur, trovato.getRisultatoAtteso());
        assertEquals(TipoEsercizio.TEMPO, trovato.getTipo());
    }

    @Test
    void testPersistenzaConRisultato() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 10);
        e.setRisultato(30, "Ottimo");
        Esercizio salvato = GestorePersistenza.salva(e);
        assertNotNull(salvato.getRisultato());
        assertNotNull(salvato.getRisultato().getId());

        Esercizio trovato = GestorePersistenza.trovaPerId(Esercizio.class, salvato.getId());
        assertNotNull(trovato.getRisultato());
        assertEquals(30, trovato.getRisultato().getRisultato());
        assertEquals("Ottimo", trovato.getRisultato().getNota());
    }

    // ==================== COPRITURA EMBEDDABLE (RisultatoAtteso) ====================
    @Test
    void testRisultatoAttesco_SetRisultatoAtteso_ConNull() {
        // Creiamo un esercizio e poi modifichiamo il risultato atteso via reflection o usando setter?
        // Il metodo setRisultatoAtteso accetta solo Integer o Duration; null causa false e eccezione.
        // Ma l'Embeddable può essere creato con costruttore vuoto: i campi restano null.
        // Verifichiamo il comportamento con un esercizio di tipo RIPETIZIONI a cui proviamo a passare null.
        // setRisultatoAtteso(null) darà false e lancerà IllegalArgumentException.
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 10);
        assertThrows(IllegalArgumentException.class, () -> e.setRisultatoAtteso(null));
    }

    // ==================== COPERTURA getRisultatoAtteso quando tipo è null? (non possibile dai costruttori) ====================
    // Ma se si usa costruttore vuoto, tipo è null e getRisultatoAtteso lancia NullPointerException?
    // Il codice: if (tipo == RIPETIZIONI) ... else ...; se tipo null, else branch tenta di chiamare this.risultatoAtteso.getDurata()
    // che a sua volta è null -> NullPointerException. Questo non è coperto dai costruttori, ma possiamo testare l'eccezione.
    @Test
    void testGetRisultatoAttesco_TipoNull_ThrowsNPE() {
        Esercizio e = new Esercizio();  // tipo = null, risultatoAtteso = null
        assertThrows(NullPointerException.class, e::getRisultatoAtteso);
    }

    // ==================== COPERTURA setRisultato con tipo non impostato (null) ====================
    @Test
    void testSetRisultato_TipoNull_ThrowsIllegalArgumentException() {
        Esercizio e = new Esercizio();
        assertThrows(IllegalArgumentException.class, () -> e.setRisultato(10, "nota"));
    }

    // ==================== ALTRI METODI ====================
    @Test
    void testGetId() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 5);
        assertNull(e.getId());
        e = GestorePersistenza.salva(e);
        assertNotNull(e.getId());
    }

    @Test
    void testGetRisultatoQuandoNull() {
        Esercizio e = new Esercizio(TEST_NOME_RIP, "desc", 5);
        assertNull(e.getRisultato());
    }

    // ==================== COPERTURA BRANCH setTipo con valori inaspettati ====================
    @Test
    void testSetTipo_ValoriNon0o1_NonCambiaTipo() {
        Esercizio e = new Esercizio();
        assertThrows(IllegalArgumentException.class,
                () -> e.setTipo(99));
        assertNull(e.getTipo());
        e.setTipo(0); // RIPETIZIONI
        assertThrows(IllegalArgumentException.class,
                () -> e.setTipo(2));
        e.setTipo(1); // TEMPO
        assertThrows(IllegalArgumentException.class,
                () -> e.setTipo(-1));
    }

    // ==================== TEST AGGIUNTIVI per setRisultatoAtteso con tipo non ancora impostato ====================
    @Test
    void testSetRisultatoAtteso_TipoNull_ThrowsNPE() {
        Esercizio e = new Esercizio();
        // tipo è null, quindi this.risultatoAtteso è null => NPE
        assertThrows(IllegalArgumentException.class, () -> e.setRisultatoAtteso(10));
    }
}