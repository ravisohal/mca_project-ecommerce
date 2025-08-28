import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order';
import { ProductService } from '../../../services/product';
import { Product } from '../../../models/product';
import { Order } from '../../../models/order';
import { OrderStatus } from '../../../models/order-status';
import { forkJoin, map } from 'rxjs';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminDashboardComponent implements OnInit {
  private orderService = inject(OrderService);
  private productService = inject(ProductService);
  protected orderStatus = OrderStatus;

  // Dashboard Data Signals
  totalSales = signal<number | null>(null);
  totalOrders = signal<number | null>(null);
  recentOrders = signal<Order[]>([]);
  lowStockProducts = signal<Product[]>([]);
  ordersByStatusCount = signal<Map<string, number> | null>(null);
  loading = signal<boolean>(true);

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading.set(true);

    this.orderService.getDashboardMetrics().subscribe({
      next: (metrics) => {
        this.totalSales.set(metrics.totalSales);
        this.totalOrders.set(metrics.totalOrders);
      },
      error: (err) => console.error('Failed to load order metrics:', err)
    });

    this.orderService.list({ page: 0, size: 5, status: OrderStatus.PENDING }).subscribe({
      next: (res) => this.recentOrders.set(res.content),
      error: (err) => console.error('Failed to load recent orders:', err)
    });

    this.orderService.getOrdersByStatusCount().subscribe({
      next: (counts) => {
        this.ordersByStatusCount.set(counts);
      },
      error: (err) => console.error('Failed to load orders by status count:', err)
    });

    this.productService.getLowStockProducts(10).subscribe({
      next: (products) => this.lowStockProducts.set(products),
      error: (err) => console.error('Failed to load low stock products:', err),
      complete: () => this.loading.set(false)
    });

    this.loading.set(false);
  }
}