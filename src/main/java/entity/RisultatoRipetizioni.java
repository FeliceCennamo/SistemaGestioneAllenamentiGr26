package entity;

import jakarta.persistence.Entity;

@Entity
public class RisultatoRipetizioni extends Risultato {

    private Integer ripetizioni;

    public RisultatoRipetizioni(){}

    public RisultatoRipetizioni(String nota, Integer ripetizioni){
        super(nota);
        this.ripetizioni = ripetizioni;
    }

    @Override
    public Integer getRisultato(){
        return this.ripetizioni;
    }

    @Override
    public void setRisultato(Object ripetizioni){

        if (!(ripetizioni instanceof Integer))
            this.ripetizioni = (Integer) ripetizioni;
        else
            throw new IllegalArgumentException("Tipo di risultato non valido");
    }


}
