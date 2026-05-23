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

    @ManyToOne
    @JoinColumn(name = "risultato", referencedColumnName = "id_risultato")
    private SessioneDiAllenamento sessione;

    @OneToOne
    @JoinColumn(name = "risultato", referencedColumnName = "id_risultato")
    private Risultato risultato;

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

        Duration d = Duration.parse(ris_atteso);

        this.setRisultatoAtteso(d);
    }

    private void setRisultatoAtteso(int risultatoAtteso){
        tipologia.setRisultatoAtteso(risultatoAtteso);
    }

    private void setRisultatoAtteso(Duration risultatoAtteso){
        tipologia.setRisultatoAtteso(risultatoAtteso);
    }


    public enum Tipologia {
        RIPETIZIONI{
            private int risultato_atteso;

            private void setRisultatoAtteso(int risultatoAtteso){
                risultato_atteso = risultatoAtteso;
            }
        },
        TEMPO{
            private Duration risultato_atteso;

            private void setRisultatoAtteso(Duration risultatoAtteso){
                risultato_atteso = risultatoAtteso;
            }
        };

        private void  setRisultatoAtteso(Object risultatoAtteso){};
    }
}
