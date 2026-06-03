package entity;


import Exceptions.ResourceNotFoundException;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


@Entity
@Table(name = "allenatori")
public class Allenatore extends Utente {

    @ManyToMany
    @JoinTable(
            name = "allenatore_atleta",
            joinColumns = @JoinColumn(name = "allenatore_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private Set<Atleta> atleti = new HashSet<Atleta>();


    @OneToMany(mappedBy = "allenatore")
    private Set<SessioneDiAllenamento> sessioni = new TreeSet<>();

    /**
     * Costruttore vuoto dell'oggetto Allenatore
     * */
    public Allenatore(){}

    /**
     * Costruttore dell'oggetto Allenatore avente disciplinaPrevalente
     * @param nome Nome Allenatore
     * @param cognome Cognome Allenatore
     * @param mail Indirizzo E-mail Allenatore
     * @param password Password Allenatore
     */
    public Allenatore(String nome, String cognome, String mail, String password){
        super(nome, cognome, mail, password);
    }

    /**
     * Costruttore dell'oggetto Allenatore avente disciplinaPrevalente
     * @param nome Nome Allenatore
     * @param cognome Cognome Allenatore
     * @param mail Indirizzo E-mail Allenatore
     * @param password Password Allenatore
     * @param disciplinaPrevalente Disciplina Prevalente Allenatore
     * */
    public Allenatore(String nome, String cognome, String mail, String password, String disciplinaPrevalente){
        super(nome, cognome, mail, password, disciplinaPrevalente);
    }

    public Set<Atleta> getAtleti(){
        return this.atleti;
    }

    public void addAtleta(Atleta a){
        atleti.add(a);
    }

    public void removeAtleta(Atleta a){
        atleti.remove(a);
        a.getAllenatori().remove(this);
    }

    public Atleta getAtleta(Long id) throws ResourceNotFoundException {
        for(Atleta a : atleti){
            if (a.getId().equals(id))
                return a;
        }

        throw new ResourceNotFoundException("Atleta non trovato");
    }

    public Set<SessioneDiAllenamento> getSessioni(){
        return this.sessioni;
    }
}
