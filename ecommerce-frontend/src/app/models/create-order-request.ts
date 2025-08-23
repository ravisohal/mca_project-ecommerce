import { Address } from "./address";
import { OrderItem } from "./order-item";
import { OrderStatus } from "./order-status";
import { Product } from "./product";
import { User } from "./user";

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: CreateOrderRequest interface for the e-commerce application.
 * Represents a request to create a new order in the e-commerce application.
 */

export interface CreateOrderRequest {
    totalAmount: number;
    customerEmail?: string;
    items: { productId: number; productName: string; quantity: number; price: number; discount: number; }[];
}