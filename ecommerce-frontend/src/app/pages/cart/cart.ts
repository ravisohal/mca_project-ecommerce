import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart';

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
  items = this.cartService.items();
  total = this.cartService.total();

  removeFromCart(item: any) {
    this.cartService.remove(item.product.id);
  }

  setQty(item: any, qty: number) {
    this.cartService.setQuantity(item.product.id, qty);
  }

  checkout() {
    this.cartService.checkout().subscribe({
      next: () => alert('Order placed!'),
      error: (err) => alert('Checkout failed: ' + (err?.error?.message ?? 'Unknown error')),
    });
  }
}
