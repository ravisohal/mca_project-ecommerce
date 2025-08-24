import { Component, ChangeDetectionStrategy, Input, Output, inject, EventEmitter } from '@angular/core';
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
  @Input({ required: true }) product!: Product;
  @Output() clicked = new EventEmitter<any>();

  private cartService = inject(CartService);

  onAddToCart(): void {
    this.cartService.add(this.product);
  }

  handleClick() {
    this.clicked.emit(this.product);
  }
}
