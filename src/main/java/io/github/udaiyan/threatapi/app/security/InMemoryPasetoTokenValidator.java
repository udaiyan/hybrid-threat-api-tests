package io.github.udaiyan.threatapi.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Simplified synthetic token validator for demonstration purposes only.
 * This performs no cryptographic verification and is not production code.
 */
@Component
public class InMemoryPasetoTokenValidator implements PasetoTokenValidator {

    private final String expectedToken;

    public InMemoryPasetoTokenValidator(
            @Value("${threatapi.security.paseto-token}") String expectedToken) {
        this.expectedToken = expectedToken;
    }

    @Override
    public boolean isValid(String token) {
        return token != null
                && token.startsWith("v4.local.")
                && token.equals(expectedToken);
    }
}
