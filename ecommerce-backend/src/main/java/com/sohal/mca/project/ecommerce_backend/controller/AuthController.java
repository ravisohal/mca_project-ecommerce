package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.security.JwtUtil;
import com.sohal.mca.project.ecommerce_backend.service.CustomUserDetailsService;
import com.sohal.mca.project.ecommerce_backend.util.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles user authentication, including login and JWT token generation.
 * It uses Spring Security for authentication and JWT for token management.
 * The controller provides endpoints for user login and token retrieval.
 * It also includes Swagger annotations for API documentation.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * authentication-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation.
 * The controller uses a logger to log authentication attempts and errors.
 * The @Autowired annotation is used to inject dependencies such as AuthenticationManager,
 * CustomUserDetailsService, and JwtUtil.
 * The controller provides an endpoint for user login, which accepts a username and password,
 * authenticates the user, and returns a JWT token if the authentication is successful.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/auth")
@Tag(name = "Authentication", description = "User authentication and JWT Token management")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class.getName());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     * @param authenticationRequest A map containing "username" and "password".
     * @return ResponseEntity with JWT token if successful, or error message.
     */
    @Operation(summary = "Authenticate user and get JWT token",
               description = "Logs in a user with username and password, returning a JWT token for subsequent authenticated requests.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Authentication successful, JWT token returned",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(example = "{\"jwt\": \"eyJhbGciOiJIUzI1Ni...\"}"))),
                   @ApiResponse(responseCode = "401", description = "Invalid credentials")
               })
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body for placing an order", required = true,
                                                        content = @Content(schema = @Schema(example = "{\"username\":\"username\",\"password\":\"password\"}")) )
                                                        @RequestBody Map<String, String> authenticationRequest) {
        String username = authenticationRequest.get("username");
        String password = authenticationRequest.get("password");

        logger.info("Attempting to authenticate user: {}", username);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user {}: Invalid username or password.", username);
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        logger.info("User {} authenticated successfully and JWT generated.", username);
        return ResponseEntity.ok(response);
    }

}
