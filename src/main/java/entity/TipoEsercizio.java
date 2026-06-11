package entity;

/**
 * Tipologia di un esercizio, determina la natura del risultato atteso
 * e del risultato effettivo.
 * <ul>
 *   <li>{@link #RIPETIZIONI} – esercizio basato sul conteggio</li>
 *   <li>{@link #TEMPO} – esercizio basato sulla durata temporale</li>
 * </ul>
 */
public enum TipoEsercizio {
    /** Esercizio il cui risultato è espresso in numero di ripetizioni. */
    RIPETIZIONI,
    /** Esercizio il cui risultato è espresso come durata temporale. */
    TEMPO
}