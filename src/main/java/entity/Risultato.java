package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public abstract class Risultato<T> {

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

    public abstract T getRisultato();

    public abstract  void setRisultato(T risultato);



}
