package entity;


import jakarta.persistence.*;

import java.util.ArrayList;
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

    public Allenatore(){}

    public Allenatore(String nome, String cognome, String mail, String password){
        super(nome, cognome, mail, password);
    }

    public HashSet<Atleta> getAtleti(){
        return this.atleti;
    }

    public void addAtleta(Atleta a){
        atleti.add(a);
        //a.addAllenatore(this); // Forse sta roba va fatta dal GestoreUtenti
        //So Gigi, si lo fa il gestore per garantire persistenza (e dividere responsabilità)
    }

    public void removeAtleta(Atleta a){
        atleti.remove(a);
        a.getAllenatori().remove(this); // Forse sta roba va fatta dal GestoreUtenti
    }

    public Atleta getAtleta(Long id){
        for(Atleta a : atleti){
            if (a.getId().equals(id))
                return a;
        }

        return null;
    }
}
