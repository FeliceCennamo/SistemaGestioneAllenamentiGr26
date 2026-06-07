package database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;


// Potrebbe essere un pattern Adapter
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String> {

    /**
     * Converte un oggetto Duration in un oggetto String
     *
     * @param duration oggetto Duration da voler convertire
     * @return oggetto String risultato della conversione
     *
     */
    @Override
    public String convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.toString();
    }

    /**
     * Converte un oggetto String in un oggetto Duration
     *
     * @param dbData oggetto String da voler convertire
     * @return oggetto Duration risultato della conversione
     *
     */
    @Override
    public Duration convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Duration.parse(dbData);
    }
}