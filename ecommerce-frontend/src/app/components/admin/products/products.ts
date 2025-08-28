import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../services/product';
import { Product } from '../../../models/product';
import { Category } from '../../../models/category';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../services/notification'; 

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './products.html',
  styleUrls: ['./products.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminProductsComponent {
  private productService = inject(ProductService);
  private formBuilder = inject(FormBuilder);
  private notificationService = inject(NotificationService);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  showEditor = signal<boolean>(false);
  editId = signal<number | null>(null);

  form = this.formBuilder.group({
    name: ['', [Validators.required]],
    description: [''],
    price: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.min(0)]],
    imageUrl: [''],
    categoryId: [null as number | null, [Validators.required]]
  });

  ngOnInit() {
    this.load();
    this.productService.getAllCategories().subscribe(c => this.categories.set(c));
  }

  load() {
    this.productService.list({ page: 0, size: 50 }).subscribe(res => this.products.set(res.content));
  }

  openAddProductModal() {
    this.editId.set(null);
    this.form.reset({ name: '', description: '', price: 0, stock: 0, imageUrl: '', categoryId: null });
    this.showEditor.set(true);
  }

  editProduct(p: Product) {
    this.editId.set(p.id);
    this.form.reset({
      name: p.name,
      description: p.description ?? '',
      price: p.price,
      stock: p.stockQuantity ?? 0,
      imageUrl: p.imageUrl ?? '',
      categoryId: p.category?.id ?? null
    });
    this.showEditor.set(true);
  }

  save() {
    if (this.form.invalid) return;
    const payload: any = { ...this.form.value };

    // Map to backend expectation (category by id)
    if (payload.categoryId) payload.category = { id: payload.categoryId };
    delete payload.categoryId;

    const id = this.editId();
    const req = id == null ? this.productService.create(payload) : this.productService.update(id, payload);

    req.subscribe({
      next: () => {
        this.showEditor.set(false);
        this.load();
        this.notificationService.success('Product saved successfully.');
      },
      error: (err) => this.notificationService.error('Save failed: ' + (err?.error?.message ?? 'Unknown'))
    });
  }

  /**
   * Handles the delete product action.
   * @param productId The ID of the product to delete.
   */
  deleteProduct(productId: number) {
    if (confirm('Are you sure you want to delete this product?')) {
      this.productService.delete(productId).subscribe({
        next: () => {
          this.load();
          this.notificationService.success('Product deleted successfully.');
        },
        error: (err) => this.notificationService.error('Delete failed: ' + (err?.error?.message ?? 'Unknown'))
      });
    }
  }
}