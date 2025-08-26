package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.Cart;
import com.sohal.mca.project.ecommerce_backend.model.Address;
import com.sohal.mca.project.ecommerce_backend.model.CartItem;
import com.sohal.mca.project.ecommerce_backend.model.Order;
import com.sohal.mca.project.ecommerce_backend.model.OrderItem;
import com.sohal.mca.project.ecommerce_backend.model.OrderStatus;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.repository.OrderRepository;
import com.sohal.mca.project.ecommerce_backend.repository.AddressRepository;
import com.sohal.mca.project.ecommerce_backend.repository.OrderItemRepository;
import com.sohal.mca.project.ecommerce_backend.repository.ProductRepository;
import com.sohal.mca.project.ecommerce_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-27
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: Service class for managing orders in the e-commerce application.
 * This class handles the business logic related to order processing, including creating orders,
 * updating order statuses, and retrieving order details.
 * It interacts with the OrderRepository and other related repositories to perform CRUD operations.
 */


@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService; // To interact with the cart
    private final UserRepository userRepository;
    private final ProductRepository productRepository; // To update product stock
    private final AddressRepository addressRepository; // Inject AddressRepository

    /**
     * Constructor for OrderService.
     * Initializes the service with the required repositories.
     * @param orderRepository The repository for Order entities.
     * @param orderItemRepository The repository for OrderItem entities.
     * @param cartService The service to manage user carts.
     * @param userRepository The repository for User entities.
     * @param productRepository The repository for Product entities.
     * @param addressRepository The repository for Address entities.
     */
    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        CartService cartService, UserRepository userRepository,
                        ProductRepository productRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        logger.info("OrderService initialized.");
    }

    /**
     * Places a new order from the user's cart.
     * This involves creating an Order, populating OrderItems, updating product stock, and clearing the cart.
     * @param userId The ID of the user placing the order.
     * @param shippingAddressId The ID of the shipping address for the order.
     * @return The newly created Order.
     * @throws IllegalArgumentException if user not found, cart is empty, insufficient stock, or shipping address not found.
     */
    @Transactional
    public Order placeOrder(Long userId, Long shippingAddressId) {
        logger.info("Attempting to place order for user ID: {} with shipping address ID: {}", userId, shippingAddressId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found during order placement.", userId);
                    return new IllegalArgumentException("User not found.");
                });

        Address shippingAddress = addressRepository.findById(shippingAddressId)
                .orElseThrow(() -> {
                    logger.error("Shipping address with ID {} not found for order placement.", shippingAddressId);
                    return new IllegalArgumentException("Shipping address not found.");
                });

        Cart cart = cartService.getOrCreateCart(userId); // Get the user's cart
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()) {
            logger.warn("Order placement failed: Cart is empty for user ID: {}", userId);
            throw new IllegalArgumentException("Cannot place order with an empty cart.");
        }

        Order order = new Order();
        order.setUserid(user.getId());
        order.setShippingAddress(shippingAddress); // Set the Address object
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Set initial status to PENDING

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // Process each item in the cart
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer orderedQuantity = cartItem.getQuantity();

            if (product.getStockQuantity() < orderedQuantity) {
                logger.error("Insufficient stock for product ID {} ({}). Requested: {}, Available: {}. Order not placed.",
                             product.getId(), product.getName(), orderedQuantity, product.getStockQuantity());
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(orderedQuantity);
            orderItem.setPrice(cartItem.getPriceAtAddition());
            orderItem.setDiscount(product.getDiscount());
            orderItems.add(orderItem);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - orderedQuantity);
            productRepository.save(product); // Persist updated stock
            logger.debug("Updated stock for product ID {} to {}. Original: {}, Ordered: {}", product.getId(), product.getStockQuantity(), (product.getStockQuantity() + orderedQuantity), orderedQuantity);

            totalAmount = totalAmount.add(cartItem.getTotal());
        }

        order.setOrderItems(orderItems); 
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order); 
        orderItems.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        // Clear the user's cart after successful order placement
        cartService.clearCart(userId);
        logger.info("Order ID {} placed successfully for user ID {}. Total amount: {}", savedOrder.getId(), userId, totalAmount);
        return savedOrder;
    }

    /**
     * Retrieves an order by its ID.
     * @param orderId The ID of the order.
     * @return An Optional containing the order if found, or empty if not.
     */
    public Optional<Order> getOrderById(Long orderId) {
        logger.debug("Attempting to retrieve order with ID: {}", orderId);
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            logger.info("Order with ID {} found.", orderId);
        } else {
            logger.warn("Order with ID {} not found.", orderId);
        }
        return order;
    }

    /**
     * Retrieves all orders for a specific user.
     * @param userId The ID of the user.
     * @return A list of orders placed by the user.
     * @throws IllegalArgumentException if user not found.
     */
    public List<Order> getOrdersByUser(Long userId) {
        logger.debug("Attempting to retrieve orders for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found while fetching orders.", userId);
                    return new IllegalArgumentException("User not found.");
                });
        List<Order> orders = orderRepository.findByUserid(user.getId());
        logger.info("Retrieved {} orders for user ID: {}.", orders.size(), userId);
        return orders;
    }

    /**
     * Updates the status of an order.
     * @param orderId The ID of the order to update.
     * @param newStatus The new status for the order.
     * @return The updated order, or empty Optional if not found.
     */
    @Transactional
    public Optional<Order> updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("Attempting to update status for order ID {} to: {}", orderId, newStatus);
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(newStatus);
                    Order updatedOrder = orderRepository.save(order);
                    logger.info("Order ID {} status updated to: {}", orderId, newStatus);
                    return updatedOrder;
                });
    }

    /**
     * Retrieves all orders (admin function).
     * @return A list of all orders.
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        logger.debug("Attempting to retrieve all orders (admin).");
        Page<Order> orders = orderRepository.findAll(pageable);
        logger.info("Retrieved {} total orders.", orders.getTotalElements());
        return orders;
    }

    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        logger.debug("Attempting to retrieve orders for user ID: {} with pagination.", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found while fetching orders.", userId);
                    return new IllegalArgumentException("User not found.");
                });
        Page<Order> orders = orderRepository.findByUserid(user.getId(), pageable);
        logger.info("Retrieved {} orders for user ID: {}.", orders.getTotalElements(), userId);
        return orders;
    }

}
