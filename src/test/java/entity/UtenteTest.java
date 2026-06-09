package entity;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UtenteTest {

    @Test
    public void SettersEGetters_validi(){
        Utente utente = new Atleta();
        utente.setNome("Mario");
        utente.setCognome("Rossi");
        utente.setMail("mario.rossi@prova.com");
        utente.setDisciplinaPrevalente("Calcio");
        utente.setPassword("Password123");

        assertEquals(utente.getNome(), "Mario");
        assertEquals(utente.getCognome(), "Rossi");
        assertEquals(utente.getMail(), "mario.rossi@prova.com");
        assertEquals(utente.getDisciplinaPrevalente(), "Calcio");
        assertTrue(BCrypt.checkpw("Password123", utente.getPassword()));
    }

    @Test
    public void SettersEGetters_NonValidi(){
        Utente utente = new Atleta();
        utente.setNome(null);
        utente.setCognome(null);
        utente.setMail(null);
        utente.setDisciplinaPrevalente(null);

        //La gestione dei NullPointerException è demandata ai
        //metodi che richiameranno i setter (GestoreUtenti)
        assertNull(utente.getNome());
        assertNull(utente.getCognome());
        assertNull(utente.getMail());
        assertNull(utente.getDisciplinaPrevalente());
    }
}
