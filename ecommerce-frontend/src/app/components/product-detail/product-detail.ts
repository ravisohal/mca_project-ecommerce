import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product';
import { CartService } from '../../services/cart';
import { InteractionService } from '../../services/interaction';
import { InteractionType } from '../../models/interaction-type';
import { NotificationService } from '../../services/notification';
import { firstValueFrom, take, tap } from 'rxjs';
import { Product } from '../../models/product';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.html',
  styleUrls: ['./product-detail.scss']
})
export class ProductDetailComponent implements OnInit {
  protected route = inject(ActivatedRoute);
  protected router = inject(Router);
  protected productService = inject(ProductService);
  protected cartService = inject(CartService);
  protected interactionService = inject(InteractionService);
  protected notificationService = inject(NotificationService);

  protected readonly product = signal<Product | null>(null);
  protected readonly loading = signal(false);

  constructor(

  ) { }

  async ngOnInit(): Promise<void> {
    this.route.paramMap.pipe(take(1)).subscribe(params => {
      const productId = params.get('id');
      if (productId) {
        this.productService.getById(parseInt(productId, 10)).subscribe({
          next: (res) => {
            this.product.set(res);
            this.loading.set(false);
          },
          error: (err) => {
            console.error('Failed to fetch product details', err);
            this.loading.set(false);
            this.product.set(null);
          }
        });
      } else {
        this.loading.set(false);
      }
    });
  }

  addToCart() {
    this.cartService.add(this.product()!);
    this.interactionService.log(InteractionType.ADD_TO_CART, this.product()?.id || 0);
    this.notificationService.success('Product added to cart');
  }

  goBack() {
    this.router.navigate(['/products']);
  }
}
