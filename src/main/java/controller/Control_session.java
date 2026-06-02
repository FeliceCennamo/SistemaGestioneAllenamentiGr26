package controller;

import com.mysql.cj.Session;
import entity.*;

import java.time.LocalDate;
import java.util.*;

public class Control_session {

    private Long id_utente_autenticato =133L;
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

    public Set<Long> getSessioneforUtente(Long id_utente){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        Set<SessioneDiAllenamento> s = g_session.cercaSessioni(id_utente);
        Set<Long> id_set = new HashSet<>();
        for(SessioneDiAllenamento sessione: s){
            id_set.add(sessione.getId());
        }
        return id_set;
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

    public List<Long> getEserciziforSessione(Long id_sessione){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        List<Long> ids = new ArrayList<>();

        for(Esercizio e : g_session.dettaglioSessione(id_sessione)) {

            ids.add(e.getId());

        }
        return ids;
    }

    public SessioneDiAllenamento getSessioneforId(Long id_sessione){
        GestoreSessioni g = GestoreSessioni.getInstance();
        return g.getSessione(id_sessione);

    }


    public void completaSessione(Long id_sessione, Map<Long, String[]> risultati_row){

        GestoreSessioni gestore = GestoreSessioni.getInstance();

        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();

        for(Long id : risultati_row.keySet()) {
            note.put(id, risultati_row.get(id)[0]);
            risultati.put(id, Integer.parseInt(risultati_row.get(id)[1]));
        }

        try {
            gestore.completaSessione(this.id_utente_autenticato, id_sessione, risultati, note);
        }catch (IllegalAccessException e){
            System.out.println("Sessione non appartenente all'utente");
        }
    }

    public Map<String, Object> dettaglioEsercizioPerId(Long id_sessione,Long id_esercizio){

        Map<String,Object> dettaglio = new HashMap<>();
        SessioneDiAllenamento s = this.getSessioneforId(id_sessione);
        Esercizio e = s.getEsercizioPerId(id_esercizio);

        SessioneDiAllenamento.Stato stato = s.getStato();


        dettaglio.put("descrizione", e.getDescrizione());
        dettaglio.put("nome", e.getNome());

        switch (stato) {

            case IN_CORSO -> dettaglio.put("stato", 1);
            case ASSEGNATA -> dettaglio.put("stato", 0);
            case COMPLETATA -> dettaglio.put("stato", 2);
            default -> dettaglio.put("stato", null);


        }


        if(e.getRisultato() == null){
            dettaglio.put("nota", null);
            dettaglio.put("risultato", null);


        }
        else{
            dettaglio.put("nota", e.getRisultato().getNota());
            dettaglio.put("risultato", e.getRisultato().getRisultato());

        }

        return dettaglio;

    }

}
