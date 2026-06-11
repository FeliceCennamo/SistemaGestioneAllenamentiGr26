package entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe base astratta per tutti gli utenti del sistema (atleti e allenatori).
 * <p>
 * Mappa i campi anagrafici comuni, le credenziali di accesso e una disciplina
 * sportiva prevalente. La password viene memorizzata sotto forma di hash
 * bcrypt e mai in chiaro.
 * </p>
 * <p>
 * Le sottoclassi concrete ({@link Atleta}, {@link Allenatore}) ereditano
 * questa mappatura grazie all'annotazione {@link MappedSuperclass}.
 * </p>
 */
@MappedSuperclass
public abstract class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;
    private String mail;
    private String password;

    private String disciplinaPrevalente;

    /**
     * Costruttore di default richiesto da JPA.
     */
    public Utente() {
    }

    /**
     * Crea un utente con i soli dati anagrafici e le credenziali, senza
     * specificare una disciplina prevalente (che rimane {@code null}).
     *
     * @param nome     nome dell'utente
     * @param cognome  cognome dell'utente
     * @param mail     indirizzo email (usato come identificativo di accesso)
     * @param password password in chiaro (verrà cifrata automaticamente)
     */
    protected Utente(String nome, String cognome, String mail, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.disciplinaPrevalente = null;
    }

    /**
     * Crea un utente completo di disciplina prevalente.
     *
     * @param nome                 nome dell'utente
     * @param cognome              cognome dell'utente
     * @param mail                 indirizzo email
     * @param password             password in chiaro (verrà cifrata)
     * @param disciplinaPrevalente disciplina sportiva principale dell'utente
     */
    public Utente(String nome, String cognome, String mail, String password,
                  String disciplinaPrevalente) {
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.disciplinaPrevalente = disciplinaPrevalente;
    }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return il nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dell'utente.
     *
     * @param nome il nuovo nome
     */
    protected void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return il cognome
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome dell'utente.
     *
     * @param cognome il nuovo cognome
     */
    protected void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce l'indirizzo email dell'utente.
     *
     * @return l'email
     */
    public String getMail() {
        return mail;
    }

    /**
     * Imposta l'indirizzo email.
     *
     * @param mail la nuova email
     */
    protected void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * Restituisce l'hash della password (non il valore in chiaro).
     *
     * @return la password cifrata
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente, memorizzandola in forma cifrata
     * tramite l'algoritmo bcrypt.
     *
     * @param password la password in chiaro da cifrare
     */
    protected void setPassword(String password) {
        // BCrypt.gensalt() genera un salt casuale e restituisce l'hash
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Restituisce l'identificativo univoco dell'utente.
     *
     * @return l'id
     */
    public Long getId() {
        return id;
    }

    /**
     * Restituisce la disciplina sportiva prevalente dell'utente.
     *
     * @return la disciplina, o {@code null} se non impostata
     */
    public String getDisciplinaPrevalente() {
        return disciplinaPrevalente;
    }

    /**
     * Imposta la disciplina sportiva prevalente.
     *
     * @param disciplinaPrevalente la nuova disciplina (può essere {@code null})
     */
    protected void setDisciplinaPrevalente(String disciplinaPrevalente) {
        this.disciplinaPrevalente = disciplinaPrevalente;
    }
}