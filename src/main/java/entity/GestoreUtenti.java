package entity;

import database.GestorePersistenza;
import exceptions.ResourceNotFoundException;
import jakarta.persistence.metamodel.EntityType;

import java.util.HashSet;
import java.util.Set;

/**
 * Fornisce i metodi per la gestione degli utenti registrati al sistema
 * Il costruttore è privato, di conseguenza richiamare il metodo gestoreUtente.getInstance()
 */
public class GestoreUtenti {
    
    /**
     * Istanza statica del gestoreUtenti
     */
    private static GestoreUtenti instance;

    /**
     * Costruttore di GestoreUtenti
     */
    private GestoreUtenti() {
    }

    /**
     * Fornisce l'istanza singola di GestoreUtente, se essa non esiste viene creata
     *
     * @return Istanza operativa di gestoreUtente
     */
    public static GestoreUtenti getInstance() {
        if (instance == null) {
            instance = new GestoreUtenti();
        }

        return instance;
    }

    /**
     * Dato in ingresso l'id dell'atleta, restituisce l'oggetto.
     *
     * @param id_atleta Id dell'atleta che si vuole ricercare
     * @return Oggetto atleta che è stato trovato
     * @throws ResourceNotFoundException
     */
    public Atleta cercaAtleta(Long id_atleta) throws ResourceNotFoundException {
        Atleta a = GestorePersistenza.trovaPerId(Atleta.class, id_atleta);
        if (a == null) {
            throw new ResourceNotFoundException("Atleta non trovato");
        }
        return a;
    }

    /**
     * Dato in ingresso l'id dell'allenatore, restituisce l'oggetto.
     *
     * @param id_allenatore Id dell'allenatore che si vuole ricercare
     * @return Oggetto allenatore che è stato trovato
     * @throws ResourceNotFoundException
     */
    public Allenatore cercaAllenatore(Long id_allenatore) throws ResourceNotFoundException {
        Allenatore a = GestorePersistenza.trovaPerId(Allenatore.class, id_allenatore);
        if (a == null) {
            throw new ResourceNotFoundException("Allenatore non trovato");
        }
        return a;
    }
}
