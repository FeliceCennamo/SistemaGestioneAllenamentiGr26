package controller;

import entity.*;

import java.util.*;

public class Control_session {

    private Long id_utente_autenticato =77L;
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

    public Set<Long> getIdSessioniPerUtente(Long id_utente){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        Set<SessioneDiAllenamento> s = g_session.cercaSessioni(id_utente);
        Set<Long> id_set = new HashSet<>();
        for(SessioneDiAllenamento sessione: s){
            id_set.add(sessione.getId());
        }
        return id_set;
    }

    public Long getIdUtenteAutenticato(){
        return id_utente_autenticato;
    }

   /* public void setAutenticato(Long id_utente){
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

    }*/

    /**
     * Restituisce gli Id degli esercizi appartenenti a una SessioneDiAllenamento
     * @param id_sessione id SessioneDiAllenamento
     * @return Lista contenente gli id degli esercizi della SessioneDiAllenamento
     * */
    public List<Long> getIdEserciziPerSessione(Long id_sessione){
        GestoreSessioni g_session = GestoreSessioni.getInstance();

        List<Long> ids = new ArrayList<>();

        for(Esercizio e : g_session.dettaglioSessione(id_sessione)) {

            ids.add(e.getId());

        }
        return ids;
    }

    /**
     * Restituisce la SessioneDiAllenamento corrispondente all'Id passato
     * @param id_sessione id SessioneDiAllenamento
     * @return Oggetto SessioneDiAllenamento corrispondente
     */
    public SessioneDiAllenamento getSessionePerId(Long id_sessione){
        GestoreSessioni g = GestoreSessioni.getInstance();
        return g.getSessione(id_sessione);

    }

    /**
     * Completa una sessione di allenamento
     * @param id_sessione id SessioneDiAllenamento
     * @param risultati_row Mappa<IdEsercizio, String[Nota, Risultato]>
     * */
    public void completaSessione(Long id_sessione, Map<Long, String[]> risultati_row) throws ClassCastException, NumberFormatException{

        GestoreSessioni gestore = GestoreSessioni.getInstance();

        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();

        for(Long id : risultati_row.keySet()) {
            int ris_intero = Integer.parseInt(risultati_row.get(id)[1]);
            if(ris_intero < 0){
                throw new NumberFormatException();
            }
            note.put(id, risultati_row.get(id)[0]);
            risultati.put(id, ris_intero);
        }

        try {
            gestore.completaSessione(this.id_utente_autenticato, id_sessione, risultati, note);
        }catch (IllegalAccessException e){
            System.out.println("Sessione non appartenente all'utente");
        }
    }

    /**
     *Restituisce una mappa contenente il dettaglio di un Esercizio
     * @param id_sessione id SessioneDiAllenamento
     * @param id_esercizio id Esercizio
     * @return La mappa contiene Nome, Descrizione, Stato Sessione, Nota e Risultato
     */
    public Map<String, Object> getDettaglioEsercizioPerId(Long id_sessione, Long id_esercizio){

        Map<String,Object> dettaglio = new HashMap<>();
        SessioneDiAllenamento s = this.getSessionePerId(id_sessione);
        Esercizio e = s.getEsercizioPerId(id_esercizio);


        dettaglio.put("descrizione", e.getDescrizione());
        dettaglio.put("nome", e.getNome());
        dettaglio.put("stato", s.getStato().toString());
        if(e.getRisultato() == null){
            dettaglio.put("nota", null);
            dettaglio.put("risultato", null);
        }
        else{
            dettaglio.put("nota", e.getRisultato().getNota());
            dettaglio.put("risultato", e.getRisultato().getRisultato());

        }

        dettaglio.put("risultato_atteso", e.getRisultatoAtteso());

        return dettaglio;

    }

    /**
     *Restituisce una mappa contenente il dettaglio di una SessioneDiAllenamento
     * @param id_sessione id SessioneDiAllenamento
     * @return La mappa contiene Titolo, Nome e Cognome Allenatore, Descrizione, Stato e Data
     */
    public Map<String, Object> getDettaglioSessionePerId(Long id_sessione){
        Map<String,Object> dettaglio = new HashMap<>();
        SessioneDiAllenamento s = this.getSessionePerId(id_sessione);

        dettaglio.put("titolo", s.getTitolo());
        dettaglio.put("allenatore", s.getAllenatore().getNome() + " " + s.getAllenatore().getCognome());

        dettaglio.put("descrizione", s.getDescrizione());
        dettaglio.put("stato", s.getStato().toString());
        dettaglio.put("data", s.getDataSvolgimento());

        return dettaglio;
    }

    public Map<String, String> getMailfromSession(Long id_sessione){

        Map<String, String> mails = new HashMap<>();
        SessioneDiAllenamento s = this.getSessionePerId(id_sessione);

        mails.put("allenatore", s.getAllenatore().getMail() );
        mails.put("atleta", s.getAtleta().getMail() );

        return mails;


    }
}
