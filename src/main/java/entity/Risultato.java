package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public abstract class Risultato {
//commento try
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nota;

    public Risultato(){}

    public Risultato(String nota){
        this.nota = nota;
    }


    public String getNota(){
        return this.nota;
    }

    public void setNota(String nota){
        this.nota = nota;
    }

    public abstract int getRisultato();

    public abstract  void setRisultato(int risultato);



}
