package entity;

import database.GestorePersistenza;
import exceptions.ResourceNotFoundException;

/**
 * Gestore centralizzato per la ricerca degli utenti (atleti e allenatori)
 * registrati nel sistema.
 * <p>
 * Implementa il pattern Singleton: l'unica istanza disponibile si ottiene
 * tramite {@link #getInstance()}.
 * </p>
 */
public class GestoreUtenti {

    private static GestoreUtenti instance;

    // Costruttore privato per impedire l'istanziazione diretta
    private GestoreUtenti() {
    }

    /**
     * Fornisce l'istanza Singleton del gestore, creandola se non ancora
     * inizializzata.
     *
     * @return l'istanza condivisa di {@code GestoreUtenti}
     */
    public static GestoreUtenti getInstance() {
        if (instance == null) {
            instance = new GestoreUtenti();
        }
        return instance;
    }

    /**
     * Cerca un atleta a partire dal suo identificativo.
     *
     * @param id_atleta identificativo dell'atleta da cercare
     * @return l'atleta trovato
     * @throws ResourceNotFoundException se non esiste un atleta con l'id specificato
     */
    public Atleta cercaAtleta(Long id_atleta) throws ResourceNotFoundException {
        Atleta a = GestorePersistenza.trovaPerId(Atleta.class, id_atleta);
        if (a == null) {
            throw new ResourceNotFoundException("Atleta non trovato");
        }
        return a;
    }

    /**
     * Cerca un allenatore a partire dal suo identificativo.
     *
     * @param id_allenatore identificativo dell'allenatore da cercare
     * @return l'allenatore trovato
     * @throws ResourceNotFoundException se non esiste un allenatore con l'id specificato
     */
    public Allenatore cercaAllenatore(Long id_allenatore) throws ResourceNotFoundException {
        Allenatore a = GestorePersistenza.trovaPerId(Allenatore.class, id_allenatore);
        if (a == null) {
            throw new ResourceNotFoundException("Allenatore non trovato");
        }
        return a;
    }
}