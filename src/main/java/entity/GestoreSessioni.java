package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class GestoreSessioni {
    /**
     * Classe di gestione di tutte le sessioni create, in corso e completate
     * Permette la visualizzazione e la modifica di esse
     */

    public HashSet<SessioneDiAllenamento> listaSessioni;
                                            //Non sono sicuro se abbia senso
                                            // mettere questo HashSet se
                                            // alla fine prendiamo tutto dal database,
                                            //bisogna chiedere al prof

    private static GestoreSessioni instance;

    private GestoreSessioni(){
        /**
         * Costruttore di GestoreSessioni
         */
    }

    public GestoreSessioni getInstance(){
        /**
         * Fornisce l'istanza singola di GestoreSessioni, se essa non esiste viene creata
         */
        if(instance == null){
            instance = new GestoreSessioni();
        }

        return instance;
    }

    public ArrayList<Esercizio> dettaglioSessione(){

    }

    public HashSet<SessioneDiAllenamento> cercaSessione(Atleta atleta){

    }


    public void completaSessione(){
        SessioneDiAllenamento s;
        //cerca sessione
        s.setStato("COMPLETATO");
        s.registraRisultati();
    }

    public SessioneDiAllenamento getSessione(){
        s = //ricerca sessione

        return s;
    }

    public SessioneDiAllenamento creaSessione(String titolo, LocalDate data, String descrizione, ArrayList<Esercizio> esercizi){
        return new SessioneDiAllenamento(titolo, descrizione, data);
    }


}
