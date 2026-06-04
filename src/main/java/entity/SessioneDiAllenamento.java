package entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessioni")
public class SessioneDiAllenamento implements Comparable{

    //Chiave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Attributi di classe
    private String titolo;
    private String descrizione;
    private LocalDate dataSvolgimento;
    private StatoSessione stato;
    private Duration durata = null;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(name = "sessione_esercizio",   // nome della tabella associativa
            joinColumns = @JoinColumn(name = "sessione_id"),
            inverseJoinColumns = @JoinColumn(name = "esercizio_id"))
    private List<Esercizio> esercizi = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "atleta_id")   // chiave esterna verso Atleti
    private Atleta atleta;

    @ManyToOne
    @JoinColumn(name = "allenatore_id") // chiave esterna verso Allenatori
    private Allenatore allenatore;

    /**
     * Costruttore vuoto dell'oggetto SessioneDiAllenamento
     * */
    public SessioneDiAllenamento(){}

    /**
     * Costruttore vuoto dell'oggetto SessioneDiAllenamento
     * @param titolo Titolo
     * @param descrizione Descrizione
     * @param dataSvolgimento Data di Svolgimento della sessione
     * @param atleta Atleta a cui la sessione è assegnata
     * @param allenatore Allenatore che crea la sessione
     * */
    public SessioneDiAllenamento(String titolo, String descrizione, LocalDate dataSvolgimento, Atleta atleta, Allenatore allenatore){

        this.titolo = titolo;
        this.dataSvolgimento = dataSvolgimento;
        this.descrizione = descrizione;
        this.stato = StatoSessione.ASSEGNATA;
        this.setAtleta(atleta);
        this.setAllenatore(allenatore);
    }

    /**
     * Costruttore vuoto dell'oggetto SessioneDiAllenamento (di cui si vuole specificare la durata)
     * @param titolo Titolo
     * @param descrizione Descrizione
     * @param dataSvolgimento Data di Svolgimento della sessione
     * @param atleta Atleta a cui la sessione è assegnata
     * @param allenatore Allenatore che crea la sessione
     * @param durata Durata della Sessione
     * */
    public SessioneDiAllenamento(String titolo, String descrizione, LocalDate dataSvolgimento, Duration durata, Atleta atleta, Allenatore allenatore){

        this.titolo = titolo;
        this.dataSvolgimento = dataSvolgimento;
        this.descrizione = descrizione;
        this.stato = StatoSessione.ASSEGNATA;
        this.durata = durata;
        this.setAtleta(atleta);
        this.setAllenatore(allenatore);
    }


    /**
     *Getter Titolo
     * @return Titolo della Sessione
     * */
    public String getTitolo() {
        return titolo;
    }

    /**
     *Getter Descrizione
     * @return Descrizione della Sessione
     * */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     *Getter DataSvolgimento
     * @return DataSvolgimento della Sessione
     * */
    public LocalDate getDataSvolgimento() {
        return dataSvolgimento;
    }

    /**
     *Getter Id
     * @return Id della Sessione
     * */
    public Long getId() {
        return id;
    }

    /**
     *Getter Atleta
     * @return Atleta a cui è assegnata Sessione
     * */
    public Atleta getAtleta(){ return atleta; }

    /**
     *Getter Allenatore
     * @return Allenatore creatore della Sessione
     * */
    public Allenatore getAllenatore(){ return allenatore; }

    /**
     *Getter Stato
     * @return Stato della Sessione
     * */
    public StatoSessione getStato(){ return stato; }

    /**
     *Getter Durata
     * @return Durata della Sessione
     * */
    public Duration getDurata(){ return durata; }

    /**
     * Setter Titolo
     * @param titolo Titolo
     * */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Setter Descrizione
     * @param descrizione Descrizione
     * */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Setter DataSvolgimento
     * @param dataSvolgimento Data di Svolgimento
     * */
    public void setDataSvolgimento(LocalDate dataSvolgimento) {
        this.dataSvolgimento = dataSvolgimento;
    }

    /**
     * Setter Durata
     * @param durata Durata
     * */
    public void setDurata(Duration durata) {
        this.durata = durata;
    }

    /**
     * Setter Stato
     * @param stato Stato
     * */
    public void setStato(String stato) throws IllegalArgumentException{
        switch (stato){
            case "COMPLETATA":
                this.stato = StatoSessione.COMPLETATA;
                break;
            case "IN CORSO":
                this.stato = StatoSessione.IN_CORSO;
                break;
            case "ASSEGNATA":
                this.stato = StatoSessione.ASSEGNATA;
                break;
            default:
                throw new IllegalArgumentException("Stato non valido");
        }
    }

    /**
     * Setter Atleta
     * @param atleta Atleta
     * */
    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
        atleta.getSessioni().add(this);
    }

    /**
     * Setter Allenatore
     * @param allenatore Allenatore
     * */
    public void setAllenatore(Allenatore allenatore) {
        this.allenatore = allenatore;
        allenatore.getSessioni().add(this);
    }

    /**
     * Getter Esercizi
     * @return Lista degli esercizi che compongono la Sessione
     * */
    public List<Esercizio> getEsercizi() {
        return esercizi;
    }

    /**
     * Setter Esercizi
     * @param esercizi Lista di Esercizi
     * */
    public void setEsercizi(List<Esercizio> esercizi) {
        this.esercizi = esercizi;
    }


    /**
     * Registra il Risultato di un Esercizio
     * @param id_esercizio Id Esercizio
     * @param nota Nota
     * @param risultato Risultato (Integer / Duration)
     * */
    public void registraRisultato(Object risultato, String nota, Long id_esercizio) throws IllegalArgumentException, ClassCastException{
        for(Esercizio e : esercizi){
            if(e.getId().equals(id_esercizio)){
                if (risultato ==null)
                    e.setRisultato(nota);
                else
                    e.setRisultato(risultato, nota);
                return;
            }
        }

        throw new IllegalArgumentException("Esercizio non trovato");
    }

    public Esercizio getEsercizioPerId(Long id_esercizio){

        for(Esercizio e : this.esercizi){
            if(e.getId().equals(id_esercizio))
                return e;
        }
        return null;

    }

    @Override
    public int compareTo(Object o) {
        SessioneDiAllenamento other_session = (SessioneDiAllenamento) o;
        if(dataSvolgimento.compareTo(other_session.getDataSvolgimento()) == 0){
            return titolo.compareTo(other_session.getTitolo());
        }else{
            return dataSvolgimento.compareTo(other_session.getDataSvolgimento());
        }
    }
}
