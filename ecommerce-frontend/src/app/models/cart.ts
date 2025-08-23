import { CartItem } from "./cart-item";
import { User } from "./user";

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Version: 1.0
 * Description: Cart class for the e-commerce application.
 * Represents a shopping cart in the e-commerce application.
 */

export interface Cart {
  id: string;
  user: User;
  cartItems: CartItem[];
  totalAmount: number;
}

