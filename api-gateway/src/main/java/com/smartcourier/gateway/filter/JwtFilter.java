package com.smartcourier.gateway.filter;

import com.smartcourier.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JwtFilter responsibilities:
 * 1. Validate JWT token
 * 2. Extract username & role
 * 3. Apply role-based authorization
 * 4. Forward user identity to downstream services via header
 */
@Component
public class JwtFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // ================= PUBLIC ENDPOINTS =================
        if (path.contains("/auth/login") || path.contains("/auth/signup")) {
            return chain.filter(exchange);
        }

        // ================= TOKEN EXTRACTION =================
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

// ROLE CHECK
            if (path.contains("/admin") && !role.equalsIgnoreCase("ADMIN")) {
                return forbidden(exchange);
            }

            if (path.contains("/deliveries")
                    && !(role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("CUSTOMER"))) {
                return forbidden(exchange);
            }

//  FIXED HEADER INJECTION
            String bearer = "Bearer " + token;

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .headers(headers -> {
                                headers.set("Authorization", bearer);
                                headers.set("X-User", username);
                                headers.set("X-Role", role);
                            })
                            .build())
                    .build();

            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    // ================= ERROR HANDLERS =================

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }



}
