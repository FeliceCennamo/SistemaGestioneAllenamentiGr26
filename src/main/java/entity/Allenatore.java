package entity;


import jakarta.persistence.*;

import java.util.HashSet;

@Entity
@Table(name = "Allenatori")
public class Allenatore extends Utente {

    @ManyToMany
    @JoinTable(
            name = "allenatore_atleta",
            joinColumns = @JoinColumn(name = "allenatore_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private HashSet<Atleta> atleti = new HashSet<Atleta>();

    @OneToMany(mappedBy = "allenatore")
    private HashSet<SessioneDiAllenamento> sessioni = new HashSet<>(); // G: Non sono sicuro lo debba avere

    public Allenatore(){}

    public Allenatore(String nome, String cognome, String mail, String password){
        super(nome, cognome, mail, password);
    }

    public HashSet<Atleta> getAtleti(){
        return this.atleti;
    }

    public void addAtleta(Atleta a){
        atleti.add(a);
    }

    public void removeAtleta(Atleta a){
        atleti.remove(a);
        a.getAllenatori().remove(this);
    }

    public Atleta getAtleta(Long id) throws IllegalArgumentException {
        for(Atleta a : atleti){
            if (a.getId().equals(id))
                return a;
        }

        throw new IllegalArgumentException("Atleta non trovato");
    }

    public HashSet<SessioneDiAllenamento> getSessioni(){
        return this.sessioni;
    }
}
