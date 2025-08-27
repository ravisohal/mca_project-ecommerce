import { Component, ChangeDetectionStrategy, inject, OnInit, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product';
import { CartService } from '../../services/cart';
import { Product } from '../../models/product';
import { Category } from '../../models/category';
import { CardComponent } from '../../components/card/card';
import { toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, CardComponent],
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush 
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  currentPage = signal(0);
  pageSize = 20;
  isLastPage = signal(false);

  selectedCategory = signal<string>('all');
  searchTerm = signal<string>('');
  sortBy = signal<'priceAsc' | 'priceDesc' | 'newest'>('newest');

  private filters = signal({
    page: this.currentPage(),
    category: this.selectedCategory(),
    searchTerm: this.searchTerm(),
    sortBy: this.sortBy()
  });

  constructor() {
    this.loadCategories();

    toObservable(this.filters)
      .pipe(
        switchMap(filters => this.productService.list({ 
            page: filters.page, 
            size: this.pageSize, 
            sort: filters.sortBy, 
            category: encodeURIComponent(filters.category), 
            name: encodeURIComponent(filters.searchTerm) 
          }))
      )
      .subscribe(res => {
        this.products.set(res.content);
        this.currentPage.set(res.number);
        this.isLastPage.set(res.last);
      });
  }
  ngOnInit(): void {
    this.updateFilters();
  }

  loadCategories() {
    this.productService.getAllCategories().subscribe({
      next: (data) => this.categories.set(data),
    });
  }

  onSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value.trim();
    this.searchTerm.set(value);
    this.updateFilters();
  }

  onSortChange(event: Event) {
    const sortValue = (event.target as HTMLSelectElement).value as 'priceAsc' | 'priceDesc' | 'newest';
    this.sortBy.set(sortValue);
    this.updateFilters();
  }

  onCategoryChange(event: Event) {
    const categoryName = (event.target as HTMLSelectElement).value;
    this.selectedCategory.set(categoryName);
    this.updateFilters();
  }

  onProductClick(product: Product) {
    this.router.navigate(['/products', product.id]); 
  }

  addToCart(p: Product) {
    this.cartService.add(p, 1);
    this.notificationService.success('Product added to cart');
  }

  prevPage() {
    this.currentPage.set(this.currentPage() - 1);
    this.updateFilters();
  }

  nextPage() {
    this.currentPage.set(this.currentPage() + 1);
    this.updateFilters();
  }

  private updateFilters() {
    this.filters.set({
      page: this.currentPage(),
      category: this.selectedCategory(),
      searchTerm: this.searchTerm(),
      sortBy: this.sortBy()
    });
  }

}
