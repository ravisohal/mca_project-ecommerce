package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.Order;
import com.sohal.mca.project.ecommerce_backend.model.OrderStatsResponse;
import com.sohal.mca.project.ecommerce_backend.model.OrderStatus;
import com.sohal.mca.project.ecommerce_backend.service.OrderService;
import com.sohal.mca.project.ecommerce_backend.util.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles operations related to customer orders,
 * including placing a new order, retrieving all orders, getting an order by ID,
 * getting orders by user ID, and updating order status. It uses Swagger annotations for API documentation.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * order-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation.
 * The controller uses a logger to log requests and responses for debugging purposes.
 * The controller uses an OrderService to handle business logic related to orders.
 * It provides endpoints for placing a new order, retrieving all orders, getting an order by ID,
 * getting orders by user ID, and updating order status. Each endpoint is secured with appropriate
 * authorization annotations to ensure that only authenticated users can perform certain actions.
 * The controller handles various exceptions and returns appropriate HTTP status codes for success and error cases.
 * The @Operation annotation is used to document each endpoint's purpose, parameters, and expected responses.
 * The @ApiResponse annotations provide detailed information about the possible responses for each endpoint.
 * The controller also includes Swagger examples for request bodies where applicable.
 * The controller is designed to be RESTful, following best practices for API design.
 * It uses standard HTTP methods (GET, POST, PUT) for different operations and returns JSON responses.
 * The controller is intended to be used in conjunction with a frontend application or other services
 * that need to interact with the order management system of the e-commerce platform.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/orders")
@Tag(name = "Orders", description = "Operations related to customer orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
        logger.info("OrderController initialized.");
    }

    @Operation(summary = "Place a new order", description = "Creates a new order from the user's current cart.")
    @ApiResponse(responseCode = "201", description = "Order placed successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., user not found, empty cart, insufficient stock, invalid address)")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/place")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to place an order
    public ResponseEntity<Order> placeOrder(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body for placing an order", required = true,
                                          content = @Content(schema = @Schema(example = "{\"userId\":1,\"shippingAddressId\":1}")))
                                          @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long shippingAddressId = request.get("shippingAddressId");

        logger.info("Received request to place order for user ID: {} with shipping address ID: {}", userId, shippingAddressId);
        try {
            Order newOrder = orderService.placeOrder(userId, shippingAddressId);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error placing order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Unexpected error placing order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')") // Only authenticated users with ADMIN role can view all orders
    public ResponseEntity<Page<Order>> getAllOrders(@Parameter(description = "Page number to retrieve", example = "0") @RequestParam(defaultValue = "0") int page,
                                                    @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        logger.debug("Received request to get all orders.");
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve a single order by its unique ID. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Order found",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)))
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view a specific order
    public ResponseEntity<Order> getOrderById(@Parameter(description = "ID of the order to retrieve") @PathVariable Long id) {
        logger.debug("Received request to get order by ID: {}", id);
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get orders by user ID", description = "Retrieve a list of orders placed by a specific user. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders for the user",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view orders for a specific user
    public ResponseEntity<Page<Order>> getOrdersByUser(@Parameter(description = "ID of the user whose orders to retrieve") @PathVariable Long userId,
                                                       @Parameter(description = "Page number to retrieve", example = "0") @RequestParam(defaultValue = "0") int page,
                                                       @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        logger.debug("Received request to get orders for user ID: {}", userId);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getOrdersByUser(userId, pageable);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting orders for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error getting orders for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update order status", description = "Update the status of an existing order. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status or order not found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')") // Only authenticated users with ADMIN role can update order status
    public ResponseEntity<Order> updateOrderStatus(@Parameter(description = "ID of the order to update") @PathVariable Long id,
                                                  @Parameter(description = "New status for the order (e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)", example = "SHIPPED") @RequestParam OrderStatus newStatus) {
        logger.info("Received request to update status for order ID {} to: {}", id, newStatus);
        try {
            Optional<Order> updatedOrder = orderService.updateOrderStatus(id, newStatus);
            return updatedOrder.map(ResponseEntity::ok)
                               .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            logger.error("Error updating order status for ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error updating order status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get orders dashboard metrics", description = "Provide orders statistics for ADMIN dashboard. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders statistics",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderStatsResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/dashboard/metrics")
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')")
    public ResponseEntity<OrderStatsResponse> getDashboardMetrics() {
        logger.info("Received request for dashboard statistics.");

        return ResponseEntity.ok(orderService.getDashboardMetrics());
    }

    @Operation(summary = "Get order counts by status",
            description = "Returns a map of the total number of orders for each status. This is a dashboard metric.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved order status counts"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @GetMapping("/status-count")
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')")
    public ResponseEntity<Map<OrderStatus, Long>> getOrdersByStatusCount() {
        logger.info("Received request to get order counts by status.");
        Map<OrderStatus, Long> counts = orderService.getOrdersByStatusCount();
        return ResponseEntity.ok(counts);
    }

}
