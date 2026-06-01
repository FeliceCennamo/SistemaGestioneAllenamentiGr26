package controller;

import entity.*;

import java.time.LocalDate;
import java.util.*;

public class Control_session {

    private Long id_utente_autenticato =74L;
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

    public Long getAutenticato(){
        return id_utente_autenticato;
    }

    public void setAutenticato(Long id_utente){
        this.id_utente_autenticato = id_utente;
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


    public List<Esercizio> stubGetEsercizioForSessione(){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        List<Esercizio> h = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            Esercizio e = new Esercizio();
            h.add(e);
        }

        return h;
    }

    public void completaSessione(Long id_sessione, Map<Long, String[]> risultati_row){

        GestoreSessioni gestore = GestoreSessioni.getInstance();

        HashMap<Long, String> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();

        for(Long id : risultati_row.keySet()) {
            note.put(id, risultati_row.get(id)[0]);
            risultati.put(id, risultati_row.get(id)[1]);
        }

        try {
            gestore.completaSessione(this.id_utente_autenticato, id_sessione, risultati, note);
        }catch (IllegalAccessException e){
            System.out.println("Sessione non trovata");
        }
    }

}
