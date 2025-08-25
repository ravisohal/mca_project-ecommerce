import { Address } from "./address";

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: User interface for the e-commerce application.
 * Represents a user in the e-commerce application.
 */

export interface User {
  id: number;
  username: string;
  password: string;
  email: string;
  phoneNumber: string;
  shippingAddress: Address;
  billingAddress: Address;
  role: "customer" | "admin";
}
