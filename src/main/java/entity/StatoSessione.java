package entity;

/**
 * Rappresenta lo stato in cui si trova una sessione di allenamento.
 */
public enum StatoSessione {
    /** La sessione è stata creata e assegnata all'atleta, ma non ancora iniziata. */
    ASSEGNATA,
    /** L'atleta ha iniziato la sessione e sta registrando i risultati. */
    IN_CORSO,
    /** Tutti gli esercizi della sessione sono stati completati con un risultato. */
    COMPLETATA
}