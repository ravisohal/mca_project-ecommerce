import { Component, ChangeDetectionStrategy, inject, OnInit, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product';
import { CartService } from '../../services/cart';
import { Product } from '../../models/product';
import { Category } from '../../models/category';
import { CardComponent } from '../../components/card/card';

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

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  currentPage = signal(0);
  pageSize = 20;
  isLastPage = signal(false);

  selectedCategory = signal<string>('all');
  searchTerm = signal<string>('');
  sortBy = signal<'priceAsc' | 'priceDesc' | 'newest'>('newest');

  constructor() {
    this.loadCategories();
    effect(() => {
      const page = this.currentPage();
      const cat = this.selectedCategory();
      const q = this.searchTerm();
      const sort = this.sortBy();
      this.load();
    });
  }
  ngOnInit(): void {
    this.load();
  }

  load() {
    this.productService.list({ 
        page: this.currentPage(), 
        size: this.pageSize, sort: this.sortBy(), 
        category: encodeURIComponent(this.selectedCategory()), 
        name: encodeURIComponent(this.searchTerm()) 
      }).subscribe(res => {
        this.products.set(res.content);
        this.currentPage.set(res.number);
        this.isLastPage.set(res.last);
    });
  }

  loadCategories() {
    this.productService.getAllCategories().subscribe({
      next: (data) => this.categories.set(data),
    });
  }

  onSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value.trim();
    this.searchTerm.set(value);
    this.load();
  }

  onSortChange(event: Event) {
    const sortValue = (event.target as HTMLSelectElement).value as 'priceAsc' | 'priceDesc' | 'newest';
    this.sortBy.set(sortValue);
    this.load();
  }

  onCategoryChange(event: Event) {
    const categoryName = (event.target as HTMLSelectElement).value;
    this.selectedCategory.set(categoryName);
    this.load();
  }

  addToCart(p: Product) {
    this.cartService.add(p, 1);
  }

  prevPage() {
    this.currentPage.set(this.currentPage() - 1);
    this.load();
  }

  nextPage() {
    this.currentPage.set(this.currentPage() + 1);
    this.load();
  }
}
