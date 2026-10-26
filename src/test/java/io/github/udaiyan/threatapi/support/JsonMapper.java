package io.github.udaiyan.threatapi.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonMapper() {
    }

    public static <T> T fromString(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to map JSON to " + type.getSimpleName(), ex);
        }
    }
}
