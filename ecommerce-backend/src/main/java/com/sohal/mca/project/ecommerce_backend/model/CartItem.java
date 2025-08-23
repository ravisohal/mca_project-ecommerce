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
 * Description: CartItem class for the e-commerce application.
 * Represents an item in a shopping cart in the e-commerce application.
 * This class can be extended to include attributes such as product, quantity, etc.
 */

@Entity
@Table(name = "cart_items")
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal priceAtAddition;

    @Column(nullable = false)
    private BigDecimal discountAtAddition;

    @Column(nullable = false)
    private BigDecimal total;

}
