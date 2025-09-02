import { Component, ChangeDetectionStrategy, Input, Output, inject, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../models/product';
import { CartService } from '../../services/cart';
import { InteractionType } from '../../models/interaction-type';
import { NotificationService } from '../../services/notification';
import { InteractionService } from '../../services/interaction';
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
  private interactionService = inject(InteractionService);
  private notificationService = inject(NotificationService);

  onAddToCart(): void {
    this.cartService.add(this.product);
    this.interactionService.log(InteractionType.ADD_TO_CART, this.product?.id || 0);
    this.notificationService.success('Product added to cart');
  }

  handleClick() {
    this.clicked.emit(this.product);
  }
  
}
