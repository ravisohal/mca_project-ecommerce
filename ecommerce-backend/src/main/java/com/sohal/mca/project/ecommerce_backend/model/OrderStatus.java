package com.sohal.mca.project.ecommerce_backend.model;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-24
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: OrderStatus enum for the e-commerce application.
 * Represents the status of an order in the e-commerce application.
 * This enum can be extended to include additional statuses as needed.
 */ 

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
    