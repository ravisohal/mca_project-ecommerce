import { Product } from './product';
import { Order } from './order';

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: OrderItem interface for the e-commerce application.
 * Represents an order item in the e-commerce application.
 */

export interface OrderItem {
  id: number;
  order: Order;
  product: Product;
  name: string;
  quantity: number;
  price: number;
  discount: number;
  total: number;
}
