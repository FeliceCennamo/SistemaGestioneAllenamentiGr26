package entity;

import database.GestorePersistenza;
import exceptions.ResourceNotFoundException;

import java.time.Duration;
import java.util.*;

/**
 * Gestore centralizzato per le operazioni sulle sessioni di allenamento.
 * <p>
 * Espone metodi per cercare, visualizzare i dettagli e completare le sessioni,
 * operando come unico punto di accesso alla logica di dominio relativa
 * a {@link SessioneDiAllenamento}. Implementa il pattern Singleton.
 * </p>
 */
public class GestoreSessioni {

    private static GestoreSessioni instance;

    /**
     * Costruttore privato per impedire l'istanziazione diretta.
     */
    private GestoreSessioni() {
    }

    /**
     * Restituisce l'istanza Singleton del gestore, creandola se necessario.
     *
     * @return l'istanza condivisa di {@code GestoreSessioni}
     */
    public static GestoreSessioni getInstance() {
        if (instance == null) {
            instance = new GestoreSessioni();
        }
        return instance;
    }

    /**
     * Recupera la lista degli esercizi che compongono una sessione.
     *
     * @param idSessione identificativo della sessione
     * @return lista ordinata degli esercizi presenti
     * @throws ResourceNotFoundException se la sessione non esiste
     */
    public List<Esercizio> dettaglioSessione(Long idSessione) {
        SessioneDiAllenamento sessione = getSessione(idSessione);
        return sessione.getEsercizi();
    }

    /**
     * Restituisce tutte le sessioni presenti nel sistema, senza filtri.
     *
     * @return insieme di tutte le sessioni
     */
    public Set<SessioneDiAllenamento> cercaSessioni() {
        List<SessioneDiAllenamento> lista = GestorePersistenza.ottieniTutti(SessioneDiAllenamento.class);
        return new HashSet<>(lista);
    }

    /**
     * Cerca tutte le sessioni associate a un determinato atleta.
     *
     * @param idAtleta identificativo dell'atleta
     * @return insieme delle sessioni a cui l'atleta partecipa
     */
    public Set<SessioneDiAllenamento> cercaSessioni(Long idAtleta) {
        String query = "SELECT s FROM SessioneDiAllenamento s WHERE s.atleta.id = :id_atleta";
        List<SessioneDiAllenamento> lista = GestorePersistenza.eseguiQuery(
                query, SessioneDiAllenamento.class, Map.of("id_atleta", idAtleta));
        return new HashSet<>(lista);
    }

    /**
     * Permette a un atleta di completare una sessione registrando i risultati
     * e le note per ciascun esercizio.
     * <p>
     * Lo stato della sessione viene impostato a "IN CORSO" all'inizio
     * e, se tutti gli esercizi hanno un risultato valorizzato, a "COMPLETATA"
     * al termine.
     * </p>
     *
     * @param idAtleta   identificativo dell'atleta che sta completando la sessione
     * @param idSessione identificativo della sessione da completare
     * @param risultati  mappa che associa l'id dell'esercizio al valore del risultato
     *                   (numero di ripetizioni o minuti totali come intero)
     * @param note       mappa che associa l'id dell'esercizio a una nota testuale
     * @throws IllegalAccessException se l'atleta non è il destinatario della sessione
     * @throws ClassCastException     se i tipi dei risultati non sono coerenti con gli esercizi
     */
    public void completaSessione(Long idAtleta, Long idSessione,
                                 HashMap<Long, Integer> risultati,
                                 HashMap<Long, String> note)
            throws IllegalAccessException {

        SessioneDiAllenamento sessione;
        try {
            sessione = getSessione(idSessione);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Verifica che l'atleta sia effettivamente assegnato alla sessione
        if (!sessione.getAtleta().getId().equals(idAtleta)) {
            throw new IllegalAccessException("La sessione non appartiene all'utente");
        }

        sessione.setStato("IN CORSO");

        // Itera su tutti gli esercizi della sessione per registrare risultati e note
        for (Long idEsercizio : sessione.getEsercizi().stream().map(Esercizio::getId).toList()) {
            Esercizio esercizio = GestorePersistenza.trovaPerId(Esercizio.class, idEsercizio);

            Object valore = null;
            if (risultati.containsKey(idEsercizio)) {
                // Converte l'intero in Duration per gli esercizi a tempo
                if (esercizio.getTipo() == TipoEsercizio.TEMPO) {
                    valore = Duration.ofMinutes(risultati.get(idEsercizio));
                } else if (esercizio.getTipo() == TipoEsercizio.RIPETIZIONI) {
                    valore = risultati.get(idEsercizio);
                }
            }

            String nota = note.getOrDefault(idEsercizio, null);

            sessione.registraRisultato(valore, nota, idEsercizio);
        }

        // Controlla se tutti gli esercizi hanno un risultato valorizzato
        boolean completata = sessione.getEsercizi().stream()
                .allMatch(e -> e.getRisultato() != null && e.getRisultato().getRisultato() != null);

        if (completata) {
            sessione.setStato("COMPLETATA");
        }

        GestorePersistenza.salva(sessione);
    }

    /**
     * Carica una sessione dato il suo identificativo.
     *
     * @param idSessione identificativo della sessione
     * @return la sessione trovata
     * @throws ResourceNotFoundException se non esiste una sessione con l'id fornito
     */
    public SessioneDiAllenamento getSessione(Long idSessione) throws ResourceNotFoundException {
        SessioneDiAllenamento s = GestorePersistenza.trovaPerId(SessioneDiAllenamento.class, idSessione);
        if (s == null) {
            throw new ResourceNotFoundException("Sessione non trovata");
        }
        return s;
    }
}