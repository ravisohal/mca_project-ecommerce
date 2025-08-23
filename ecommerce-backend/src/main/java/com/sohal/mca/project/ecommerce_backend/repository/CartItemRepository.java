package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.Cart;
import com.sohal.mca.project.ecommerce_backend.model.CartItem;
import com.sohal.mca.project.ecommerce_backend.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: CartItemRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on CartItem entities.
 * This interface can be extended to include custom query methods as needed.
 */

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    List<CartItem> findByProduct(Product product);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
