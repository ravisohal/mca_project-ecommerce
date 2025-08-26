import { Injectable, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Order } from '../models/order';
import { Paged } from '../models/paged';
import { map, Observable } from 'rxjs';
import { ApiService } from './api';
import { PageResponse } from '../models/page-response';


@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiService = inject(ApiService);
  private baseUrl = `/orders`;

  create(userId: number, shippingAddressId: number): Observable<Order> {
    return this.apiService.post<Order>(`${this.baseUrl}/place`, { userId, shippingAddressId }).pipe(
                map((response: Order) => response)
            );
  }

  getById(id: number): Observable<Order> {
    return this.apiService.get<Order>(`${this.baseUrl}/${id}`).pipe(
                map((response: Order) => response)
            );
  }

  updateStatus(id: number, status: string) {
    return this.apiService.put<Order>(`${this.baseUrl}/${id}/status`, { status });
  }

list(opts: { page?: number; size?: number; status?: string } = {}): Observable<PageResponse<Order>> {
    let p = new HttpParams();
    if (opts.page != null) p = p.set('page', String(opts.page) || '1');
    if (opts.size != null) p = p.set('size', String(opts.size) || '20');
    if (opts.status) p = p.set('status', opts.status);
    return this.apiService.get<PageResponse<Order>>(this.baseUrl, { params: p });
  }

  listByUser(userId: number, opts: { page?: number; size?: number; status?: string } = {}): Observable<PageResponse<Order>> {
    let p = new HttpParams();
    if (opts.page != null) p = p.set('page', String(opts.page) || '1');
    if (opts.size != null) p = p.set('size', String(opts.size) || '20');
    if (opts.status) p = p.set('status', opts.status);
    return this.apiService.get<PageResponse<Order>>(`${this.baseUrl}/user/${userId}`, { params: p });
  }

}
