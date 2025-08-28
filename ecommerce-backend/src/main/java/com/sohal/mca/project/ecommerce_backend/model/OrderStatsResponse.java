package com.sohal.mca.project.ecommerce_backend.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-24
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: OrderStatsResponse class for the e-commerce application.
 * Represents a response containing order statistics such as total orders and total sales.
 * This class can be extended to include additional statistics as needed.
 */

 @Schema(name = "OrderStatsResponse", description = "Response body for order statistics")
public class OrderStatsResponse {
    private Long totalOrders;
    private BigDecimal totalSales;

    public OrderStatsResponse(Long totalOrders, BigDecimal totalSales) {
        this.totalOrders = totalOrders;
        this.totalSales = totalSales;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }
}
