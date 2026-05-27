package entity;


import jakarta.persistence.*;
import java.util.HashSet;

@Entity
@Table(name = "Atleti")
public class Atleta extends Utente{

    private String disciplina;
    private int livello;

    // Obiettivo è un attributo opzionale tradotto come un attributo nullo di default
    private String obiettivo = null;

    @ManyToMany(mappedBy = "atleti")
    private HashSet<Allenatore> allenatori = new HashSet<Allenatore>();

    @OneToMany(mappedBy = "atleta")
    private HashSet<SessioneDiAllenamento> sessioni = new HashSet<>(); //G: Non sono sicuro lo debba avere

    public Atleta(){}

    public Atleta(String nome, String cognome, String mail, String password, String disciplina, int livello){
        super(nome, cognome, mail, password);
        this.disciplina = disciplina;
        this.livello = livello;
    }

    public Atleta(String nome, String cognome, String mail, String password, String disciplina, int livello, String obiettivo){
        super(nome, cognome, mail, password);
        this.disciplina = disciplina;
        this.livello = livello;
        this.obiettivo = obiettivo;
    }

    public String getObiettivo(){
        return this.obiettivo;
    }

    public String getDisciplina(){
        return this.disciplina;
    }

    public int getLivello(){
        return this.livello;
    }

    public void setObiettivo(String obiettivo){
        this.obiettivo = obiettivo;
    }

    public void setDisciplina(String disciplina){
        this.disciplina = disciplina;
    }

    public void setLivello(int livello){
        this.livello = livello;
    }

    public HashSet<Allenatore> getAllenatori(){
        return this.allenatori;
    }

    public void addAllenatore(Allenatore a){
        this.allenatori.add(a);
    }

    public HashSet<SessioneDiAllenamento> getSessioni(){
        return this.sessioni;
    }
}
