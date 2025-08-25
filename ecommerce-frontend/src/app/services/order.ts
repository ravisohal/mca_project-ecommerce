import { Injectable, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Order } from '../models/order';
import { Paged } from '../models/paged';
import { Observable } from 'rxjs';
import { ApiService } from './api';


@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiService = inject(ApiService);
  private baseUrl = `/orders`;

  create(userId: number, shippingAddressId: number) {
    return this.apiService.post<Order>(`${this.baseUrl}/place`, { userId, shippingAddressId });
  }

  getById(id: number) {
    return this.apiService.get<Order>(`${this.baseUrl}/${id}`);
  }

  // Admin
  list(opts: { page?: number; size?: number; status?: string } = {}): Observable<Paged<Order>> {
    let p = new HttpParams();
    if (opts.page != null) p = p.set('page', String(opts.page));
    if (opts.size != null) p = p.set('size', String(opts.size));
    if (opts.status) p = p.set('status', opts.status);
    return this.apiService.get<Paged<Order>>(this.baseUrl, { params: p });
  }

  updateStatus(id: number, status: string) {
    return this.apiService.put<Order>(`${this.baseUrl}/${id}/status`, { status });
  }

  listByUser(userId: string | number) {
  return this.apiService.get<any>(`${this.baseUrl}/user/${userId}`);
  }

}
