package entity;

import jakarta.persistence.*;

import java.time.Duration;

/**
 * Rappresenta un esercizio fisico, caratterizzato da un tipo
 * (a ripetizioni o a tempo), un risultato atteso e un risultato
 * effettivo opzionale.
 * <p>
 * La logica di gestione del risultato atteso è incapsulata
 * nell'embeddable {@link RisultatoAtteso}, mentre il risultato
 * effettivo viene modellato tramite un'entità separata
 * {@link Risultato} (con sottoclassi specializzate).
 * </p>
 */
@Entity
@Table(name = "esercizi")
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;

    /**
     * Tipologia di esercizio: a ripetizioni o a tempo.
     * Mappato come stringa nel database.
     */
    @Enumerated(EnumType.STRING)
    private TipoEsercizio tipo;

    /**
     * Valore target dell'esercizio (es. 15 ripetizioni o 30 minuti).
     */
    @Embedded
    private RisultatoAtteso risultatoAtteso;

    /**
     * Risultato effettivamente conseguito durante una sessione.
     * Relazione uno-a-uno con cascade completo.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "risultato")
    private Risultato risultato = null;

    /**
     * Costruttore di default obbligatorio per JPA.
     */
    public Esercizio() {
    }

    /**
     * Crea un esercizio di tipo ripetizioni con il risultato atteso
     * specificato.
     *
     * @param nome        nome dell'esercizio
     * @param descrizione breve descrizione
     * @param ripetizioni numero di ripetizioni target (deve essere positivo)
     * @throws IllegalArgumentException se {@code ripetizioni} è minore o uguale a zero
     */
    public Esercizio(String nome, String descrizione, int ripetizioni) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.tipo = TipoEsercizio.RIPETIZIONI;

        if (ripetizioni > 0) {
            this.risultatoAtteso = new RisultatoAtteso(ripetizioni);
        } else {
            throw new IllegalArgumentException("Il numero di ripetizioni deve essere positivo");
        }
    }

    /**
     * Crea un esercizio di tipo tempo con il risultato atteso espresso
     * come durata.
     *
     * @param nome        nome dell'esercizio
     * @param descrizione breve descrizione
     * @param durata      durata target (es. 30 minuti)
     */
    public Esercizio(String nome, String descrizione, Duration durata) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.tipo = TipoEsercizio.TEMPO;
        this.risultatoAtteso = new RisultatoAtteso(durata);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Restituisce il tipo di esercizio.
     *
     * @return {@link TipoEsercizio#RIPETIZIONI} o {@link TipoEsercizio#TEMPO}
     */
    public TipoEsercizio getTipo() {
        return tipo;
    }

    /**
     * Restituisce il risultato atteso sotto forma di oggetto generico.
     * <p>
     * Il tipo concreto dipende dal {@link #tipo}:
     * <ul>
     *   <li>{@link TipoEsercizio#RIPETIZIONI} → {@link Integer}</li>
     *   <li>{@link TipoEsercizio#TEMPO} → {@link Duration}</li>
     * </ul>
     *
     * @return il valore atteso, oppure {@code null} se non impostato
     */
    public Object getRisultatoAtteso() {
        if (tipo == TipoEsercizio.RIPETIZIONI) {
            return this.risultatoAtteso.getRipetizioni();
        } else {
            return this.risultatoAtteso.getDurata();
        }
    }

    /**
     * Restituisce il risultato effettivo registrato per questo esercizio.
     *
     * @return l'entità {@link Risultato} associata, oppure {@code null}
     */
    public Risultato getRisultato() {
        return risultato;
    }

    protected void setNome(String nome) {
        this.nome = nome;
    }

    protected void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Imposta il tipo di esercizio a partire da un intero.
     *
     * @param tipo 0 per {@link TipoEsercizio#RIPETIZIONI}, 1 per {@link TipoEsercizio#TEMPO}
     * @throws IllegalArgumentException se il valore non è 0 o 1
     */
    protected void setTipo(int tipo) {
        if (tipo == 0) {
            this.tipo = TipoEsercizio.RIPETIZIONI;
        } else if (tipo == 1) {
            this.tipo = TipoEsercizio.TEMPO;
        } else {
            throw new IllegalArgumentException("Valori accettati solo 0 e 1");
        }
    }

    /**
     * Modifica il risultato atteso, adattandolo al tipo di esercizio.
     *
     * @param risultatoAtteso oggetto {@link Integer} per ripetizioni,
     *                        o {@link Duration} per tempo
     * @throws IllegalArgumentException se il tipo di dato non corrisponde
     *                                  al tipo di esercizio corrente o se
     *                                  l'aggiornamento fallisce
     */
    protected void setRisultatoAtteso(Object risultatoAtteso) {
        boolean successo = false;
        if (this.tipo == TipoEsercizio.RIPETIZIONI && risultatoAtteso instanceof Integer) {
            successo = this.risultatoAtteso.setRisultatoAtteso(risultatoAtteso);
        } else if (this.tipo == TipoEsercizio.TEMPO && risultatoAtteso instanceof Duration) {
            successo = this.risultatoAtteso.setRisultatoAtteso(risultatoAtteso);
        }

        if (!successo) {
            throw new IllegalArgumentException("Risultato atteso non valido per il tipo di esercizio");
        }
    }

    /**
     * Registra il risultato effettivo dell'esercizio comprensivo di valore e nota.
     *
     * @param risultato valore ottenuto: {@link Duration} per esercizi a tempo,
     *                  {@link Integer} per ripetizioni
     * @param nota      eventuale annotazione testuale
     * @throws IllegalArgumentException se il tipo di dato non corrisponde al tipo esercizio
     */
    public void setRisultato(Object risultato, String nota) {
        if (this.tipo == TipoEsercizio.TEMPO && risultato instanceof Duration) {
            this.risultato = new RisultatoTempo(nota, (Duration) risultato);
        } else if (this.tipo == TipoEsercizio.RIPETIZIONI && risultato instanceof Integer) {
            this.risultato = new RisultatoRipetizioni(nota, (Integer) risultato);
        } else {
            throw new IllegalArgumentException("Tipo di risultato non valido per questo esercizio");
        }
    }

    /**
     * Classe embeddable che memorizza il risultato atteso in modo polimorfico:
     * può contenere un numero di ripetizioni o una durata.
     */
    @Embeddable
    static class RisultatoAtteso {
        private Integer ripetizioni;
        private Duration durata; // Convertito in stringa ISO-8601 tramite AttributeConverter globale

        /**
         * Costruttore vuoto per JPA.
         */
        public RisultatoAtteso() {
            this.ripetizioni = null;
            this.durata = null;
        }

        /**
         * Crea un risultato atteso basato su ripetizioni.
         *
         * @param ripetizioni numero target di ripetizioni
         */
        public RisultatoAtteso(int ripetizioni) {
            this.ripetizioni = ripetizioni;
            this.durata = null;
        }

        /**
         * Crea un risultato atteso basato su durata.
         *
         * @param durata durata target
         */
        public RisultatoAtteso(Duration durata) {
            this.durata = durata;
            this.ripetizioni = null;
        }

        public Integer getRipetizioni() {
            return ripetizioni;
        }

        public Duration getDurata() {
            return durata;
        }

        /**
         * Modifica il valore atteso, accettando solo il tipo coerente
         * con lo stato corrente dell'oggetto.
         *
         * @param risultato {@link Integer} se l'oggetto è stato creato per ripetizioni,
         *                  {@link Duration} se per tempo
         * @return {@code true} se l'aggiornamento è riuscito, {@code false} altrimenti
         */
        public boolean setRisultatoAtteso(Object risultato) {
            if (risultato instanceof Integer && this.durata == null) {
                this.ripetizioni = (Integer) risultato;
                return true;
            } else if (risultato instanceof Duration && this.ripetizioni == null) {
                this.durata = (Duration) risultato;
                return true;
            }
            return false;
        }
    }
}