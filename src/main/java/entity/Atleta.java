package entity;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "atleti")
public class Atleta extends Utente {

    private String disciplina;
    private int livello;

    @ElementCollection
    @CollectionTable(name = "atleta_obiettivo",
            joinColumns = @JoinColumn(name = "atleta_id"))
    @Column(name = "obiettivo")
    private Set<String> obiettivi = null;

    @ManyToMany(mappedBy = "atleti")
    private final Set<Allenatore> allenatori = new HashSet<Allenatore>();

    @OneToMany(mappedBy = "atleta")
    private final Set<SessioneDiAllenamento> sessioni = new TreeSet<>(); //G: Non sono sicuro lo debba avere

    public Atleta() {
    }

    protected Atleta(String nome, String cognome, String mail, String password, String disciplina, int livello) {
        super(nome, cognome, mail, password);
        this.disciplina = disciplina;
        this.livello = livello;
    }

    protected Atleta(String nome, String cognome, String mail, String password, String disciplina, int livello, Set<String> obiettivi) {
        super(nome, cognome, mail, password);
        this.disciplina = disciplina;
        this.livello = livello;
        this.obiettivi = obiettivi;
    }

    /**
     * Getter Obiettivi
     *
     * @return Lista di Obiettivi
     *
     */
    protected Set<String> getObiettivo() {
        return this.obiettivi;
    }

    /**
     * Getter Disciplina
     *
     * @return Disciplina
     *
     */
    public String getDisciplina() {
        return this.disciplina;
    }

    /**
     * Getter Livello
     *
     * @return Livello
     *
     */
    public int getLivello() {
        return this.livello;
    }

    /**
     * Setter Obiettivi
     *
     * @param obiettivi Lista di Obiettivi
     *
     */
    protected void setObiettivo(Set<String> obiettivi) {
        this.obiettivi = obiettivi;
    }

    /**
     * Setter Disciplina
     *
     * @param disciplina Disciplina
     *
     */
    protected void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    /**
     * Setter Livello
     *
     * @param livello Livello
     *
     */
    protected void setLivello(int livello) {
        this.livello = livello;
    }

    /**
     * Getter Allenatori
     *
     * @return Lista Allenatori
     *
     */
    protected Set<Allenatore> getAllenatori() {
        return this.allenatori;
    }

    /**
     * Aggiunge un Allenatore alla lista di Allenatori dell'atleta
     *
     * @param allenatore Allenatore da aggiungere alla lista
     *
     */
    protected void addAllenatore(Allenatore allenatore) {
        this.allenatori.add(allenatore);
    }

    /**
     * Getter Sessioni
     *
     * @return Lista di Sessioni assegnate all'Atleta
     *
     */
    protected Set<SessioneDiAllenamento> getSessioni() {
        return this.sessioni;
    }
}
