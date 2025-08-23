package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.OrderItem;
import com.sohal.mca.project.ecommerce_backend.model.Order;
import com.sohal.mca.project.ecommerce_backend.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: OrderRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on Order entities.
 * This interface can be extended to include custom query methods as needed.
 */

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Order order);

    List<OrderItem> findByProductId(Product product);

}
