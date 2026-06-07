package entity;

import jakarta.persistence.*;

import java.time.Duration;

@Entity
@Table(name = "esercizi")
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;

    @Enumerated(EnumType.STRING)
    private TipoEsercizio tipo; // Enum semplice senza dati variabili

    @Embedded
    private RisultatoAtteso risultatoAtteso;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "risultato")
    private Risultato risultato = null;

    public Esercizio() {
    }

    // Costruttore per ripetizioni
    public Esercizio(String nome, String descrizione, int ripetizioni) throws IllegalArgumentException {
        this.nome = nome;
        this.descrizione = descrizione;
        this.tipo = TipoEsercizio.RIPETIZIONI;

        if (ripetizioni > 0)
            this.risultatoAtteso = new RisultatoAtteso(ripetizioni);
        else
            throw new IllegalArgumentException("Inserito un numero di ripetzioni negativo");

    }

    // Costruttore per tempo (Duration)
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

    // Ragionare se fargli ritornare una stringa o tradurre l'intero dopo e se ha senso il defoult a null
    public TipoEsercizio getTipo() {
        return this.tipo;
    }

    public Object getRisultatoAtteso() {
        if (tipo == TipoEsercizio.RIPETIZIONI)
            return this.risultatoAtteso.getRipetizioni();
        else
            return this.risultatoAtteso.getDurata();
    }


    public Risultato getRisultato() {
        return risultato;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setTipo(int tipo) {
        if (tipo == 0)
            this.tipo = TipoEsercizio.RIPETIZIONI;
        else if (tipo == 1)
            this.tipo = TipoEsercizio.TEMPO;
    }

    public void setRisultatoAtteso(Object risultatoAtteso) throws IllegalArgumentException {

        boolean esito = false;
        if (this.tipo == TipoEsercizio.RIPETIZIONI && risultatoAtteso instanceof Integer)
            esito = this.risultatoAtteso.setRisultatoAtteso(risultatoAtteso);
        else if (this.tipo == TipoEsercizio.TEMPO && risultatoAtteso instanceof Duration)
            esito = this.risultatoAtteso.setRisultatoAtteso(risultatoAtteso);

        if (!esito)
            throw new IllegalArgumentException("Risultato non valido");
    }


    public void setRisultato(Object risultato, String nota) throws IllegalArgumentException {
        if (this.tipo == TipoEsercizio.TEMPO && risultato instanceof Duration) {
            this.risultato = new RisultatoTempo(nota, (Duration) risultato);

        } else if (this.tipo == TipoEsercizio.RIPETIZIONI && risultato instanceof Integer) {
            this.risultato = new RisultatoRipetizioni(nota, (Integer) risultato);
        } else
            throw new IllegalArgumentException("Tipo di risultato non valido");
    }

    public void setRisultato(String nota) throws IllegalArgumentException {
        if (this.tipo == TipoEsercizio.TEMPO) {
            this.risultato = new RisultatoTempo(nota, null);

        } else if (this.tipo == TipoEsercizio.RIPETIZIONI) {
            this.risultato = new RisultatoRipetizioni(nota, null);
        } else
            throw new IllegalArgumentException("Tipo di risultato non valido");
    }

    // Embeddable per memorizzare il risultato atteso in modo polimorfico
    @Embeddable
    static class RisultatoAtteso {
        private Integer ripetizioni;
        private Duration durata; // JPA può mappare Duration come stringa ISO-8601 con @Convert o usando un AttributeConverter

        public RisultatoAtteso() {
            this.ripetizioni = null;
            this.durata = null;
        }

        // Costruttore per ripetizioni
        public RisultatoAtteso(int ripetizioni) {
            this.ripetizioni = ripetizioni;
            this.durata = null;
        }

        // Costruttore per durata
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

        public boolean setRisultatoAtteso(Object risultato) {
            if (risultato instanceof Integer && this.durata == null)
                this.ripetizioni = (Integer) risultato;
            else if (risultato instanceof Duration && this.ripetizioni == null)
                this.durata = (Duration) risultato;
            else
                return false;
            return true;
        }
    }
}