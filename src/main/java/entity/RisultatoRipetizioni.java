package entity;

public class RisultatoRipetizioni extends Risultato<Integer> {

    private Integer ripetizioni;

    public RisultatoRipetizioni(String nota, Integer ripetizioni){
        super(nota);
        this.ripetizioni = ripetizioni;
    }

    @Override
    public Integer getRisultato(){
        return this.ripetizioni;
    }

    @Override
    public void setRisultato(Integer ripetizioni){
        this.ripetizioni = ripetizioni;
    }


}
