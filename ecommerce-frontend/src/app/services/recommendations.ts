import { Injectable, inject } from '@angular/core';
import { Product } from '../models/product';
import { ApiService } from './api';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class RecommendationsService {
  
  private readonly apiService = inject(ApiService);
  private authService = inject(AuthService);
  private readonly baseUrl = '/recommendations'; 

  getRecommendations() {
    return this.apiService.get<Product[]>(`${this.baseUrl}/${this.authService.user()?.id || 0}`);
  }
}
