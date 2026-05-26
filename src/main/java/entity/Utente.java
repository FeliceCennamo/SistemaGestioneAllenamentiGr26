package entity;

import jakarta.persistence.*;


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

    public Utente(){}

    public Utente(String nome, String cognome, String mail, String password, String disciplinaPrevalente){
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.disciplinaPrevalente = disciplinaPrevalente;
    }

    public Utente(String nome, String cognome, String mail, String password){
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.disciplinaPrevalente = null;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }
}
