import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../../services/order';
import { Order } from '../../../models/order';
import { OrderStatus } from '../../../models/order-status';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-orders.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminOrdersComponent {
  private orderService = inject(OrderService);
  protected orderStatus = OrderStatus;
  orders = signal<Order[]>([]);
  page = signal(0);

  ngOnInit() {
    this.load();
  }

  load(page = 0) {
    this.orderService.list({ page, size: 20 }).subscribe((res) => {
      this.orders.set(res.content);
      this.page.set(res.number);
    });
  }

  setStatus(o: Order, status: string) {
    this.orderService.updateStatus(o.id, status as OrderStatus).subscribe({
      next: (updated) => {
        this.orders.set(this.orders().map(x => x.id === updated.id ? { ...x, ...updated } : x));
      },
      error: (err) => alert('Failed to update status: ' + (err?.error?.message ?? 'Unknown'))
    });
  }
}