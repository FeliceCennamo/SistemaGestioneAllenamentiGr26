package entity;

public class RisultatoTempo extends Risultato {

    int tempo;

    public RisultatoTempo(){}

    public RisultatoTempo(String nota, int tempo) {
        super(nota);
        this.tempo = tempo;
    }

    public int getRisultato(){
        return this.tempo;
    }

    public void setRisultato(int tempo){
        this.tempo = tempo;
    }

}

