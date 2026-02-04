package org.mahedi.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
@NullMarked
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment environment;

    public AuthorizationHeaderFilter(Environment environment) {
        super(Config.class);
        this.environment = environment;
    }


    /*
     Config class is required by Spring Cloud Gateway's AbstractGatewayFilterFactory. It holds configuration properties
     for the custom filter, allowing it to be parameterized via YAML (e.g., filters: - AuthorizationHeaderFilter=param1=value1).
     Even if empty, it's mandatory for the factory to instantiate and apply the filter correctly.
     */
    public static class Config {
        // Put configuration properties here
    }

    /*
        Client sends request
            ↓
        Gateway receives request
            ↓
        AuthorizationHeaderFilter checks for Authorization header
            ↓ (if missing)
            Return 401 Unauthorized
            ↓ (if present)
            Extract and validate JWT
            ↓ (if invalid)
            Return 401 Unauthorized
            ↓ (if valid)
            Forward request to downstream service
            ↓
        Service processes request and responds
            ↓
        Gateway returns response to client
     */

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header provided");
            }

            // get the jason web token
            String authorizationHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeaders.replace("Bearer", "").trim();

            if (!isJwtValid(jwt)) {
                return onError(exchange, "Invalid Json Web Token");
            }

            return chain.filter(exchange);
        };
    }

    /*
        JWT
            eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

        It has 3 parts separated by dots (.):
            1. Header (algorithm & token type)
                {
                  "alg": "HS256",
                  "typ": "JWT"
                }
            2. Payload (claims/data)
                {
                  "sub": "1234567890", (user identifier)
                  "name": "John Doe",
                  "iat": 1516239022
                }

                Note: we could add custom claims from User Microservice too: Jwts.builder()
                                                                                .claim("email", userEmail)
                                                                                .claim("role", userRole)
                                                                                .compact()

            3. Signature (verification)
                This verifies the token hasn't been tampered with.
     */

    /*
        Received Json web token as parameter
            -> Retrieve the token secret from properties file
            -> Convert the token secret string to HMAC SHA key (SecretKey) using encoded Base64 bytes
            -> With the SecretKey, build a JwtParser configured to verify signatures
            -> Use that JwtParser.parseSignedClaims() to:
               - Parse the JWT string
               - Verify its signature using the secret key
               - Throw exception if signature is invalid, token is expired, or malformed
            -> Then, using getPayload(), extract the claims (decoded payload data)
            -> From the claims, extract the "sub" (subject) field
               (This contains the UserId which was added in the User Microservice's AuthenticationFilter)
            -> Validation checks:
               - If parsing throws any exception → mark request as invalid
               - If subject is null or empty → mark request as invalid
               - Otherwise → mark request as valid
     */

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String tokenSecret = environment.getProperty("token.secret");
        assert tokenSecret != null;
        byte[] tokenSecretBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(tokenSecretBytes);
        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        String subject = null;
        try {
            Claims claims = parser.parseSignedClaims(jwt).getPayload();
            subject = claims.get("sub").toString();
        } catch (Exception ex) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(message.getBytes())));
    }

}
