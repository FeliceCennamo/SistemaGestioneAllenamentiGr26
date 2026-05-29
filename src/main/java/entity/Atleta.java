package entity;


import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Atleti")
public class Atleta extends Utente{

    private String disciplina;
    private int livello;

    // Obiettivo è un attributo opzionale tradotto come un attributo nullo di default
    private String obiettivo = null;

    @ManyToMany(mappedBy = "atleti")
    private Set<Allenatore> allenatori = new HashSet<Allenatore>();

    @OneToMany(mappedBy = "atleta")
    private Set<SessioneDiAllenamento> sessioni = new HashSet<>(); //G: Non sono sicuro lo debba avere

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

    /**
     * Getter Obiettivo
     * @return Obiettivo
     * */
    public String getObiettivo(){
        return this.obiettivo;
    }

    /**
     * Getter Disciplina
     * @return Disciplina
     * */
    public String getDisciplina(){
        return this.disciplina;
    }

    /**
     * Getter Livello
     * @return Livello
     * */
    public int getLivello(){
        return this.livello;
    }

    /**
     * Setter Obiettivo
     * @param obiettivo Obiettivo
     * */
    public void setObiettivo(String obiettivo){
        this.obiettivo = obiettivo;
    }

    /**
     * Setter Disciplina
     * @param disciplina Disciplina
     * */
    public void setDisciplina(String disciplina){
        this.disciplina = disciplina;
    }

    /**
     * Setter Livello
     * @param livello Livello
     * */
    public void setLivello(int livello){
        this.livello = livello;
    }

    /**
     * Getter Allenatori
     * @return Lista Allenatori
     * */
    public Set<Allenatore> getAllenatori(){
        return this.allenatori;
    }

    /**
     * Aggiunge un Allenatore alla lista di Allenatori dell'atleta
     * @param allenatore Allenatore da aggiungere alla lista
     * */
    public void addAllenatore(Allenatore allenatore){
        this.allenatori.add(allenatore);
    }

    /**
     * Getter Sessioni
     * @return Lista di Sessioni assegnate all'Atleta
     * */
    public Set<SessioneDiAllenamento> getSessioni(){
        return this.sessioni;
    }
}
