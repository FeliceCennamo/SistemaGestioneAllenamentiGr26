package entity;

import com.mysql.cj.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Classe di gestione di tutte le sessioni create, in corso e completate
 * Permette la visualizzazione e la modifica di esse
 */
public class GestoreSessioni {

    /*public HashSet<SessioneDiAllenamento> listaSessioni;
    Non sono sicuro se abbia senso
     mettere questo HashSet se
     alla fine prendiamo tutto dal database,
    bisogna chiedere al prof*/
    // private repo_sessioni

    private static GestoreSessioni instance;

    /**
     * Costruttore di GestoreSessioni
     */
    private GestoreSessioni() {}

    /**
     * Fornisce l'istanza singola di GestoreSessioni, se essa non esiste viene creata
     */
    public GestoreSessioni getInstance() {
        if (instance == null) {
            instance = new GestoreSessioni();
        }

        return instance;
    }

    /**
     * Permette di vedere gli esercizi che compongono una sessione dato il suo id
     */
    public List<Esercizio> dettaglioSessione(Long id_sessione) {
        SessioneDiAllenamento sessione = this.getSessione(id_sessione);
        return sessione.getEsercizi();
    }

    /**
     * Permette di cercare tutte le sessioni presenti e passate
     */
    public HashSet<SessioneDiAllenamento> cercaSessioni(){
        HashSet<SessioneDiAllenamento> listaSessioni = //ricerca nel db
        return listaSessioni;
    }

    public HashSet<SessioneDiAllenamento> cercaSessioni(Long id_atleta) {
        HashSet<SessioneDiAllenamento> listaSessioni = this.cercaSessioni();
        listaSessioni.removeIf(sessione -> !sessione.getAtleta().getId().equals(id_atleta));
        return listaSessioni;

    }


    /**
     *Permette a un atleta di completare la sessione
     * @param id_atleta id_atleta
     * @param id_sessione id_sessione
     *
     */
    public void completaSessione(Long id_atleta, Long id_sessione) {
        SessioneDiAllenamento s = this.getSessione(id_sessione);
        if(!s.getAtleta().getId().equals(id_atleta)){
            return;
        }
        //cerca sessione
        s.setStato("COMPLETATA");
        s.registraRisultati();
    }

    /**
     *Permette di ricercare una sessione dato il suo id
     */
    public SessioneDiAllenamento getSessione(Long id_sessione) {
        HashSet<SessioneDiAllenamento> listaSessioni = this.cercaSessioni();
        for(SessioneDiAllenamento sessione : listaSessioni){
            if(sessione.getId().equals(id_sessione)){
                return sessione;
            }
        }

        return null;
    }

    /**
     *Permette la creazione di una sessione dati in ingresso il suo titolo, la descrizione, la data di svolgimento e la lista di esercizi
     *Se l'allenatore non è associato all'atleta la sessione non viene creata e il metodo ritorna un valore null
     */
    public SessioneDiAllenamento creaSessione(String titolo, LocalDate data, String descrizione, ArrayList<Esercizio> esercizi, Long id_atleta, Long id_allenatore) {
        GestoreUtenti utenti = null;
        SessioneDiAllenamento s = null;
        utenti = utenti.getInstance();
        Allenatore allenatore = utenti.cercaAllenatore(id_allenatore);
        Atleta atleta = allenatore.getAtleta(id_atleta);
        if(atleta != null) {
            s = new SessioneDiAllenamento(titolo, descrizione, data);
            s.setAllenatore(allenatore);
            s.setAtleta(atleta);
            s.setEsercizi(esercizi);
            s.setStato("ASSEGNATA");
        }
        return s;
    }

}
