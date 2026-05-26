package entity;

import jakarta.persistence.*;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SessioneDiAllenamento {

    //Chiave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Attributi di classe
    private String titolo;
    private String descrizione;
    private LocalDate dataSvolgimento;
    private Stato stato;

    @OneToMany(mappedBy = "sessione", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Esercizio> esercizi = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "atleta_id")   // chiave esterna verso Atleti
    private Atleta atleta;

    @ManyToOne
    @JoinColumn(name = "allenatore_id") // chiave esterna verso Allenatori
    private Allenatore allenatore;

    //costruttore vuoto
    public SessioneDiAllenamento(){}

    //costruttore con tutti gli attributi
    public SessioneDiAllenamento(String titolo, String descrizione, LocalDate dataSvolgimento){

        this.titolo = titolo;
        this.dataSvolgimento = dataSvolgimento;
        this.descrizione = descrizione;
        this.stato = Stato.ASSEGNATA;
    }


    //Metodi get
    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDate getDataSvolgimento() {
        return dataSvolgimento;
    }

    public Long getId() {
        return id;
    }

    public Atleta getAtleta(){ return atleta; }

    public Allenatore getAllenatore(){ return allenatore; }

    public Stato getStato(){ return stato; }

    //Metodi set
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setDataSvolgimento(LocalDate dataSvolgimento) {
        this.dataSvolgimento = dataSvolgimento;
    }

    public void setStato(String stato){
        switch (stato){
            case "COMPLETATA":
                this.stato = Stato.COMPLETATA;
                break;
            case "IN CORSO":
                this.stato = Stato.IN_CORSO;
                break;
            case "ASSEGNATA":
                this.stato = Stato.ASSEGNATA;
                break;
            default:
                //no action
        }
    }

    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
        atleta.getSessioni().add(this);
    }

    public void setAllenatore(Allenatore allenatore) {
        this.allenatore = allenatore;
        allenatore.getSessioni().add(this);
    }

    public List<Esercizio> getEsercizi() {
        return esercizi;
    }

    public void setEsercizi(List<Esercizio> esercizi) {
        this.esercizi = esercizi;
    }

    // Perchè sta roba non ci piace?
    /*
    void addEsercizio(String nome, String descrizione, boolean tipologia, int ris_atteso){
        // Eventualmente controlli
        this.esercizi.add(new Esercizio(nome, descrizione, tipologia, ris_atteso));
    }*/


    // Qua bisogna ragionare su come fargli arrivare i risultati, va bene anche un listone ma forse meglio una sorta di dizionario dato che gli esercizi di una stessa sessione vogliono pèiù tipi
    public void registraRisultati(){}

    public enum Stato {
        ASSEGNATA,
        IN_CORSO,
        COMPLETATA
    }

}
