import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart';
import { CartItem } from '../../models/cart-item';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CartComponent {
  private cartService = inject(CartService);
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
  }

  setQty(item: any, qty: number) {
    if (qty <= 0) {
      this.cartService.remove(item.product.id);
    } else {
      this.cartService.setQuantity(item.product.id, qty);
    }
    this.refreshCart();
  }

  clearCart() {
    this.cartService.clear();
    this.refreshCart();
  }

  checkout() {
    this.cartService.checkout().subscribe({
      next: () => alert('Order placed!'),
      error: (err) => alert('Checkout failed: ' + (err?.error?.message ?? 'Unknown error')),
    });
  }
}