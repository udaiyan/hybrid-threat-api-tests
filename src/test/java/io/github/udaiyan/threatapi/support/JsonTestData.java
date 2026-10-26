package io.github.udaiyan.threatapi.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class JsonTestData {

    private JsonTestData() {
    }

    public static String validCreateThreat() {
        return read("/testdata/create-threat-valid.json");
    }

    public static String invalidSeverityCreateThreat() {
        return read("/testdata/create-threat-invalid-severity.json");
    }

    public static String missingNameCreateThreat() {
        return read("/testdata/create-threat-missing-name.json");
    }

    private static String read(String path) {
        try (InputStream in = JsonTestData.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing test data resource: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
