import { Component, ChangeDetectionStrategy, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../models/product';
import { CartService } from '../../services/cart';
@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card.html',
  styleUrl: './card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardComponent {
  @Input({ required: true }) product!: Product

  private cartService = inject(CartService);

  onAddToCart(): void {
    this.cartService.add(this.product);
  }
}
