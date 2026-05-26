package entity;

import java.time.Duration;

public class RisultatoTempo extends Risultato<Duration> {

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
    public void setRisultato(Duration tempo){
        this.tempo = tempo;
    }

}

