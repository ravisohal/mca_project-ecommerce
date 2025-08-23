
/**
 * Author: Ravi Sohal
 * Date: 2025-08-19
 * Post: MCA Project - E-commerce Frontend
 * Description: interface Paged 
 */

export interface Paged<T> {
  content: T[];
  number: number;       // current page
  size: number;         // page size
  totalElements: number;
  totalPages: number;
}