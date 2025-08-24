import { Address } from "./address";
import { User } from "./user";
import { OrderItem } from "./order-item";
import { OrderStatus } from "./order-status";

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: Order class for the e-commerce application.
 * Represents an order in the e-commerce application.
 */

export interface Order {
  id: number;
  user: User;
  orderDate: Date;
  status: OrderStatus;
  totalAmount: number;
  customerEmail?: string;
  shippingAddress?: Address;
  orderItems: OrderItem[];
  createdAt: Date;
  updatedAt: Date;
}