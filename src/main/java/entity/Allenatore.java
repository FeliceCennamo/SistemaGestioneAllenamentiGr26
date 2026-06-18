package entity;

import exceptions.ResourceNotFoundException;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Rappresenta un allenatore all'interno del sistema.
 * <p>
 * Estende {@link Utente}, ereditandone i dati anagrafici e le credenziali
 * di accesso. Un allenatore può supervisionare più atleti e gestire
 * diverse sessioni di allenamento.
 * </p>
 */
@Entity
@Table(name = "allenatori")
public class Allenatore extends Utente {

    /**
     * Insieme degli atleti seguiti da questo allenatore.
     * La relazione molti-a-molti è gestita tramite la tabella di join
     * {@code allenatore_atleta}.
     */
    @ManyToMany
    @JoinTable(
            name = "allenatore_atleta",
            joinColumns = @JoinColumn(name = "allenatore_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private Set<Atleta> atleti = new HashSet<>();

    /**
     * Insieme delle sessioni di allenamento create da questo allenatore,
     * ordinate secondo l'ordinamento naturale definito in
     * {@link SessioneDiAllenamento}.
     */
    @OneToMany(mappedBy = "allenatore")
    private Set<SessioneDiAllenamento> sessioni = new TreeSet<>();

    /**
     * Costruttore di default richiesto da JPA.
     */
    public Allenatore() {
    }

    /**
     * Crea un nuovo allenatore con i dati anagrafici e le credenziali,
     * senza specificare una disciplina prevalente.
     *
     * @param nome     nome dell'allenatore
     * @param cognome  cognome dell'allenatore
     * @param mail     indirizzo email (usato anche come username)
     * @param password password non cifrata (la cifratura è demandata
     *                 ad un livello esterno)
     */
    public Allenatore(String nome, String cognome, String mail, String password) {
        super(nome, cognome, mail, password);
    }

    /**
     * Crea un nuovo allenatore completo di disciplina prevalente.
     *
     * @param nome                 nome dell'allenatore
     * @param cognome              cognome dell'allenatore
     * @param mail                 indirizzo email
     * @param password             password non cifrata
     * @param disciplinaPrevalente la disciplina sportiva principale insegnata
     */
    public Allenatore(String nome, String cognome, String mail, String password,
                      String disciplinaPrevalente) {
        super(nome, cognome, mail, password, disciplinaPrevalente);
    }

    /**
     * Restituisce l'insieme degli atleti assegnati a questo allenatore.
     *
     * @return insieme (non modificabile dall'esterno) di {@link Atleta}
     */
    protected Set<Atleta> getAtleti() {
        return atleti;
    }

    /**
     * Aggiunge un atleta alla lista degli atleti seguiti.
     *
     * @param a l'atleta da associare
     */
    protected void addAtleta(Atleta a) {
        atleti.add(a);
    }

    /**
     * Rimuove un atleta dalla lista e aggiorna il lato inverso della
     * relazione, eliminando questo allenatore dall'insieme degli
     * allenatori dell'atleta.
     *
     * @param a l'atleta da dissociare
     */
    protected void removeAtleta(Atleta a) {
        atleti.remove(a);
        a.getAllenatori().remove(this);
    }

    /**
     * Cerca un atleta specifico tra quelli seguiti, in base all'identificativo.
     *
     * @param id l'id dell'atleta da cercare
     * @return l'atleta trovato
     * @throws ResourceNotFoundException se l'atleta non è tra quelli seguiti
     *                                   da questo allenatore
     */
    protected Atleta getAtleta(Long id) throws ResourceNotFoundException {
        for (Atleta a : atleti) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        throw new ResourceNotFoundException("Atleta non trovato");
    }

    /**
     * Restituisce l'insieme delle sessioni di allenamento create da
     * questo allenatore, ordinate per data e ora.
     *
     * @return insieme ordinato di {@link SessioneDiAllenamento}
     */
    protected Set<SessioneDiAllenamento> getSessioni() {
        return sessioni;
    }
}