// src/main/java/com/sohal/mca/project/ecommerce_backend/config/SecurityConfig.java
package com.sohal.mca.project.ecommerce_backend.config;

import com.sohal.mca.project.ecommerce_backend.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; 
import java.util.List;
/**
 * Author: Ravi Sohal
 * Date: 2025-07-30
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This class configures the security settings for the E-commerce backend application.
 * It uses Spring Security to define authentication and authorization rules.
 * The configuration includes:
 * - Disabling CSRF protection for REST APIs.
 * - Defining authorization rules for different endpoints.
 * - Using JWT for stateless session management.
 * - Exposing the AuthenticationManager bean for user authentication.
 * - Configuring a PasswordEncoder bean for password hashing.
 * - Adding a JWT filter to intercept requests and validate JWT tokens.
 * - **Configuring CORS to allow requests from the React frontend.**
 * The class is annotated with @Configuration and @EnableWebSecurity to enable Spring Security features.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Add this annotation to enable @PreAuthorize
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Defines the password encoder bean.
     * Uses BCryptPasswordEncoder for strong password hashing.
     * @return PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean.
     * Used by the AuthenticationController to perform user authentication.
     * @param authenticationConfiguration AuthenticationConfiguration instance.
     * @return AuthenticationManager instance.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the CORS (Cross-Origin Resource Sharing) settings for the application.
     * This bean defines which origins, methods, and headers are allowed for cross-origin requests.
     * @return CorsConfigurationSource instance.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your React frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); 
        // Allow specific HTTP methods, including OPTIONS for preflight requests
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow all headers, or specify only necessary ones (e.g., "Authorization", "Content-Type")
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        // Allow credentials (like cookies, authorization headers) to be sent with requests
        configuration.setAllowCredentials(true);
        // How long the preflight request can be cached
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all paths
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the security filter chain.
     * Defines authorization rules for different endpoints and integrates the JWT filter.
     * @param http HttpSecurity instance to configure.
     * @return SecurityFilterChain instance.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .cors(Customizer.withDefaults()) // Enable CORS using the bean defined above
            .authorizeHttpRequests(authorize -> authorize
                // Allow unauthenticated access to authentication endpoints (e.g., /api/1.0/auth/login)
                // and explicitly allow OPTIONS preflight requests for these paths.
                .requestMatchers(HttpMethod.POST, "/api/1.0/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/1.0/auth/login").permitAll()

                // Allow unauthenticated access to user registration endpoint (e.g., /api/1.0/users/register)
                // and explicitly allow OPTIONS preflight requests.
                .requestMatchers(HttpMethod.POST, "/api/1.0/users/register").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/1.0/users/register").permitAll()

                // Allow unauthenticated access to user product catalog endpoint (e.g., /api/1.0/products)
                // and explicitly allow OPTIONS preflight requests.
                .requestMatchers(HttpMethod.GET, "/api/1.0/products").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/1.0/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/1.0/products/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/1.0/products/**").permitAll()

                // Allow unauthenticated access to user product catalog endpoint (e.g., /api/1.0/categories)
                // and explicitly allow OPTIONS preflight requests.
                .requestMatchers(HttpMethod.GET, "/api/1.0/categories").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/1.0/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/1.0/categories/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/1.0/categories/**").permitAll()

                // Allow unauthenticated access to Swagger UI and API docs
                // and explicitly allow OPTIONS preflight requests for these paths.
                .requestMatchers(HttpMethod.GET, "/mcaproject/ecombackend/v3/api-docs/ecombackend-project-v1.0.json/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/error").permitAll() // Allow access to default Spring Boot error page

                // Require authentication for all other API endpoints starting with /api/1.0/
                .requestMatchers("/api/1.0/**").authenticated()

                // Deny all other requests by default
                .anyRequest().denyAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions for REST APIs (JWT)
            );

        // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
