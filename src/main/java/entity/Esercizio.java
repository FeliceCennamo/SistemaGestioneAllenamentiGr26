package entity;

import com.mysql.cj.Session;
import jakarta.persistence.*;

import java.time.Duration;

@Entity
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;
    private Tipologia tipologia;


    //si deve far quadrare jakarta per le tabelle
    @ManyToOne
    @JoinColumn(name = "risultato", referencedColumnName = "id_risultato")
    private SessioneDiAllenamento sessione;


    public Esercizio(){}

    public Esercizio(String nome, String descrizione, int ris_atteso){

        this.nome = nome;
        this.descrizione = descrizione;
        this.tipologia = Tipologia.RIPETIZIONI;
        this.setRisultatoAtteso(ris_atteso);
    }

    // Gestire formato risultato atteso nel controller
    public Esercizio(String nome, String descrizione, String ris_atteso){

        this.nome = nome;
        this.descrizione = descrizione;
        this.tipologia = Tipologia.TEMPO;
        try{
            Duration d = Duration.parse(ris_atteso);
            this.setRisultatoAtteso(d);
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    private void setRisultatoAtteso(int risultatoAtteso){
        this.tipologia.setRisultatoAtteso(risultatoAtteso);
    }

    private void setRisultatoAtteso(Duration risultatoAtteso){
        this.tipologia.setRisultatoAtteso(risultatoAtteso);
    }


    public enum Tipologia {
        RIPETIZIONI{
            private int risultato_atteso;

            @Override
            void setRisultatoAtteso(Object risultatoAtteso){
                this.risultato_atteso =(Integer) risultatoAtteso;
            }
        },
        TEMPO{
            private Duration risultato_atteso;

            @Override
            void setRisultatoAtteso(Object risultatoAtteso){
                risultato_atteso = (Duration) risultatoAtteso;
            }
        };

        abstract void  setRisultatoAtteso(Object risultatoAtteso);
    }
}
