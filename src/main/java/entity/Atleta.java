package entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Rappresenta un atleta registrato nel sistema.
 * <p>
 * Estende {@link Utente} aggiungendo attributi specifici come disciplina
 * sportiva, livello di esperienza e una lista personale di obiettivi.
 * Un atleta può essere seguito da più allenatori e partecipa a sessioni
 * di allenamento.
 * </p>
 */
@Entity
@Table(name = "atleti")
public class Atleta extends Utente {

    private String disciplina;
    private int livello;

    /**
     * Insieme degli obiettivi personali dell'atleta, memorizzati come
     * collezione di stringhe in una tabella separata.
     */
    @ElementCollection
    @CollectionTable(name = "atleta_obiettivo",
            joinColumns = @JoinColumn(name = "atleta_id"))
    @Column(name = "obiettivo")
    private Set<String> obiettivi;

    /**
     * Allenatori che seguono questo atleta.
     * Lato inverso della relazione molti-a-molti definita in
     * {@link Allenatore#atleti}.
     */
    @ManyToMany(mappedBy = "atleti")
    private final Set<Allenatore> allenatori = new HashSet<>();

    /**
     * Sessioni di allenamento a cui l'atleta partecipa, ordinate per data e ora
     * secondo l'ordinamento naturale di {@link SessioneDiAllenamento}.
     */
    @OneToMany(mappedBy = "atleta")
    private final Set<SessioneDiAllenamento> sessioni = new TreeSet<>();

    /**
     * Costruttore di default richiesto da JPA.
     */
    public Atleta() {
    }

    /**
     * Crea un nuovo atleta con i dati anagrafici e le informazioni sportive
     * principali.
     *
     * @param nome       nome dell'atleta
     * @param cognome    cognome dell'atleta
     * @param mail       indirizzo email (usato come username)
     * @param password   password non cifrata
     * @param disciplina disciplina sportiva praticata
     * @param livello    livello di esperienza (es. 1 = principiante)
     */
    public Atleta(String nome, String cognome, String mail, String password,
                     String disciplina, int livello) {
        super(nome, cognome, mail, password);
        this.disciplina = disciplina;
        this.livello = livello;
    }

    /**
     * Crea un nuovo atleta includendo anche una lista di obiettivi personali.
     *
     * @param nome       nome dell'atleta
     * @param cognome    cognome dell'atleta
     * @param mail       indirizzo email
     * @param password   password non cifrata
     * @param disciplina disciplina sportiva praticata
     * @param livello    livello di esperienza
     * @param obiettivi  insieme degli obiettivi personali
     */
    public Atleta(String nome, String cognome, String mail, String password,
                     String disciplina, int livello, Set<String> obiettivi) {
        super(nome, cognome, mail, password);
        this.disciplina = disciplina;
        this.livello = livello;
        this.obiettivi = obiettivi;
    }

    /**
     * Restituisce l'insieme degli obiettivi personali dell'atleta.
     *
     * @return insieme di stringhe rappresentanti gli obiettivi
     */
    protected Set<String> getObiettivo() {
        return this.obiettivi;
    }

    /**
     * Restituisce la disciplina sportiva praticata dall'atleta.
     *
     * @return la disciplina
     */
    public String getDisciplina() {
        return this.disciplina;
    }

    /**
     * Restituisce il livello di esperienza dell'atleta.
     *
     * @return il livello
     */
    public int getLivello() {
        return this.livello;
    }

    /**
     * Imposta la lista degli obiettivi personali.
     *
     * @param obiettivi il nuovo insieme di obiettivi
     */
    protected void setObiettivo(Set<String> obiettivi) {
        this.obiettivi = obiettivi;
    }

    /**
     * Imposta la disciplina sportiva.
     *
     * @param disciplina la nuova disciplina
     */
    protected void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    /**
     * Imposta il livello di esperienza.
     *
     * @param livello il nuovo livello
     */
    protected void setLivello(int livello) {
        this.livello = livello;
    }

    /**
     * Restituisce l'insieme degli allenatori che seguono questo atleta.
     *
     * @return insieme di {@link Allenatore}
     */
    protected Set<Allenatore> getAllenatori() {
        return this.allenatori;
    }

    /**
     * Aggiunge un allenatore alla lista di chi segue questo atleta.
     * <p>
     * Non aggiorna il lato inverso della relazione; è compito del chiamante
     * mantenere la coerenza.
     * </p>
     *
     * @param allenatore l'allenatore da associare
     */
    protected void addAllenatore(Allenatore allenatore) {
        this.allenatori.add(allenatore);
    }

    /**
     * Restituisce l'insieme ordinato delle sessioni di allenamento a cui
     * l'atleta partecipa.
     *
     * @return insieme ordinato di {@link SessioneDiAllenamento}
     */
    protected Set<SessioneDiAllenamento> getSessioni() {
        return this.sessioni;
    }
}