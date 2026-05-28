package entity;

import database.GestorePersistenza;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.metamodel.EntityType;

import java.util.HashSet;
import java.util.Set;

/**
 *Fornisce i metodi per la gestione degli utenti registrati al sistema
 *Il costruttore è privato, di conseguenza richiamare il metodo gestoreUtente.getInstance()
 */
public class GestoreUtenti {

    /**Accesso al package database*/
    private static final GestorePersistenza persistence_utenti = new GestorePersistenza();

    /**Istanza statica del gestoreUtenti*/
    private static GestoreUtenti instance;

    /**
     * Costruttore di GestoreUtenti
     */
    private GestoreUtenti(){}

    /**
     * Fornisce l'istanza singola di GestoreUtente, se essa non esiste viene creata
     * @return Istanza operativa di gestoreUtente
     */
    public static GestoreUtenti getInstance(){
        if(instance == null){
            instance = new GestoreUtenti();
        }

        return instance;
    }

    /**
     * Permette di ottenere tutti gli utenti registrati nel sistema
     * @return Set degli utenti registrati
     */
    public Set<Utente> getListaUtenti(){
        Set<Utente> listaUtenti = new HashSet<>();
        Set<EntityType<?>> listaFiglie = persistence_utenti.getFiglie(Utente.class);
        for(EntityType<?> figlia: listaFiglie){
            persistence_utenti.ottieniTutti(figlia.getClass());
        }
        return listaUtenti;
    }

    /**
     * Dato in ingresso l'id dell'atleta, restituisce l'oggetto. Se l'atleta non esiste genera una EntityNotFoundException
     * @param id_atleta Id dell'atleta che si vuole ricercare
     * @return Oggetto atleta che è stato trovato
     */
    public Atleta cercaAtleta(Long id_atleta) throws EntityNotFoundException{
        Atleta a = persistence_utenti.trovaPerId(Atleta.class, id_atleta);
        if(a == null){
            throw new EntityNotFoundException("Atleta non trovato");
        }
        return a;
    }

    /**
     * Dato in ingresso l'id dell'allenatore, restituisce l'oggetto. Se l'allenatore non esiste genera una EntityNotFoundException
     * @param id_allenatore Id dell'allenatore che si vuole ricercare
     * @return Oggetto allenatore che è stato trovato
     */
    public Allenatore cercaAllenatore(Long id_allenatore) throws EntityNotFoundException{
        Allenatore a = persistence_utenti.trovaPerId(Allenatore.class, id_allenatore);
        if(a == null){
            throw new EntityNotFoundException("Allenatore non trovato");
        }
        return a;
    }

    /**
     * Dati un allenatore e un atleta, permette a essi di associarsi l'un l'altro
     * @param id_allenatore allenatore che vuole associare l'atleta
     * @param id_atleta atleta che deve essere associato a quell'allenatore
     */
    public void associaAtletaAllenatore(Long id_atleta, Long id_allenatore){
        Allenatore allenatore;
        Atleta atleta;
        try {
            atleta = cercaAtleta(id_atleta);
            allenatore = cercaAllenatore(id_allenatore);
        }catch(EntityNotFoundException e) {
            e.printStackTrace();
            return;
        }
        allenatore.addAtleta(atleta);
        atleta.addAllenatore(allenatore);
    }

    /**
     * Permette all'allenatore di modificare le informazioni relative all'esperienza dell'atleta
     * @param id_allenatore id dell'allenatore che compie l'operazione
     * @param id_atleta id dell'atleta a cui vanno modificati i valori di carriera
     * @param disciplina La nuova disciplina dell'atleta
     * @param livello il nuovo livello dell'atleta
     * @param obiettivo il nuovo obiettivo dell'atleta
     */
    public void gestisciProfiloAtleta(Long id_allenatore, Long id_atleta, String obiettivo, String disciplina, int livello){
        Allenatore allenatore;
        Atleta atleta;
        try{
                allenatore = cercaAllenatore(id_allenatore);
        }catch(EntityNotFoundException e){
            e.printStackTrace();
            return;
        }

        try {
            atleta = allenatore.getAtleta(id_atleta);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            return;
        }

        atleta.setDisciplina(disciplina);
        atleta.setLivello(livello);
        atleta.setObiettivo(obiettivo);

    }

    public void loginUtente(){
        //Caso d'uso non implementato
    }

    public void RegistrazioneUtente(){
        //Caso d'uso non implementato
    }

    public void monitoraPrestazioni(){
        //Caso d'uso non implementato
    }

}
