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

    @OneToMany(mappedBy = "sessione", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Esercizio> esercizi = new ArrayList<>();


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

    //costruttore vuoto
    public SessioneDiAllenamento(){}

    //costruttore con tutti gli attributi
    public SessioneDiAllenamento(String titolo, String descrizione, LocalDate dataSvolgimento){

        this.titolo = titolo;
        this.dataSvolgimento = dataSvolgimento;
        this.descrizione = descrizione;
    }

    /*
    void addEsercizio(String nome, String descrizione, boolean tipologia, int ris_atteso){
        // Eventualmente controlli
        this.esercizi.add(new Esercizio(nome, descrizione, tipologia, ris_atteso));
    }*/



}
