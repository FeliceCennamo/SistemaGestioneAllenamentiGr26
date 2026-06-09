package entity;

import jakarta.persistence.Entity;

import java.time.Duration;

@Entity
public class RisultatoTempo extends Risultato {

    Duration tempo;

    /**
     * Costruttore vuoto di RisultatoTempo
     *
     */
    public RisultatoTempo() {
    }

    /**
     * Costruttore di RisultatoTempo
     *
     * @param nota  Nota
     * @param tempo Tempo
     *
     */
    public RisultatoTempo(String nota, Duration tempo) {
        super(nota);
        this.tempo = tempo;
    }

    /**
     * Getter Risultato
     *
     * @return Risultato
     *
     */
    @Override
    public Duration getRisultato() {
        return this.tempo;
    }

    /**
     * Setter Risultato
     *
     * @param tempo Tempo
     *
     */
    @Override
    public void setRisultato(Object tempo) throws ClassCastException {
        if (tempo instanceof Duration || tempo == null)
            this.tempo = (Duration) tempo;
        else
            throw new ClassCastException("Tipo di risultato non valido");
    }

}

