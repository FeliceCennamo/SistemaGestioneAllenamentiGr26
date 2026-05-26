package entity;

import jakarta.persistence.Entity;

import java.time.Duration;

@Entity
public class RisultatoTempo extends Risultato {

    Duration tempo;

    public RisultatoTempo(){}

    public RisultatoTempo(String nota, Duration tempo) {
        super(nota);
        this.tempo = tempo;
    }

    @Override
    public Duration getRisultato(){
        return this.tempo;
    }

    @Override
    public void setRisultato(Object tempo) throws IllegalArgumentException{
        if (tempo instanceof Duration)
            this.tempo = (Duration) tempo;
        else
            throw new IllegalArgumentException("Tipo di risultato non valido");
    }

}

