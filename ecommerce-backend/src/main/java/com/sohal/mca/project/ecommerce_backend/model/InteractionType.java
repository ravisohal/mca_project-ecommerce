package com.sohal.mca.project.ecommerce_backend.model;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-24
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserInteraction class for the e-commerce application.
 * Represents a user interaction in the e-commerce application.
 * This class can be extended to include attributes such as user, product, action type, etc.
 */

public enum InteractionType {
    VIEW,
    ADD_TO_CART,
    REMOVE_FROM_CART,
    PURCHASE,
    REVIEW
}
