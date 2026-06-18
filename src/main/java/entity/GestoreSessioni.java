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
     * Cerca tutte le sessioni associate a un determinato utente e restituisce il loro id.
     *
     * @param idUtente identificativo dell'utente
     * @return insieme degli id delle sessioni associate all'utente
     */
    public List<Long> getIdSessioniForUtente(Long idUtente) {
        TreeSet<SessioneDiAllenamento> sessioni = new TreeSet<>(cercaSessioni(idUtente));

        List<Long> ids = new ArrayList<>();
        for (SessioneDiAllenamento sessione : sessioni) {
            ids.add(sessione.getId());
        }

        return ids;
    }

    /**
     * Elenca gli identificativi di tutti gli esercizi contenuti in una sessione.
     *
     * @param idSessione identificativo della sessione
     * @return lista ordinata degli id degli esercizi
     */
    public List<Long> getIdEserciziForSessione(Long idSessione){
        List<Long> ids = new ArrayList<>();

        for (Esercizio esercizio : dettaglioSessione(idSessione)) {
            ids.add(esercizio.getId());
        }
        return ids;
    }

    /**
     * Restituisce una mappa con i dati riepilogativi di una sessione.
     * <p>
     * Le chiavi presenti sono:
     * <ul>
     *   <li>{@code titolo} – titolo della sessione</li>
     *   <li>{@code allenatore} – nome e cognome dell'allenatore</li>
     *   <li>{@code email_allenatore} – email dell'allenatore</li>
     *   <li>{@code descrizione} – descrizione testuale</li>
     *   <li>{@code stato} – stato corrente</li>
     *   <li>{@code data} – data di svolgimento</li>
     * </ul>
     *
     * @param idSessione identificativo della sessione
     * @return mappa contenente il dettaglio
     */
    public Map<String, Object> getDettaglioSessionePerId(Long idSessione) {
        Map<String, Object> dettaglio = new HashMap<>();
        SessioneDiAllenamento sessione = getSessione(idSessione);

        dettaglio.put("titolo", sessione.getTitolo());
        dettaglio.put("allenatore", sessione.getAllenatore().getNome() + " " +
                sessione.getAllenatore().getCognome());
        dettaglio.put("email_allenatore", sessione.getAllenatore().getMail());
        dettaglio.put("descrizione", sessione.getDescrizione());
        dettaglio.put("stato", sessione.getStato().toString());
        dettaglio.put("data", sessione.getDataSvolgimento());

        return dettaglio;
    }

    /**
     * Restituisce una mappa con i dettagli di un esercizio all'interno di una
     * sessione.
     * <p>
     * Le chiavi presenti sono:
     * <ul>
     *   <li>{@code nome} – nome dell'esercizio</li>
     *   <li>{@code descrizione} – descrizione testuale</li>
     *   <li>{@code stato} – stato della sessione di appartenenza</li>
     *   <li>{@code nota} – nota associata al risultato (o null)</li>
     *   <li>{@code risultato} – valore effettivo del risultato (o null)</li>
     *   <li>{@code risultato_atteso} – target dell'esercizio</li>
     * </ul>
     *
     * @param idSessione  identificativo della sessione
     * @param idEsercizio identificativo dell'esercizio
     * @return mappa contenente le informazioni di dettaglio
     */
    public Map<String, Object> getDettaglioEsercizioPerId(Long idSessione, Long idEsercizio) {
        Map<String, Object> dettaglio = new HashMap<>();
        SessioneDiAllenamento sessione = this.getSessione(idSessione);
        Esercizio esercizio = sessione.getEsercizioPerId(idEsercizio);

        dettaglio.put("nome", esercizio.getNome());
        dettaglio.put("descrizione", esercizio.getDescrizione());
        dettaglio.put("stato", sessione.getStato().toString());

        Risultato risultato = esercizio.getRisultato();
        if (risultato == null) {
            dettaglio.put("nota", null);
            dettaglio.put("risultato", null);
        } else {
            dettaglio.put("nota", risultato.getNota());
            dettaglio.put("risultato", risultato.getRisultato());
        }

        dettaglio.put("risultato_atteso", esercizio.getRisultatoAtteso());

        return dettaglio;
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
        if(sessione.getStato() == StatoSessione.COMPLETATA){
            throw new IllegalAccessException("La sessione è già stata completata");
        }

        // Itera su tutti gli esercizi della sessione per registrare risultati e note
        for (Long idEsercizio : sessione.getEsercizi().stream().map(Esercizio::getId).toList()) {
            Esercizio esercizio = GestorePersistenza.trovaPerId(Esercizio.class, idEsercizio);

            Object valore = null;
            if (risultati.containsKey(idEsercizio)) {
                // Converte l'intero in Duration per gli esercizi a tempo
                if (esercizio.getTipo() == TipoEsercizio.TEMPO) {
                    valore = Duration.ofMinutes(risultati.getOrDefault(idEsercizio, null));
                } else if (esercizio.getTipo() == TipoEsercizio.RIPETIZIONI) {
                    valore = risultati.getOrDefault(idEsercizio, null);
                }
            }

            String nota = note.getOrDefault(idEsercizio, "");

            sessione.registraRisultato(valore, nota, idEsercizio);
        }

        // Controlla se tutti gli esercizi hanno un risultato valorizzato
        boolean completata = sessione.getEsercizi().stream()
                .allMatch(e -> e.getRisultato() != null && e.getRisultato().getRisultato() != null);

        if (completata) {
            sessione.setStato("COMPLETATA");
        }else{
            sessione.setStato("IN_CORSO");
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

    public boolean isSessionCompleted(Long idSessione){
        return getSessione(idSessione).getStato().toString().equals("COMPLETATA");
    }
}