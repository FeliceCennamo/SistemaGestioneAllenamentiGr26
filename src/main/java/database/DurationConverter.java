package database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;


// Potrebbe essere un pattern Adapter
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String> {
    @Override
    public String convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Duration.parse(dbData);
    }
}