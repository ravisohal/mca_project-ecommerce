import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderService } from '../../services/order';
import { Order } from '../../models/order';
import { OrderStatus } from '../../models/order-status';
import { take } from 'rxjs';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDetailComponent implements OnInit {
  protected orderService = inject(OrderService);
  protected route = inject(ActivatedRoute);
  protected readonly loading = signal<boolean>(true);
  protected readonly order = signal<Order | null>(null);
  protected readonly OrderStatus = OrderStatus;

  ngOnInit() {
    this.route.paramMap.pipe(take(1)).subscribe(params => {
      const orderId = params.get('id');
      if (orderId) {
        this.orderService.getById(parseInt(orderId, 10)).subscribe({
          next: (res) => {
            this.order.set(res);
            this.loading.set(false);
          },
          error: (err) => {
            console.error('Failed to fetch order details', err);
            this.loading.set(false);
            // Optionally set order to null if an error occurs
            this.order.set(null);
          }
        });
      } else {
        this.loading.set(false);
      }
    });
  }
}
