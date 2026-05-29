package controller;

import entity.Esercizio;
import entity.GestoreSessioni;
import entity.GestoreUtenti;
import entity.SessioneDiAllenamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Controller BCED per le operazioni sulle sessioni di allenamento dell'atleta.
 * Fa da mediatore tra il layer Boundary e i GestoreSessioni / GestoreUtenti (Entity).
 * Singleton: viene istanziato una sola volta e condiviso da tutte le boundary.
 */
public class Control_Session {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static Control_Session instance;

    private Control_Session() {}

    public static Control_Session getInstance() {
        if (instance == null) {
            instance = new Control_Session();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // DTO leggero per trasferire i dati di una sessione alla boundary
    // senza esporre direttamente le entity JPA.
    // -------------------------------------------------------------------------

    /**
     * Snapshot immutabile dei dati di riepilogo di una SessioneDiAllenamento,
     * adatto ad essere visualizzato in una SchedaSingola.
     */
    public record SessioneSummaryDTO(
            Long   id,
            String titolo,
            String dataStr,
            int    numeroEsercizi,
            String nomeAllenatore,
            String stato
    ) {}

    /**
     * Snapshot immutabile dei dati di un singolo Esercizio,
     * adatto ad essere visualizzato in una SchedaSingolaEsercizio.
     */
    public record EsercizioDettDTO(
            Long   id,
            String nome,
            String descrizione,
            String tipo,           // "RIPETIZIONI" | "TEMPO"
            String risultatoAttesoStr,
            String risultatoOttenutoStr,  // null se non ancora registrato
            String notaStr                // null se non ancora registrata
    ) {}

    // -------------------------------------------------------------------------
    // USE CASE 1 – Visualizza elenco sessioni dell'atleta corrente
    // -------------------------------------------------------------------------

    /**
     * Restituisce la lista di summary DTO per tutte le sessioni dell'atleta.
     *
     * @param idAtleta id dell'atleta autenticato
     * @return lista ordinata per data (più recente prima)
     */
    public List<SessioneSummaryDTO> getSessioniAtleta(Long idAtleta) {
        GestoreSessioni gs = GestoreSessioni.getInstance();
        Set<SessioneDiAllenamento> sessioni = gs.cercaSessioni(idAtleta);

        List<SessioneSummaryDTO> dtos = new ArrayList<>();
        for (SessioneDiAllenamento s : sessioni) {
            dtos.add(toSummaryDTO(s));
        }

        // Ordina per data decrescente (sessioni più recenti prima)
        dtos.sort((a, b) -> b.dataStr().compareTo(a.dataStr()));
        return dtos;
    }

    // -------------------------------------------------------------------------
    // USE CASE 2 – Visualizza dettaglio sessione (lista esercizi)
    // -------------------------------------------------------------------------

    /**
     * Restituisce la lista degli esercizi di una sessione come DTO.
     *
     * @param idSessione id della sessione da aprire
     * @return lista di EsercizioDettDTO
     */
    public List<EsercizioDettDTO> getDettaglioSessione(Long idSessione) {
        GestoreSessioni gs = GestoreSessioni.getInstance();
        List<Esercizio> esercizi = gs.dettaglioSessione(idSessione);

        List<EsercizioDettDTO> dtos = new ArrayList<>();
        for (Esercizio e : esercizi) {
            dtos.add(toEsercizioDTO(e));
        }
        return dtos;
    }

    // -------------------------------------------------------------------------
    // USE CASE 3 – Registra risultato e nota per un singolo esercizio
    // -------------------------------------------------------------------------

    /**
     * Registra per un esercizio il risultato effettivamente ottenuto e la nota
     * dell'atleta.  Il tipo del risultato (Integer per RIPETIZIONI, String in
     * formato ISO-8601 per TEMPO) viene convertito qui prima di passarlo
     * all'entity, così la boundary rimane completamente disaccoppiata.
     *
     * @param idSessione  id della sessione contenente l'esercizio
     * @param idEsercizio id dell'esercizio da aggiornare
     * @param risultatoRaw stringa inserita dall'utente (numero intero o HH:MM:SS)
     * @param nota        testo libero dell'atleta
     * @throws IllegalArgumentException se il formato del risultato non è valido
     * @throws IllegalAccessException   non usata qui, propagata per coerenza
     */
    public void registraRisultatoEsercizio(Long idSessione,
                                           Long idEsercizio,
                                           String risultatoRaw,
                                           String nota)
            throws IllegalArgumentException {

        GestoreSessioni gs = GestoreSessioni.getInstance();
        SessioneDiAllenamento sessione = gs.getSessione(idSessione);

        // Cerca l'esercizio nella sessione
        Esercizio target = sessione.getEsercizi().stream()
                .filter(e -> e.getId().equals(idEsercizio))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Esercizio con id " + idEsercizio + " non trovato nella sessione " + idSessione));

        // Converti la stringa grezza nel tipo atteso dall'entity
        Object risultatoConverted = parseRisultato(target.getTipo(), risultatoRaw);
        target.setRisultato(risultatoConverted, nota);
    }

    // -------------------------------------------------------------------------
    // USE CASE 4 – Completa sessione (con tutti i risultati già inseriti)
    // -------------------------------------------------------------------------

    /**
     * Segna la sessione come COMPLETATA.
     * Raccoglie risultati e note da una mappa idEsercizio → {risultatoRaw, nota}
     * passata dalla boundary (già compilata dall'utente nelle schede esercizi).
     *
     * @param idAtleta     id dell'atleta che ha completato la sessione
     * @param idSessione   id della sessione da completare
     * @param risultatiRaw mappa idEsercizio → stringa risultato grezza
     * @param note         mappa idEsercizio → nota testuale
     * @throws IllegalAccessException se l'atleta non è il proprietario della sessione
     */
    public void completaSessione(Long idAtleta,
                                 Long idSessione,
                                 HashMap<Long, String> risultatiRaw,
                                 HashMap<Long, String> note)
            throws IllegalAccessException {

        GestoreSessioni gs = GestoreSessioni.getInstance();
        SessioneDiAllenamento sessione = gs.getSessione(idSessione);

        // Prima registra tutti i risultati
        for (Esercizio e : sessione.getEsercizi()) {
            String rawVal = risultatiRaw.get(e.getId());
            String nota   = note.get(e.getId());

            if (rawVal != null && !rawVal.isBlank()) {
                Object convertito = parseRisultato(e.getTipo(), rawVal);
                e.setRisultato(convertito, nota != null ? nota : "");
            }
        }

        // Poi invoca il completamento sull'entity (che verifica proprietà)
        // GestoreSessioni.completaSessione si aspetta HashMap<Long,Risultato>:
        // qui facciamo una chiamata diretta al metodo setStato per semplicità,
        // rispecchiando la logica di accesso che già esiste nel GestoreSessioni.
        if (!sessione.getAtleta().getId().equals(idAtleta)) {
            throw new IllegalAccessException(
                    "L'atleta " + idAtleta + " non è il proprietario della sessione " + idSessione);
        }
        sessione.setStato("COMPLETATA");
    }

    // -------------------------------------------------------------------------
    // Metodi di supporto privati
    // -------------------------------------------------------------------------

    private SessioneSummaryDTO toSummaryDTO(SessioneDiAllenamento s) {
        String nomeAllenatore = s.getAllenatore() != null
                ? s.getAllenatore().getNome() + " " + s.getAllenatore().getCognome()
                : "—";
        return new SessioneSummaryDTO(
                s.getId(),
                s.getTitolo(),
                s.getDataSvolgimento() != null ? s.getDataSvolgimento().toString() : "—",
                s.getEsercizi().size(),
                nomeAllenatore,
                s.getStato().name()
        );
    }

    private EsercizioDettDTO toEsercizioDTO(Esercizio e) {
        // Risultato atteso come stringa leggibile
        String atteso;
        if (e.getTipo() == Esercizio.TipoEsercizio.RIPETIZIONI) {
            atteso = e.getRisultatoAtteso() + " rip.";
        } else {
            atteso = formatDuration((java.time.Duration) e.getRisultatoAtteso());
        }

        // Risultato ottenuto (null se non ancora registrato)
        String ottenuto = null;
        String nota     = null;
        if (e.getRisultato() != null) {
            Object r = e.getRisultato().getRisultato();
            ottenuto = (r instanceof java.time.Duration d)
                    ? formatDuration(d)
                    : r.toString();
            nota = e.getRisultato().getNota();
        }

        return new EsercizioDettDTO(
                e.getId(),
                e.getNome(),
                e.getDescrizione(),
                e.getTipo().name(),
                atteso,
                ottenuto,
                nota
        );
    }

    /**
     * Converte la stringa grezza inserita dall'utente nel tipo atteso
     * dall'entity (Integer per RIPETIZIONI, Duration per TEMPO).
     */
    private Object parseRisultato(Esercizio.TipoEsercizio tipo, String raw) {
        if (tipo == Esercizio.TipoEsercizio.RIPETIZIONI) {
            try {
                int val = Integer.parseInt(raw.trim());
                if (val <= 0) throw new NumberFormatException();
                return val;
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Inserire un numero intero positivo per le ripetizioni (ricevuto: \"" + raw + "\")");
            }
        } else {
            // Accetta HH:MM:SS oppure formato ISO-8601 PT…
            try {
                if (raw.trim().toUpperCase().startsWith("PT")) {
                    return java.time.Duration.parse(raw.trim());
                }
                // Formato HH:MM:SS
                String[] parts = raw.trim().split(":");
                if (parts.length == 3) {
                    long h = Long.parseLong(parts[0]);
                    long m = Long.parseLong(parts[1]);
                    long s = Long.parseLong(parts[2]);
                    return java.time.Duration.ofHours(h).plusMinutes(m).plusSeconds(s);
                }
                // Formato MM:SS
                if (parts.length == 2) {
                    long m = Long.parseLong(parts[0]);
                    long s = Long.parseLong(parts[1]);
                    return java.time.Duration.ofMinutes(m).plusSeconds(s);
                }
                throw new IllegalArgumentException();
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Formato tempo non valido. Usare HH:MM:SS oppure MM:SS (ricevuto: \"" + raw + "\")");
            }
        }
    }

    private String formatDuration(java.time.Duration d) {
        long h   = d.toHours();
        long min = d.toMinutesPart();
        long sec = d.toSecondsPart();
        return h > 0
                ? String.format("%d:%02d:%02d", h, min, sec)
                : String.format("%d:%02d", min, sec);
    }
}
