import { Category } from "./category";

/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: Product interface for the e-commerce application.
 * Represents a product in the e-commerce application.
 */

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  discount: number;
  imageUrl?: string;
  stockQuantity: number;
  category: Category; 
}
