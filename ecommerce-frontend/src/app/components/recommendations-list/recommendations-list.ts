import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RecommendationsService } from '../../services/recommendations';
import { Product } from '../../models/product';
import { NotificationService } from '../../services/notification';
import { CartService } from '../../services/cart';

@Component({
  selector: 'app-recommendations-list',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './recommendations-list.html',
  styleUrls: ['./recommendations-list.scss']
})
export class RecommendationsListComponent implements OnInit {
  private cartService = inject(CartService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  recommendations = signal<Product[]>([]);
  isLoading = signal(false);
  error = signal('');

  constructor(private recommendationsService: RecommendationsService) {}

  ngOnInit(): void {
    this.fetchRecommendations();
  }

  fetchRecommendations(): void {
    this.isLoading.set(true);
    this.recommendationsService.getRecommendations().subscribe({
      next: (products) => {
        this.recommendations.set(products);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Could not load recommendations at this time. Sorry for the inconvenience.');
        this.isLoading.set(false);
        console.error(err);
      }
    });
  }

  // Optional: Methods for manual scrolling if needed for a specific layout
  scrollLeft(): void {
    const container = document.querySelector('.recommendations-list');
    if (container) {
      container.scrollBy({ left: -200, behavior: 'smooth' });
    }
  }

  scrollRight(): void {
    const container = document.querySelector('.recommendations-list');
    if (container) {
      container.scrollBy({ left: 200, behavior: 'smooth' });
    }
  }

  onAddToCart(product: Product): void {
    this.cartService.add(product);
    this.notificationService.success('Product added to cart');
  }

  onProductClick(product: Product) {
    this.router.navigate(['/products', product.id]); 
  }

}