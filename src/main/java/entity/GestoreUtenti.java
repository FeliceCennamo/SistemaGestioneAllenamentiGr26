package entity;

import java.util.HashSet;

public class GestoreUtenti {
    /**
     *Fornisce i metodi per la gestione degli utenti registrati al sistema
     * Il costruttore è privato, di conseguenza richiamare il metodo .getInstance
     */

    private HashSet<Utenti> listaUtenti;
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

    public HashSet<Utenti> getListaUtenti(){
        return listaUtenti;
    }

    public Utente cercaAtleta(){

    }

    public Utente cercaAllenatore(){

    }

    public void associaAtletaAllenatore(){

    }

    public void gestisciProfiloAtleta(){

    }

    public void loginUtente(){

    }

    public void RegistrazioneUtente(){

    }

    public void addUtente(/*Informazioni dell'utente*/){
        listaUtenti.add(/*Utente*/);

    }

    public void monitoraPrestazioni(){

    }

}
