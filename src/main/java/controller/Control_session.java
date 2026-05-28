package controller;

import entity.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Control_session {

    public Set<SessioneDiAllenamento> getSessioneforUtente(Long id_utente){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        return g_session.cercaSessioni(id_utente);
    }

    public Set<SessioneDiAllenamento> stubGetSessioneforUtente(Long id_utente){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        HashSet<SessioneDiAllenamento> h = new HashSet<SessioneDiAllenamento>();

        for(int i = 0; i < 10; i++){
            SessioneDiAllenamento s = new SessioneDiAllenamento("Titolo " + i, "Descrizione " + i, LocalDate.now(),
                    new Atleta(), new Allenatore());
            h.add(s);
        }

        return h;
    }
}
