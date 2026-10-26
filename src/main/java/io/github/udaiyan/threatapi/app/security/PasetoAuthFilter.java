package io.github.udaiyan.threatapi.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.udaiyan.threatapi.model.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class PasetoAuthFilter extends OncePerRequestFilter {

    private static final String PROTECTED_PATH = "/api/v1/assessments";

    private final PasetoTokenValidator validator;
    private final ObjectMapper mapper;

    public PasetoAuthFilter(PasetoTokenValidator validator) {
        this.validator = validator;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!PROTECTED_PATH.equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            reject(request, response, "Missing Bearer token");
            return;
        }

        String token = header.substring("Bearer ".length()).trim();
        if (!validator.isValid(token)) {
            reject(request, response, "Invalid PASETO token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                401,
                "Unauthorized",
                message,
                request.getRequestURI()
        );

        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
