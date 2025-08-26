import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order';
import { AuthService } from '../../services/auth';
import { RouterLink } from '@angular/router';
import { Order } from '../../models/order';
import { Paged } from '../../models/paged';
import { OrderStatus } from '../../models/order-status';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-list.html',
  styleUrl: './order-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderListComponent implements OnInit {
  protected orderService = inject(OrderService);
  protected authService = inject(AuthService);
  protected readonly loading = signal<boolean>(true);
  protected readonly orders = signal<Order[]>([]);
  protected readonly totalPages = signal<number>(0);
  protected readonly currentPage = signal<number>(1);
  protected readonly pageSize = 20;
  protected readonly orderStatus = OrderStatus;

  ngOnInit() {
    this.fetchOrders(this.currentPage());
  }

  fetchOrders(page: number) {
    this.loading.set(true);
    const user = this.authService.user();
    if (!user || !user.id) {
      this.loading.set(false);
      return;
    }

    if (this.authService.isAdmin()) {
      this.orderService.list({ page: page - 1, size: this.pageSize }).subscribe({
        next: (res) => {
          this.orders.set(res.content ?? res as any);
          this.totalPages.set(res.totalPages || 0);
          this.currentPage.set((res.number || 0) + 1);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Failed to fetch orders', err);
          this.loading.set(false);
        }
      });
    } else {
      this.orderService.listByUser(user.id, { page: page - 1, size: this.pageSize }).subscribe({
        next: (res) => { 
          this.orders.set(res.content ?? res as any);
          this.totalPages.set(res.totalPages || 0);
          this.currentPage.set((res.number || 0) + 1);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Failed to fetch orders', err);
          this.loading.set(false);
        }
      });
    }
  }

  onPageChange(page: number) {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
      this.fetchOrders(page);
    }
  }

  protected get pagesArray(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i + 1);
  }
}
