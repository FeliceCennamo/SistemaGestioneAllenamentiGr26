package entity;

import jakarta.persistence.*;

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
    private String dataSvolgimento;

    @OneToMany(mappedBy = "sessione")
    private List<Esercizio> esercizi = new ArrayList<>();


    //Metodi get
    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getDataSvolgimento() {
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

    public void setDataSvolgimento(String dataSvolgimento) {
        this.dataSvolgimento = dataSvolgimento;
    }

    //costruttore vuoto
    public SessioneDiAllenamento(){}

    //costruttore con tutti gli attributi
    public SessioneDiAllenamento(String titolo, String descrizione, String dataSvolgimento){

        this.titolo = titolo;
        this.dataSvolgimento = dataSvolgimento;
        this.descrizione = descrizione;
    }


}
