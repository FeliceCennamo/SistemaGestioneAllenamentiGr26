package entity;

import com.mysql.cj.Session;
import jakarta.persistence.*;

@Entity
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;

    @OneToOne
    @JoinColumn(name = "risultato", referencedColumnName = "id_risultato")
    private SessioneDiAllenamento sessione;



    @OneToOne
    @JoinColumn(name = "risultato", referencedColumnName = "id_risultato")
    private Risultato risultato;
}
