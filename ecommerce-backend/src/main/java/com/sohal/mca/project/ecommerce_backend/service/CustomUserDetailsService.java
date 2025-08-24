package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // For simple empty authorities list
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-30
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: CustomUserDetailsService class for the e-commerce application.
 * Implements UserDetailsService to load user-specific data.
 * This service is used by Spring Security to authenticate users based on their username.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user based on the username.
     * @param username The username identifying the user whose data is required.
     * @return A fully populated user record (an instance of Spring Security's UserDetails).
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<String> roles = user.getRole() != null ? List.of(user.getRole()) : new ArrayList<>();
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // For simplicity, we are returning a Spring Security User object with an empty list of authorities.
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
