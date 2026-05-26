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
    public SessioneDiAllenamento(String titolo, String descrizione, LocalDate dataSvolgimento, Atleta atleta, Allenatore allenatore){

        this.titolo = titolo;
        this.dataSvolgimento = dataSvolgimento;
        this.descrizione = descrizione;
        this.stato = Stato.ASSEGNATA;
        this.setAtleta(atleta);
        this.setAllenatore(allenatore);
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

    public void setStato(String stato) throws IllegalArgumentException{
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
                throw new IllegalArgumentException("Stato non valido");
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

    /**
     * PRECONDITION: LA LISTA DEI RISULTATI DEVE MATCHARE I TIPI DEGLI ESERCIZI IN ORDINE NELLA LISTA
     * @param risultati lista di risultati ottenuti
     *
     **/

    public void registraRisultati(Object[] risultati) throws IllegalArgumentException{
        int i = 0;
        if(this.esercizi.size() != risultati.length)
            throw new IllegalArgumentException("Il numero di risultati non combacia con il numero degli esercizi");

        for(Esercizio esercizio : this.esercizi){

            if((esercizio.getTipo() == TipoEsercizio.RIPETIZIONI && (risultati[i] instanceof  RisultatoRipetizioni )) ||
                    (esercizio.getTipo() == TipoEsercizio.TEMPO && (risultati[i] instanceof  RisultatoTempo ))
            ){
                esercizio.setRisultato(risultati[i]);
                i++;
            }
            else
                throw new IllegalArgumentException("Il tipo dei risultati non combacia con il tipo degli esercizi");
        }

    }

    public enum Stato {
        ASSEGNATA,
        IN_CORSO,
        COMPLETATA
    }
}
