import { inject, Injectable } from '@angular/core';
import { Product } from '../models/product';
import { Observable } from 'rxjs';
import { ApiService } from './api';
import { map } from 'rxjs/operators';
import { PageResponse } from '../models/page-response';
import { Category } from '../models/category';

@Injectable({ providedIn: 'root' })
export class ProductService {

  private readonly apiService = inject(ApiService);
  private readonly baseUrl = '/products'; 
  
  getAll(): Observable<Product[]> {
    return this.apiService.get<Product[]>(this.baseUrl);
  }

  getAllCategories(): Observable<Category[]> {
    return this.apiService.get<Category[]>('/categories');
  }

  list(params: { page?: number; size?: number; sort?: any; category?: string; name?: string; }): Observable<PageResponse<Product>> {
    let url = `${this.baseUrl}?page=${params.page || 0}&size=${params.size || 20}`;
    
    if (params.sort) {
      let sortValue = params.sort;

      if (sortValue === 'newest') {
        sortValue = 'createdAt,desc';
      } else if (sortValue === 'priceAsc') {
        sortValue = 'price,asc';
      } else if (sortValue === 'priceDesc') {
        sortValue = 'price,desc';
      }

      url += `&sort=${sortValue}`;
    }
    
    if (params.category && params.category !== 'all') {
      url += `&category=${params.category}`;
    }
    if (params.name) {
      url += `&name=${params.name}`;
    }
    
    return this.apiService.get<PageResponse<Product>>(url);
  }

  getById(id: number): Observable<Product> {
    return this.apiService.get<Product>(`${this.baseUrl}/${id}`);
  }

  getProductsByCategoryName(categoryName: string): Observable<Product[]> {
    return this.apiService.get<Product[]>(`${this.baseUrl}/category/${categoryName}`);
  }

  create(product: Product): Observable<Product> {
    return this.apiService.post<Product>(this.baseUrl, product);
  }

  update(id: number, product: Product): Observable<Product> {
    return this.apiService.put<Product>(`${this.baseUrl}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  getLowStockProducts(threshold: number): Observable<Product[]> {
    return this.apiService.get<Product[]>(`${this.baseUrl}/low-stock?threshold=${threshold}`);
  }
}
