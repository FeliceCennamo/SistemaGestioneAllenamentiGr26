package controller;

import entity.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Control_session {

    private static Control_session instance;

    /**
     * Costruttore di GestoreSessioni
     */
    private Control_session() {}

    /**
     * Fornisce l'istanza singola di GestoreSessioni, se essa non esiste viene creata
     * @return Istanza di gestoreSessioni operativa
     */
    public static Control_session getInstance() {
        if (instance == null) {
            instance = new Control_session();
        }

        return instance;
    }



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

    public List<Esercizio> getEserciziforSessione(Long id_sessione){
        GestoreSessioni g_session = GestoreSessioni.getInstance();
        return g_session.dettaglioSessione(id_sessione);
    }

    public SessioneDiAllenamento getSessioneforId(Long id_sessione){
        GestoreSessioni g = GestoreSessioni.getInstance();
        return g.getSessione(id_sessione);

    }


    public List<Esercizio> stubGetEsercizioforUtente(){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        List<Esercizio> h = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            Esercizio e = new Esercizio();
            h.add(e);
        }

        return h;
    }

}
