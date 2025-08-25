import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order';
import { AuthService } from '../../services/auth';
import { Order } from '../../models/order';
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
  protected readonly OrderStatus = OrderStatus;

  ngOnInit() {
    const user = this.authService.user();
    if (!user || !user.id) {
      this.loading.set(false);
      return;
    }

    this.orderService.listByUser(user.id).subscribe({
      next: (res) => {
        const ordersArray = Array.isArray(res) ? res : res.items;
        this.orders.set(ordersArray || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch orders', err);
        this.loading.set(false);
      }
    });
  }
}
