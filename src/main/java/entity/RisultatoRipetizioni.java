package entity;

import jakarta.persistence.Entity;

@Entity
public class RisultatoRipetizioni extends Risultato {

    private Integer ripetizioni;

    /**
     * Costruttore vuoto RisultatoRipetizioni
     */
    public RisultatoRipetizioni() {
    }

    /**
     * Costruttore RisultatoRipetizioni
     *
     * @param nota        Nota
     * @param ripetizioni Ripetizioni
     */
    public RisultatoRipetizioni(String nota, Integer ripetizioni) {
        super(nota);
        this.ripetizioni = ripetizioni;
    }

    /**
     * Getter Risultato
     *
     * @return Risultato (Integer)
     *
     */
    @Override
    public Integer getRisultato() {
        return this.ripetizioni;
    }

    /**
     * Setter Risultato
     *
     * @param ripetizioni Ripetizioni
     *
     */
    @Override
    public void setRisultato(Object ripetizioni) throws ClassCastException {
        if (ripetizioni instanceof Integer || ripetizioni == null)
            this.ripetizioni = (Integer) ripetizioni;
        else
            throw new ClassCastException("Tipo di risultato non valido");
    }


}
