import { NotificationService } from './../../services/notification';
import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart';
import { CartItem } from '../../models/cart-item';
import { Order } from '../../models/order';
import { RecommendationsListComponent } from '../recommendations-list/recommendations-list';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RecommendationsListComponent],
  templateUrl: './cart.html',
  styleUrls: ['./cart.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CartComponent {
  private cartService = inject(CartService);
  private notificationService = inject(NotificationService);

  items = signal<CartItem[]>([]);
  total = signal(0);

  constructor() {
    this.refreshCart();
  }

  refreshCart() {
    this.items.set(this.cartService.items());
    this.total.set(this.cartService.total());
  }

  removeFromCart(item: any) {
    this.cartService.remove(item.product.id);
    this.refreshCart();
    this.notificationService.success('Item removed from cart');
  }

  setQty(item: any, qty: number) {
    if (qty <= 0) {
      this.cartService.remove(item.product.id);
      this.notificationService.success('Item removed from cart');
    } else {
      this.cartService.setQuantity(item.product.id, qty);
      this.notificationService.success('Quantity updated');
    }
    this.refreshCart();
  }

  clearCart() {
    this.cartService.clear();
    this.refreshCart();
    this.notificationService.success('Cart cleared');
  }

  checkout() {
    this.cartService.checkout().subscribe({
      next: (response: Order) => {
        this.cartService.clear();
        this.refreshCart();
        this.notificationService.success(`Order# ${response.id} placed!`);
      },
      error: (err) => this.notificationService.error('Checkout failed: ' + (err?.error?.message ?? 'Unknown error')),
    });
  }
}