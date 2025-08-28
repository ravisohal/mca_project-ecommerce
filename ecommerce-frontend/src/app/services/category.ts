import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api';
import { Category } from '../models/category';

@Injectable({ providedIn: 'root' })
export class CategoryService {

  private readonly apiService = inject(ApiService);
  private readonly baseUrl = '/categories';

  getAll(): Observable<Category[]> {
    return this.apiService.get<Category[]>(this.baseUrl);
  }

  getById(id: number): Observable<Category> {
    return this.apiService.get<Category>(`${this.baseUrl}/${id}`);
  }

  create(category: Category): Observable<Category> {
    return this.apiService.post<Category>(this.baseUrl, category);
  }

  update(id: number, category: Category): Observable<Category> {
    return this.apiService.put<Category>(`${this.baseUrl}/${id}`, category);
  }

  delete(id: number): Observable<void> {
    return this.apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

}
