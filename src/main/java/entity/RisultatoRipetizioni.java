package entity;

import jakarta.persistence.Entity;

/**
 * Risultato di un esercizio espresso in numero di ripetizioni.
 * <p>
 * Estende {@link Risultato} e memorizza il valore come {@link Integer}.
 * </p>
 */
@Entity
public class RisultatoRipetizioni extends Risultato {

    private Integer ripetizioni;

    /**
     * Costruttore di default richiesto da JPA.
     */
    public RisultatoRipetizioni() {
    }

    /**
     * Crea un risultato a ripetizioni con la nota e il valore indicati.
     *
     * @param nota        annotazione testuale (può essere {@code null})
     * @param ripetizioni numero di ripetizioni eseguite (può essere {@code null}
     *                    se non ancora registrato)
     */
    public RisultatoRipetizioni(String nota, Integer ripetizioni) {
        super(nota);
        this.ripetizioni = ripetizioni;
    }

    /**
     * Restituisce il numero di ripetizioni registrate.
     *
     * @return valore intero delle ripetizioni, oppure {@code null}
     */
    @Override
    protected Integer getRisultato() {
        return ripetizioni;
    }

    /**
     * Imposta il numero di ripetizioni.
     *
     * @param ripetizioni oggetto {@link Integer} o {@code null} per azzerare il valore
     * @throws ClassCastException se l'oggetto fornito non è né {@link Integer} né {@code null}
     */
    @Override
    protected void setRisultato(Object ripetizioni) {
        if (ripetizioni instanceof Integer || ripetizioni == null) {
            this.ripetizioni = (Integer) ripetizioni;
        } else {
            throw new ClassCastException("Tipo di risultato non valido: atteso Integer o null");
        }
    }
}