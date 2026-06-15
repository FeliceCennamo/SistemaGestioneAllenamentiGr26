package entity;

import jakarta.persistence.*;

/**
 * Rappresenta il risultato conseguito in un esercizio durante una sessione
 * di allenamento.
 * <p>
 * Classe astratta base per la strategia di ereditarietà JPA; le sottoclassi
 * concrete ({@code RisultatoRipetizioni}, {@code RisultatoTempo}) definiscono
 * il tipo specifico del valore restituito da {@link #getRisultato()}.
 * </p>
 */
@Entity
@Table(name = "risultati")
public abstract class Risultato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Annotazione testuale opzionale sul risultato.
     */
    @Column(name = "nota", nullable = true)
    private String nota;

    /**
     * Costruttore di default richiesto da JPA.
     */
    public Risultato() {
    }

    /**
     * Crea un risultato con la nota specificata.
     *
     * @param nota testo descrittivo (può essere {@code null})
     */
    protected Risultato(String nota) {
        this.nota = nota;
    }

    /**
     * Restituisce la nota associata a questo risultato.
     *
     * @return la nota, oppure {@code null} se assente
     */
    protected String getNota() {
        return nota;
    }

    /**
     * Imposta la nota per questo risultato.
     *
     * @param nota il testo della nota
     */
    protected void setNota(String nota) {
        this.nota = nota;
    }

    /**
     * Restituisce il valore concreto del risultato, il cui tipo dipende
     * dalla sottoclasse (ad esempio {@link Integer} per ripetizioni,
     * {@link java.time.Duration} per tempo).
     *
     * @return il valore del risultato
     */
    protected abstract Object getRisultato();

    /**
     * Imposta il valore concreto del risultato.
     *
     * @param risultato oggetto del tipo atteso dalla sottoclasse
     */
    protected abstract void setRisultato(Object risultato);

    public Long getId() {
        return id;
    }
}