package com.sohal.mca.project.ecommerce_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-24
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: OrderItem class for the e-commerce application.
 * Represents an item in an order in the e-commerce application.
 * This class can be extended to include attributes such as product, quantity, etc.
 */

@Entity
@Table(name = "order_items")
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal discount;

    @Column(nullable = false)
    private BigDecimal total;

    @PrePersist
    protected void onCreate() {
        BigDecimal discountedPricePerItem = price.multiply(BigDecimal.ONE.subtract(discount));
        this.total = discountedPricePerItem.multiply(BigDecimal.valueOf(quantity));
    }

}