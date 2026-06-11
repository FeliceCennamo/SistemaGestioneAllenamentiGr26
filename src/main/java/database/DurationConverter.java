package database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

/**
 * Convertitore JPA che mappa un {@link Duration} su una colonna di tipo
 * stringa (VARCHAR) e viceversa.
 * <p>
 * La rappresentazione testuale utilizzata segue il formato ISO-8601
 * (ad esempio {@code PT30M} per 30 minuti), come prodotto e accettato
 * da {@link Duration#toString()} e {@link Duration#parse(CharSequence)}.
 * </p>
 *
 * <p>
 * Grazie all'attributo {@code autoApply = true} il convertitore viene
 * applicato automaticamente a tutte le proprietà di tipo {@code Duration}
 * in tutte le entità gestite dall'unità di persistenza, senza bisogno di
 * annotare esplicitamente ogni attributo con {@code @Convert}.
 * </p>
 */
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String> {

    /**
     * Converte un oggetto {@link Duration} nel suo equivalente testuale
     * ISO-8601 per la persistenza nel database.
     *
     * @param duration la durata da convertire; può essere {@code null}
     * @return la rappresentazione testuale, oppure {@code null} se
     *         l'argomento è {@code null}
     */
    @Override
    public String convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.toString();
    }

    /**
     * Converte una stringa ISO-8601 prelevata dal database in un
     * oggetto {@link Duration}.
     *
     * @param dbData il valore testuale letto dalla colonna; può essere
     *               {@code null}
     * @return l'oggetto {@link Duration} corrispondente, oppure {@code null}
     *         se l'argomento è {@code null}
     */
    @Override
    public Duration convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Duration.parse(dbData);
    }
}