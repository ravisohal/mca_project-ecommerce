package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on User entities.
 * This interface can be extended to include custom query methods as needed.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByUsername(String name);

    boolean existsByEmail(String email);

}
