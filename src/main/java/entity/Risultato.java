package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "risultati")
public abstract class Risultato {

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

    public abstract Object getRisultato();

    public abstract  void setRisultato(Object risultato);



}
