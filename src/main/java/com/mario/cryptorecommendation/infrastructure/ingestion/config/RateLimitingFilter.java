package com.mario.cryptorecommendation.infrastructure.ingestion.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Bucket bucket;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(
            @Value("${rate.limit.requests-per-minute:20}") int requestsPerMinute,
            ObjectMapper objectMapper) {

        var limit = Bandwidth.simple(requestsPerMinute, Duration.ofMinutes(1));
        this.bucket = Bucket.builder().addLimit(limit).build();
        this.objectMapper = objectMapper;

        log.info("Rate limiting filter initialized with {} requests per minute", requestsPerMinute);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var requestURI = request.getRequestURI();
        var clientIP = getClientIP(request);

        if (bucket.tryConsume(1)) {
            log.debug("Request allowed for {} to {}", clientIP, requestURI);
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for {} to {}", clientIP, requestURI);
            sendRateLimitResponse(response);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        var xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(TOO_MANY_REQUESTS.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        var errorResponse = Map.of(
                "error", "Rate limit exceeded",
                "message", "Too many requests. Please try again later.",
                "retryAfter", "60 seconds"
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
