package controller;

import entity.GestoreSessioni;
import entity.GestoreUtenti;
import java.util.*;

/**
 * Controller principale dell'applicazione, punto di accesso per le operazioni
 * richieste dall'interfaccia utente.
 * <p>
 * Incapsula la logica di presentazione, delegando la gestione della
 * persistenza e delle regole di dominio ai gestori specializzati
 * ({@link GestoreSessioni}, {@link GestoreUtenti}, ecc.).
 * </p>
 * <p>
 * L'utente autenticato è al momento determinato da un identificativo
 * fisso, simulando una sessione di lavoro senza autenticazione reale.
 * </p>
 */
public class Controller {

    /**
     * Identificativo dell'utente correntemente autenticato.
     * In una implementazione reale verrebbe recuperato dal contesto di sicurezza.
     */
    private Long idUtenteAutenticato = 87L;

    private static Controller instance;

    /**
     * Costruttore privato per il pattern Singleton.
     */
    private Controller() {
    }

    /**
     * Restituisce l'unica istanza del Controller, creandola se necessario.
     *
     * @return l'istanza Singleton
     */
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Recupera gli identificativi di tutte le sessioni associate a un utente.
     *
     * @param idUtente identificativo dell'atleta
     * @return insieme degli id delle sessioni trovate
     */
    public List<Long> getIdSessioniPerUtente(Long idUtente) {
        GestoreSessioni gestoreSessioni = GestoreSessioni.getInstance();

        List<Long> ids = gestoreSessioni.getIdSessioniForUtente(idUtente);

        return ids;
    }

    /**
     * Restituisce l'identificativo dell'utente attualmente autenticato.
     *
     * @return id utente
     */
    public Long getIdUtenteAutenticato() {
        return idUtenteAutenticato;
    }

    /**
     * Elenca gli identificativi di tutti gli esercizi contenuti in una sessione.
     *
     * @param idSessione identificativo della sessione
     * @return lista ordinata degli id degli esercizi
     */
    public List<Long> getIdEserciziPerSessione(Long idSessione) {
        return GestoreSessioni.getInstance().getIdEserciziForSessione(idSessione);
    }

    /**
     * Completa una sessione di allenamento registrando risultati e note
     * per ciascun esercizio.
     * <p>
     * La mappa in ingresso associa a ogni id esercizio un array di due stringhe:
     * <ul>
     *   <li>indice 0: nota testuale (può essere vuota)</li>
     *   <li>indice 1: valore del risultato (numero di ripetizioni o minuti totali)</li>
     * </ul>
     * Il valore viene convertito in intero; per gli esercizi a tempo si assume
     * che l'intero rappresenti minuti (la conversione a Duration è delegata
     * a {@link GestoreSessioni#completaSessione(Long, Long, HashMap, HashMap)}).
     * </p>
     *
     * @param idSessione    identificativo della sessione da completare
     * @param risultatiRow  mappa (id esercizio → [nota, risultato])
     * @throws NumberFormatException se il valore del risultato non è un intero valido
     *                               o è negativo
     * @throws ClassCastException    in caso di incompatibilità di tipo durante la
     *                               registrazione dei risultati
     */
    public void completaSessione(Long idSessione, Map<Long, String[]> risultatiRow) throws IllegalArgumentException {
        GestoreSessioni gestore = GestoreSessioni.getInstance();

        HashMap<Long, Integer> risultati = new HashMap<>();
        HashMap<Long, String> note = new HashMap<>();

        for (Map.Entry<Long, String[]> entry : risultatiRow.entrySet()) {
            Long idEsercizio = entry.getKey();
            String[] dati = entry.getValue();

            // dati[1] contiene il risultato come stringa
            if("".equals(dati[1])){
                throw new IllegalArgumentException("È proibito inserire la nota senza inserire il risultato.\n" +
                                                    "Svolgere l'esercizio inserendo sia la nota che il risultato");
            }
            int valore = Integer.parseInt(dati[1]);
            if (valore <= 0) {
                throw new NumberFormatException("Il risultato deve essere necessariamente un numero maggiore di 0");
            }

            risultati.put(idEsercizio, valore);
            note.put(idEsercizio, dati[0]);   // dati[0] è la nota
        }

        try {
            gestore.completaSessione(idUtenteAutenticato, idSessione, risultati, note);
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
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

        return GestoreSessioni.getInstance().getDettaglioEsercizioPerId(idSessione, idEsercizio);

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
        return GestoreSessioni.getInstance().getDettaglioSessionePerId(idSessione);

    }

    public boolean isCompleted(Long id_sessione){
        return GestoreSessioni.getInstance().isSessionCompleted(id_sessione);
    }
}