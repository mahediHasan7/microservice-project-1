package org.mahedi.photoappconfigserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // Require authentication for all HTTP requests
                .authorizeHttpRequests(authRequest -> authRequest
                        // restrict POST busrefresh endpoint only for ADMIN role, only user with ADMIN role can request
                        .requestMatchers(HttpMethod.POST, "/actuator/busrefresh").hasRole("ADMIN")
                        // restrict any GET request endpoint only for CLIENT role, only user with CLIENT role can request
                        .requestMatchers(HttpMethod.GET, "/**").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/encrypt").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/decrypt").hasRole("ADMIN")
                        .anyRequest().authenticated())
                // Disable CSRF protection for the /actuator/busrefresh endpoint
                .csrf(csrfConfig -> csrfConfig.ignoringRequestMatchers("/actuator/busrefresh", "/encrypt", "/decrypt"))
                // Without enabling HTTP Basic authentication, all requests requiring authentication will be denied access.
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

    /*
        UserDetails objects created for 'admin' and 'client' (with username, encoded password, roles)
           ↓
        InMemoryUserDetailsManager stores these UserDetails in memory at application startup
           ↓
        HTTP request received by the application
           ↓
        Spring Security intercepts the request and extracts the username/password from HTTP Basic Auth
           ↓
        InMemoryUserDetailsManager searches for the username among in-memory users
           ↓
        If user found, provided password is encoded and compared with stored encoded password
           ↓
        If password matches, authentication is successful
           ↓
        Spring Security checks the user's roles/authorities for the requested endpoint
           ↓
        If user has required role, access is granted; otherwise, access is denied
     */


    @Bean
    InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails admin = User
                .withUsername(environment.getProperty("spring.security.user.name", "admin"))
                .password(passwordEncoder.encode((environment.getProperty("spring.security.user.password", "12345"))))
                .roles(environment.getProperty("spring.security.user.roles", "ADMIN"))
                .build();

        UserDetails client = User
                .withUsername(environment.getProperty("client-spring.security.user.name", "client"))
                .password(passwordEncoder.encode((environment.getProperty("client-spring.security.user.password", "12345"))))
                .roles(environment.getProperty("client-spring.security.user.roles", "CLIENT"))
                .build();

        return new InMemoryUserDetailsManager(admin, client);
    }

    @Bean
    PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
