package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.Cart;
import com.sohal.mca.project.ecommerce_backend.model.User;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: CartRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on Cart entities.
 * This interface can be extended to include custom query methods as needed.
 */

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    @Query("SELECT c FROM Cart c WHERE c.user = :user")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cart> findByUserWithLock(User user);

    boolean existsByUser(User user);

    void deleteByUser(User user);

}
