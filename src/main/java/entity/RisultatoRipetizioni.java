package entity;

public class RisultatoRipetizioni extends Risultato {

    private int ripetizioni;

    public RisultatoRipetizioni(String nota, int ripetizioni){
        super(nota);
        this.ripetizioni = ripetizioni;
    }

    public int getRisultato(){
        return this.ripetizioni;
    }

    public void setRisultato(int ripetizioni){
        this.ripetizioni = ripetizioni;
    }


}
