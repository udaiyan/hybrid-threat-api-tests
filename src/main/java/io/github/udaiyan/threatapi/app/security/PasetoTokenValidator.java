package io.github.udaiyan.threatapi.app.security;

public interface PasetoTokenValidator {
    boolean isValid(String token);
}
