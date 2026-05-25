package entity;

import java.util.HashSet;

/**
 *Fornisce i metodi per la gestione degli utenti registrati al sistema
 *Il costruttore è privato, di conseguenza richiamare il metodo gestoreUtente.getInstance()
 */
public class GestoreUtenti {

    /* Non ha senso sto HashSet, è meglio lavorare sul db, dobbiamo chiedere al prof se
     questa cosa ha senso anche a livello UML del nostro diagramma*/
    //private repo_utenti (istanza della repository degli utenti sul db)
    private static GestoreUtenti instance;

    /**
     * Costruttore di GestoreUtenti
     */
    private GestoreUtenti(){}

    /**
     * Fornisce l'istanza singola di GestoreUtente, se essa non esiste viene creata
     */
    public GestoreUtenti getInstance(){
        if(instance == null){
            instance = new GestoreUtenti();
        }

        return instance;
    }

    public HashSet<Utente> getListaUtenti(){
        //return repo_utenti.findall()
    }

    /**
     * Dato in ingresso l'id dell'atleta, restituisce l'oggetto
     */
    public Atleta cercaAtleta(Long id_atleta){
        HashSet<Utente> listaUtenti = this.getListaUtenti();
        for(Utente utente : listaUtenti){
            if( utente instanceof Atleta && utente.getId().equals(id_atleta) ){
                return (Atleta) utente;
            }
        }
        return null;
    }

    /**
     * Dato in ingresso l'id dell'allenatore, restituisce l'oggetto
     */
    public Allenatore cercaAllenatore(Long id_allenatore){
        HashSet<Utente> listaUtenti = this.getListaUtenti();
        for(Utente utente : listaUtenti){
            if(utente instanceof Allenatore && utente.getId().equals(id_allenatore)){
                return (Allenatore) utente;
            }
        }
        return null;
    }

    /**
     * Dati un allenatore e un atleta, permette a essi di associarsi l'un l'altro
     */
    public void associaAtletaAllenatore(Long id_atleta, Long id_allenatore){
        Atleta atleta = this.cercaAtleta(id_atleta);
        Allenatore allenatore = this.cercaAllenatore(id_allenatore);
        if(atleta == null || allenatore == null){
            return;
        }
        allenatore.addAtleta(atleta);
        atleta.addAllenatore(allenatore);
    }

    /**
     * Permette all'allenatore di modificare le informazioni relative all'esperienza dell'atleta
     */
    public void gestisciProfiloAtleta(Long id_allenatore, Long id_atleta, String obiettivo, String disciplina, int livello){
        Allenatore allenatore = this.cercaAllenatore(id_allenatore);
        if(allenatore == null){
            return;
        }
        Atleta atleta = allenatore.getAtleta(id_atleta);
        if(atleta == null){
            return;
        }
        atleta.setDisciplina(disciplina);
        atleta.setLivello(livello);
        atleta.setObbiettivo(obiettivo);

    }

    public void loginUtente(){

    }

    public void RegistrazioneUtente(){

    }

    public void addUtente(Utente utente){
        //non so se sto metodo ha senso metterlo se alla fine non abbiamo la lista
    }

    public void monitoraPrestazioni(){

    }

}
