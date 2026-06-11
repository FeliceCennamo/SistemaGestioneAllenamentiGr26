package entity;

import jakarta.persistence.Entity;

import java.time.Duration;

/**
 * Risultato di un esercizio espresso come durata temporale.
 * <p>
 * Estende {@link Risultato} e memorizza il valore tramite
 * {@link Duration}, convertito automaticamente in formato ISO-8601
 * dal {@code AttributeConverter} globale quando persiste.
 * </p>
 */
@Entity
public class RisultatoTempo extends Risultato {

    Duration tempo;

    /**
     * Costruttore di default richiesto da JPA.
     */
    public RisultatoTempo() {
    }

    /**
     * Crea un risultato a tempo con la nota e la durata specificate.
     *
     * @param nota  annotazione testuale (può essere {@code null})
     * @param tempo durata registrata (può essere {@code null} se non ancora
     *              impostata)
     */
    public RisultatoTempo(String nota, Duration tempo) {
        super(nota);
        this.tempo = tempo;
    }

    /**
     * Restituisce la durata registrata per questo risultato.
     *
     * @return oggetto {@link Duration}, oppure {@code null}
     */
    @Override
    public Duration getRisultato() {
        return tempo;
    }

    /**
     * Imposta la durata del risultato.
     *
     * @param tempo oggetto {@link Duration} o {@code null} per azzerare il valore
     * @throws ClassCastException se l'argomento non è né {@link Duration} né
     *                            {@code null}
     */
    @Override
    protected void setRisultato(Object tempo) {
        if (tempo instanceof Duration || tempo == null) {
            this.tempo = (Duration) tempo;
        } else {
            throw new ClassCastException("Tipo di risultato non valido: atteso Duration o null");
        }
    }
}