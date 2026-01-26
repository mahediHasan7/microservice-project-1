package org.mahedi.photoappapiuser.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.mahedi.photoappapiuser.dto.LoginRequest;
import org.mahedi.photoappapiuser.dto.UserResponseDto;
import org.mahedi.photoappapiuser.service.UserService;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@NullMarked
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Environment environment;

    public AuthenticationFilter(UserService userService, Environment environment,
                                AuthenticationManager authenticationManager) {
        // Providing the AuthenticationManager to parent class to be used for/by getAuthenticationManager() inside the attemptAuthentication method. Without it, getAuthenticationManager() returns null.
        // We created this AuthenticationManager inside the WebSecurity class's configure() method.
        super(authenticationManager);
        this.userService = userService;
        this.environment = environment;
    }


    // Method to override for reading username(email) and password and invoke authenticate method on Authentication Manager
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // Deserializing the JSON payload from the HTTP request's input stream into a LoginRequest object using Jackson's ObjectMapper.
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            // Using AuthenticationManager, we call authenticate method which returns Authentication obj
            //
            // The `authenticate` method belongs to the `AuthenticationManager` interface in Spring Security, which is a functional interface (it has a single abstract method). This method takes an `Authentication` object as its parameter, which is an interface representing the authentication request.
            //
            //In your code, you're passing a `UsernamePasswordAuthenticationToken` instance to it. This class implements the `Authentication` interface and encapsulates the user's email (as the principal), password (as credentials), and an empty list of authorities (granted roles/permissions). The `AuthenticationManager` uses this token to perform authentication against your configured user details service or provider. If successful, it returns an authenticated `Authentication` object; otherwise, it throws an `AuthenticationException`.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {


        assert auth.getPrincipal() != null;
        //  auth.getPrincipal() providing the authenticated user's details (e.g., username from the User object). This ensures you're using the verified identity after authentication, not the raw input from the HttpServletRequest
        String userEmail = ((User) auth.getPrincipal()).getUsername();
        UserResponseDto user = userService.findUserByEmail(userEmail);
        String userId = user.getId().toString();
        String tokenSecret = environment.getProperty("token.secret");
        assert tokenSecret != null;
        byte[] tokenSecretBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(tokenSecretBytes);

        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(userId)
                .expiration(Date.from(now.plusMillis(Long.parseLong(environment.getProperty("token.expiration.time", "900000")))))
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        response.addHeader("Authorization", token);
        response.addHeader("userId", userId);
    }

}
