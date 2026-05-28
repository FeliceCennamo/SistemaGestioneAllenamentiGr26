package controller;

import entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Controller BCED che fa da intermediario tra il boundary (GUI Swing)
 * e i gestori del livello entity (GestoreSessioni, GestoreUtenti).
 *
 * Espone DTO semplici alla GUI per evitare accoppiamento diretto con le entity.
 */
public class AtletaSessioniController {

    private final GestoreSessioni gestoreSessioni;
    private final GestoreUtenti gestoreUtenti;

    public AtletaSessioniController() {
        this.gestoreSessioni = GestoreSessioni.getInstance();
        this.gestoreUtenti   = GestoreUtenti.getInstance();
    }

    // -------------------------------------------------------------------------
    // DTO interni – la GUI lavora solo con questi, mai con le entity direttamente
    // -------------------------------------------------------------------------

    /** Rappresentazione leggera di una sessione per la lista. */
    public record SessioneDTO(
            Long   id,
            String titolo,
            String descrizione,
            String dataSvolgimento,
            String stato,
            String durata
    ) {}

    /** Rappresentazione leggera di un esercizio per il pannello di dettaglio. */
    public record EsercizioDTO(
            Long   id,
            String nome,
            String descrizione,
            String tipo,
            String risultatoAtteso,
            String risultatoEffettivo,   // null se non ancora completata
            String nota                  // null se non ancora completata
    ) {}

    // -------------------------------------------------------------------------
    // Metodi esposti alla GUI
    // -------------------------------------------------------------------------

    /**
     * Restituisce la lista delle sessioni associate all'atleta con l'id dato.
     *
     * @param idAtleta id dell'atleta
     * @return lista di SessioneDTO ordinata per data (più recente prima)
     */
    public List<SessioneDTO> getSessioniAtleta(Long idAtleta) {
        Set<SessioneDiAllenamento> sessioni = gestoreSessioni.cercaSessioni(idAtleta);

        return sessioni.stream()
                .sorted((a, b) -> b.getDataSvolgimento().compareTo(a.getDataSvolgimento()))
                .map(this::toSessioneDTO)
                .toList();
    }

    /**
     * Restituisce la lista degli esercizi di una sessione specifica.
     *
     * @param idSessione id della sessione
     * @return lista di EsercizioDTO
     */
    public List<EsercizioDTO> getEserciziSessione(Long idSessione) {
        List<Esercizio> esercizi = gestoreSessioni.dettaglioSessione(idSessione);
        List<EsercizioDTO> dtos = new ArrayList<>();
        for (Esercizio e : esercizi) {
            dtos.add(toEsercizioDTO(e));
        }
        return dtos;
    }

    // -------------------------------------------------------------------------
    // Conversioni entity → DTO (private)
    // -------------------------------------------------------------------------

    private SessioneDTO toSessioneDTO(SessioneDiAllenamento s) {
        String durata = s.getDurata() != null
                ? formatDurata(s.getDurata().toMinutes())
                : "—";

        return new SessioneDTO(
                s.getId(),
                s.getTitolo(),
                s.getDescrizione(),
                s.getDataSvolgimento().toString(),
                s.getStato().name(),
                durata
        );
    }

    private EsercizioDTO toEsercizioDTO(Esercizio e) {
        // Risultato atteso
        String atteso;
        if (e.getTipo() == Esercizio.TipoEsercizio.RIPETIZIONI) {
            atteso = e.getRisultatoAtteso() + " rip.";
        } else {
            java.time.Duration d = (java.time.Duration) e.getRisultatoAtteso();
            atteso = formatDurata(d.toMinutes());
        }

        // Risultato effettivo (può essere null)
        String effettivo = null;
        String nota      = null;
        if (e.getRisultato() != null) {
            Risultato r = e.getRisultato();
            nota = r.getNota();
            if (r instanceof RisultatoRipetizioni rr) {
                effettivo = rr.getRisultato() + " rip.";
            } else if (r instanceof RisultatoTempo rt) {
                effettivo = formatDurata(rt.getRisultato().toMinutes());
            }
        }

        return new EsercizioDTO(
                e.getId(),
                e.getNome(),
                e.getDescrizione(),
                e.getTipo().name(),
                atteso,
                effettivo,
                nota
        );
    }

    private String formatDurata(long minuti) {
        if (minuti < 60) return minuti + " min";
        return (minuti / 60) + "h " + (minuti % 60) + "min";
    }
}
