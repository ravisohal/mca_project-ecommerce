import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order';
import { AuthService } from '../../services/auth';
import { RouterLink } from '@angular/router';
import { Order } from '../../models/order';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './customer-dashboard.html',
  styleUrls: ['./customer-dashboard.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerDashboardComponent {
  protected authService = inject(AuthService);
  protected orderService = inject(OrderService);
  protected readonly loading = signal<boolean>(true);
  protected readonly orders = signal<Order[]>([]);
  protected readonly user = this.authService.user();

  ngOnInit() {
    if (!this.user) {
      this.loading.set(false);
      return;
    }
    this.orderService.listByUser(this.user.id).subscribe({
      next: (res) => { this.orders.set(res.items ?? res as any); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}
