package entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;


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
     * Costruttore vuoto dell'oggetto Utente
     *
     */
    public Utente() {
    }

    /**
     * Costruttore dell'oggetto Utente avente disciplinaPrevalente
     *
     * @param nome                 Nome Utente
     * @param cognome              Cognome Utente
     * @param mail                 Indirizzo E-mail Utente
     * @param password             Password Utente
     * @param disciplinaPrevalente Disciplina Prevalente Utente
     *
     */
    public Utente(String nome, String cognome, String mail, String password, String disciplinaPrevalente) {
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.disciplinaPrevalente = disciplinaPrevalente;
    }


    /**
     * Costruttore dell'oggetto Utente avente disciplinaPrevalente
     *
     * @param nome     Nome Utente
     * @param cognome  Cognome Utente
     * @param mail     Indirizzo E-mail Utente
     * @param password Password Utente
     *
     */
    public Utente(String nome, String cognome, String mail, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.disciplinaPrevalente = null;
    }

    /**
     * Getter Nome
     *
     * @return Nome Utente
     *
     */
    public String getNome() {
        return nome;
    }

    /**
     * Setter Nome
     *
     * @param nome Nome
     *
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Getter Cognome
     *
     * @return Cognome Utente
     *
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Setter Cognome
     *
     * @param cognome Cognome
     *
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Getter E-mail
     *
     * @return E-mail Utente
     *
     */
    public String getMail() {
        return mail;
    }

    /**
     * Setter E-mail
     *
     * @param mail E-mail
     *
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * Getter Password
     *
     * @return Password Utente
     *
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter Password
     *
     * @param password Password
     *
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter Id
     *
     * @return Id Utente
     *
     */
    public Long getId() {
        return id;
    }
}
