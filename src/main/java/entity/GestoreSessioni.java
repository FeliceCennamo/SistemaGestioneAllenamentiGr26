package entity;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

public class GestoreSessioni {
    /**
     * Classe di gestione di tutte le sessioni create, in corso e completate
     * Permette la visualizzazione e la modifica di esse
     */

    /*public HashSet<SessioneDiAllenamento> listaSessioni;
    Non sono sicuro se abbia senso
     mettere questo HashSet se
     alla fine prendiamo tutto dal database,
    bisogna chiedere al prof*/
    // private repo_sessioni

    private static GestoreSessioni instance;

    private GestoreSessioni() {
        /**
         * Costruttore di GestoreSessioni
         */
    }

    public GestoreSessioni getInstance() {
        /**
         * Fornisce l'istanza singola di GestoreSessioni, se essa non esiste viene creata
         */
        if (instance == null) {
            instance = new GestoreSessioni();
        }

        return instance;
    }

    public List<Esercizio> dettaglioSessione(Long id_sessione) {
        SessioneDiAllenamento sessione = this.getSessione(id_sessione);
        return sessione.getEsercizi();
    }

    public HashSet<SessioneDiAllenamento> cercaSessioni(){
        HashSet<SessioneDiAllenamento> listaSessioni = //ricerca nel db
        return listaSessioni;
    }

    public HashSet<SessioneDiAllenamento> cercaSessioni(Long id_atleta) {
        HashSet<SessioneDiAllenamento> listaSessioni = this.cercaSessioni();
        listaSessioni.removeIf(sessione -> !sessione.getAtleta().getId().equals(id_atleta));
        return listaSessioni;

    }


    public void completaSessione(Long id_atleta, Long id_sessione) {
        SessioneDiAllenamento s = this.getSessione(id_sessione);
        //cerca sessione
        s.setStato("COMPLETATO");
        s.registraRisultati();
    }

    public SessioneDiAllenamento getSessione(Long id_sessione) {
        HashSet<SessioneDiAllenamento> listaSessioni = this.cercaSessioni();
        for(SessioneDiAllenamento sessione : listaSessioni){
            if(sessione.getId().equals(id_sessione)){
                return sessione;
            }
        }

        return null;
    }

    public SessioneDiAllenamento creaSessione(String titolo, LocalDate data, String descrizione, ArrayList<Esercizio> esercizi, Atleta atleta, Allenatore allenatore) {
        SessioneDiAllenamento s = new SessioneDiAllenamento(titolo, descrizione, data);
        s.setAllenatore(allenatore);
        s.setAtleta(atleta);
        s.setEsercizi(esercizi);

        return s;

    }

}
