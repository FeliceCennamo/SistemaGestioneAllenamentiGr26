package entity;

import Exceptions.ResourceNotFoundException;
import database.GestorePersistenza;
import database.JpaUtil;
import jakarta.persistence.EntityNotFoundException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe di gestione di tutte le sessioni create, in corso e completate
 * Permette la visualizzazione e la modifica di esse
 */
public class GestoreSessioni {

    /**
     * Accesso al package database
     */
    private static final GestorePersistenza persistence_sessioni = new GestorePersistenza();

    /**
     * Istanza statica del gestoreSessioni
     */
    private static GestoreSessioni instance;

    /**
     * Costruttore di GestoreSessioni
     */
    private GestoreSessioni() {}

    /**
     * Fornisce l'istanza singola di GestoreSessioni, se essa non esiste viene creata
     * @return Istanza di gestoreSessioni operativa
     */
    public static GestoreSessioni getInstance() {
        if (instance == null) {
            instance = new GestoreSessioni();
        }

        return instance;
    }

    /**
     * Permette di vedere gli esercizi che compongono una sessione dato il suo id
     * @param id_sessione id della sessione di cui si vogliono visualizzare gli esercizi
     * @return Lista degli esercizi presenti nella sessione
     */
    public List<Esercizio> dettaglioSessione(Long id_sessione) {
        try {
            SessioneDiAllenamento sessione = getSessione(id_sessione);
            return sessione.getEsercizi();
        }
        catch(EntityNotFoundException e){
            System.out.println("sessione non trovata per l'id: " + id_sessione);
            throw e;
        }

    }

    /**
     * Permette di cercare tutte le sessioni presenti e passate
     * @return Set di tutte le sessioni presenti nel sistema
     */
    public Set<SessioneDiAllenamento> cercaSessioni(){
        List<SessioneDiAllenamento> listaSessioni =persistence_sessioni.ottieniTutti(SessioneDiAllenamento.class);
        return new HashSet<>(listaSessioni);
    }

    /**
     * Permette di cercare tutte le sessioni associate a un dato atleta
     * @param id_atleta Id dell'atleta di cui si stanno cercando le sessioni di allenamento
     * @return Set delle sessioni associate all'atleta
     */
    public Set<SessioneDiAllenamento> cercaSessioni(Long id_atleta) {
        String query = "SELECT s FROM SessioneDiAllenamento s WHERE s.atleta.id = :id_atleta";
        List<SessioneDiAllenamento> lista_sessioni = persistence_sessioni.eseguiQuery(query, SessioneDiAllenamento.class, Map.of("id_atleta", id_atleta));
        return new HashSet<>(lista_sessioni);
    }


    /**
     *Permette a un atleta di completare la sessione
     * @param id_atleta Id dell'atleta che ha richiesto di completare la sessione
     * @param id_sessione Id della sessione che si sta cercando di completare
     * @throws IllegalAccessException Se l'utente prova a modificare una sessione che non è stata assegnata a lui
     */
    public void completaSessione(Long id_atleta, Long id_sessione, HashMap<Long, Integer> risultati, HashMap<Long, String> note) throws IllegalAccessException {

        SessioneDiAllenamento s;
        try{
            s = getSessione(id_sessione);
        }catch(EntityNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if(s.getAtleta().getId().equals(id_atleta)) {
            s.setStato("IN CORSO");
            for(Long es : s.getEsercizi().stream().map(Esercizio::getId).toList()) {
                Esercizio esercizio  = persistence_sessioni.trovaPerId(Esercizio.class, es);

                Object ris = null;
                String nota = null;
                if(risultati.containsKey(es)){
                    if (esercizio.getTipo() == TipoEsercizio.RIPETIZIONI)
                        ris = risultati.get(es);
                    else if (esercizio.getTipo() == TipoEsercizio.TEMPO)
                        ris = Duration.ofMinutes(risultati.get(es));
                }
                if(note.containsKey(es)){
                    nota = note.get(es);
                }

                s.registraRisultato(ris, nota, es);
            }

        }else{
            throw new IllegalAccessException("La sessione non appartiene all'utente");
        }

        for(Esercizio e: s.getEsercizi()){
            if(e.getRisultato().getRisultato() == null){
                return; //Se trova un esercizio dove il risultato è null, allora non tutti i risultati sono stati inseriti
                        //Non può quindi essere completata la sessione
            }
        }
        s.setStato("COMPLETATA"); //Se viene eseguito questo metodo, la schermatura del for è stata superata
        persistence_sessioni.salva(s);
    }

    /**
     *Permette di ricercare una sessione dato il suo id
     * @param id_sessione Id della sessione che stiamo cercando
     * @throws EntityNotFoundException Se la sessione richiesta non è stata trovata
     * @return sessione richiesta
     */
    public SessioneDiAllenamento getSessione(Long id_sessione) throws EntityNotFoundException {
        SessioneDiAllenamento s = persistence_sessioni.trovaPerId(SessioneDiAllenamento.class, id_sessione);
        if(s == null){
            throw new EntityNotFoundException();
        }
        return s;
    }

    /**
     *Crea la sessione di allenamento dati i suoi parametri
     * @param id_allenatore Id dell'allenatore che crea la sessione
     * @param id_atleta Id dell'atleta che dovrà completare la sessione
     * @param data La data in cui ci si aspetta lo svolgimento della sessione
     * @param descrizione Descrizione della sessione
     * @param esercizi ArrayList degli esercizi che dovranno essere svolti
     * @param titolo Titolo della sessione
     * @return La sessione appena creata
     * @throws EntityNotFoundException Se L'allenatore non esiste nel database
     * @throws IllegalArgumentException Se l'atleta non è associato all'allenatore
     */
    public SessioneDiAllenamento creaSessione(String titolo, LocalDate data, String descrizione, Duration durata,
                                              ArrayList<Esercizio> esercizi, Long id_atleta, Long id_allenatore)
                                            throws EntityNotFoundException, ResourceNotFoundException {

        GestoreUtenti utenti = GestoreUtenti.getInstance();

        Allenatore allenatore = utenti.cercaAllenatore(id_allenatore);
        Atleta atleta = allenatore.getAtleta(id_atleta);

        SessioneDiAllenamento s = new SessioneDiAllenamento(titolo, descrizione, data, durata, atleta, allenatore);
        s.setEsercizi(esercizi);

        persistence_sessioni.salva(s);

        return s;
    }



}
