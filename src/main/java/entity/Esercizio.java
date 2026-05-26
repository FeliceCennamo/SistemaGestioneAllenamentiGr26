package entity;

import jakarta.persistence.*;
import java.time.Duration;

@Entity
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

    @ManyToOne
    @JoinColumn(name = "sessione_id") // Nome colonna FK verso SessioneDiAllenamento
    private SessioneDiAllenamento sessione;

    public Esercizio() {}

    // Costruttore per ripetizioni
    public Esercizio(String nome, String descrizione, int ripetizioni) throws IllegalArgumentException{
        this.nome = nome;
        this.descrizione = descrizione;
        this.tipo = TipoEsercizio.RIPETIZIONI;

        if(ripetizioni > 0)
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
    public Integer getTipo() {
        return switch (this.tipo) {
            case RIPETIZIONI -> 0;
            case TEMPO -> 1;
            default -> null;
        };
    }

    public Object getRisultatoAtteso() {
        if(tipo == TipoEsercizio.RIPETIZIONI)
            return this.risultatoAtteso.getRipetizioni();
        else
            return this.risultatoAtteso.getDurata();
    }

    public SessioneDiAllenamento getSessione() {
        return sessione;
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
        else if (tipo==1)
            this.tipo = TipoEsercizio.TEMPO;
    }

    public void setRisultatoAtteso(Object risultatoAtteso) {
        if (this.tipo==TipoEsercizio.RIPETIZIONI && risultatoAtteso instanceof Integer)
            this.risultatoAtteso.setRipetizioni((Integer) risultatoAtteso);
        else if (this.tipo==TipoEsercizio.TEMPO && risultatoAtteso instanceof Duration)
            this.risultatoAtteso.setDurata((Duration) risultatoAtteso);
        else
            throw new IllegalArgumentException("Tipo di risultato non valido");
    }

    public void setSessione(SessioneDiAllenamento sessione) {
        this.sessione = sessione;
    }
}

// Enum semplice
enum TipoEsercizio {
    RIPETIZIONI,
    TEMPO
}

// Embeddable per memorizzare il risultato atteso in modo polimorfico
@Embeddable
class RisultatoAtteso {
    private Integer ripetizioni;
    private Duration durata; // JPA può mappare Duration come stringa ISO-8601 con @Convert o usando un AttributeConverter

    public RisultatoAtteso() {}

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

    public void setRipetizioni(Integer ripetizioni) {
        this.ripetizioni = ripetizioni;
    }

    public Duration getDurata() {
        return durata;
    }

    public void setDurata(Duration durata) {
        this.durata = durata;
    }
}