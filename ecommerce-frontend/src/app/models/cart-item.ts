import { Cart } from "./cart";
import { Product } from "./product";

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: CartItem class for the e-commerce application.
 * Represents a cart item in the e-commerce application.
 */

export interface CartItem {
  id: string;
  product: Product;
  quantity: number;
  priceAtAddition: number;
  discountAtAddition: number;
  total: number;
}
