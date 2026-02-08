package org.mahedi.photoappapiuser.security;

import lombok.RequiredArgsConstructor;
import org.mahedi.photoappapiuser.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final Environment environment;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //! READ THIS TO UNDERSTAND THE WHOLE FLOW:
    // WebSecurity.configure() (sets up AuthenticationManager and adds AuthenticationFilter)
    // -> Login Request -> AuthenticationFilter.attemptAuthentication()
    // -> AuthenticationManager.authenticate() -> UserService.loadUserByUsername(email) (queries DB for UserDetails with encoded password)
    // -> Password Comparison (BCryptPasswordEncoder.matches(rawPasswordFromRequest, encodedPasswordFromDB))
    // -> Success (Authentication object) or Failure (AuthenticationException)
    // -> successfulAuthentication() (generates JWT).

    // SecurityFilterChain configures Spring Security's HTTP security settings, and it's automatically used by Spring Boot to apply these security rules to incoming requests.
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) {
        // Need to create instance of AuthenticationManager and AuthenticationFilter(my own created class) which will be provided with http

        // Authentication Manager and it's config
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // AuthenticationFilter instance (used AuthenticationManager instance)
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager);
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path", "/users/login"));

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authManager -> {
            authManager
                    // please make sure the request coming in /h2-console/**, not /users/h2-console/** or /users-ws/h2-console/**
                    // use RewritePath=/users-ws/(?<segment>.*),/$\{segment} in ApiGateway config file for Path=/users-ws/h2-console/**
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/actuator/circuitbreakerevents").permitAll()
                    .requestMatchers(HttpMethod.GET, "/actuator/retryevents").permitAll()
                    .requestMatchers(("/users/**")).access(new WebExpressionAuthorizationManager(
                            "hasIpAddress('" + environment.getProperty("gateway.ip") + "')"));

        });


        // configuring sessions as STATELESS ensures the server does not maintain session state between requests. This is ideal for RESTful APIs, as each request must include all necessary information (e.g., via tokens like JWT), improving scalability, reliability, and ease of horizontal scaling across servers.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // h2-console uses iframes (frames within frames) to display its database interface.
        // This line allows those frames to load only from the same origin (localhost), like allowing only your own photos in your picture frame.
        // Without this line, Spring Security blocks the frames by default, causing the console to appear blank.
        http.headers(header -> header.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // adds custom AuthenticationFilter to the Spring Security filter chain. It enables custom authentication logic before other filters process the request. This will help to invoke attemptAuthentication() and successfulAuthentication() method that I override in AuthenticationFilter class
        http.addFilter(authenticationFilter);

        // adds custom AuthenticationManager to HttpSecurity as default AuthManager, which is configured with UserService and BCryptPasswordEncoder
        http.authenticationManager(authenticationManager);

        return http.build();
    }

}
