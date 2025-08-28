import { NotificationService } from './../../../services/notification';
import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order';
import { Order } from '../../../models/order';
import { OrderStatus } from '../../../models/order-status';


@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './admin-orders.html',
  styleUrl: './admin-orders.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminOrdersComponent implements OnInit {
  private orderService = inject(OrderService);
  private notificationService = inject(NotificationService);
  protected orderStatus = OrderStatus;
  protected orderStatusList = Object.values(OrderStatus);
  
  orders = signal<Order[]>([]);
  currentPage = signal(0);
  isLastPage = signal(false);
  totalPages = signal(0);
  pageSize = 20;

  ngOnInit() {
    this.load();
  }

  load(page = 0) {
    this.orderService.list({ page, size: this.pageSize }).subscribe((res) => {
      this.orders.set(res.content);
      this.currentPage.set(res.number);
      this.isLastPage.set(res.last);
      this.totalPages.set(res.totalPages);
    });
  }

  setStatus(o: Order, status: string) {
    this.orderService.updateStatus(o.id, status as OrderStatus).subscribe({
      next: (updated) => {
        this.orders.set(this.orders().map(x => x.id === updated.id ? { ...x, ...updated } : x));
        this.notificationService.success('Order status updated successfully.');
      },
      error: (err) => this.notificationService.error('Failed to update status: ' + (err?.error?.message ?? 'Unknown'))
    });
  }
}