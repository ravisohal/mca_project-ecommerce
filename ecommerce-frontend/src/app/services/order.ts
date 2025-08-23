import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Order } from '../models/order';
import { Paged } from '../models/paged';
import { Observable } from 'rxjs';
import { CreateOrderRequest } from '../models/create-order-request';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/orders`;

  // Customer
  create(req: CreateOrderRequest) {
    return this.http.post<Order>(this.base, req);
  }

  getById(id: number) {
    return this.http.get<Order>(`${this.base}/${id}`);
  }

  // Admin
  list(opts: { page?: number; size?: number; status?: string } = {}): Observable<Paged<Order>> {
    let p = new HttpParams();
    if (opts.page != null) p = p.set('page', String(opts.page));
    if (opts.size != null) p = p.set('size', String(opts.size));
    if (opts.status) p = p.set('status', opts.status);
    return this.http.get<Paged<Order>>(this.base, { params: p });
  }

  updateStatus(id: number, status: string) {
    return this.http.put<Order>(`${this.base}/${id}/status`, { status });
  }
}
