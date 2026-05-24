package entity;

import java.util.HashSet;

public class GestoreUtenti {
    /**
     *Fornisce i metodi per la gestione degli utenti registrati al sistema
     *Il costruttore è privato, di conseguenza richiamare il metodo gestoreUtente.getInstance()
     */

    // Non ha senso sto HashSet, è meglio lavorare sul db, dobbiamo chiedere al prof se
    // questa cosa ha senso anche a livello UML del nostro diagramma
    //private repo_utenti (istanza della repository degli utenti sul db)
    private static GestoreUtenti instance;

    private GestoreUtenti(){
        /**
         * Costruttore di GestoreUtenti
         */
    }

    public GestoreUtenti getInstance(){
        /**
         * Fornisce l'istanza singola di GestoreUtente, se essa non esiste viene creata
         */
        if(instance == null){
            instance = new GestoreUtenti();
        }

        return instance;
    }

    public HashSet<Utente> getListaUtenti(){
        //return repo_utenti.findall()
    }

    public Atleta cercaAtleta(Long id_atleta){
        HashSet<Utente> listaUtenti = getListaUtenti();
        for(Utente utente : listaUtenti){
            if( utente instanceof Atleta && utente.getId().equals(id_atleta) ){
                return (Atleta) utente;
            }
        }
        return null;
    }

    public Allenatore cercaAllenatore(Long id_allenatore){
        HashSet<Utente> listaUtenti = getListaUtenti();
        for(Utente utente : listaUtenti){
            if(utente instanceof Allenatore && utente.getId().equals(id_allenatore)){
                return (Allenatore) utente;
            }
        }
        return null;
    }

    public void associaAtletaAllenatore(Long id_atleta, Long id_allenatore){
        Atleta atleta = cercaAtleta(id_atleta);
        Allenatore allenatore = cercaAllenatore(id_allenatore);
        if(atleta == null || allenatore == null){
            return;
        }
        allenatore.addAtleta(atleta);
        atleta.addAllenatore(allenatore);
    }

    public void gestisciProfiloAtleta(Long id_allenatore, Long id_atleta, String obiettivo, String disciplina, int livello){

        Allenatore allenatore = cercaAllenatore(id_allenatore);
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
