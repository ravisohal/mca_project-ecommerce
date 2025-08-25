import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../services/product';
import { Product } from '../../../models/product';
import { Category } from '../../../models/category';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-products.html',
  styleUrls: ['./admin-products.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminProductsComponent {
  private productService = inject(ProductService);
  private formBuilder = inject(FormBuilder);

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
      next: () => { this.showEditor.set(false); this.load(); },
      error: (err) => alert('Save failed: ' + (err?.error?.message ?? 'Unknown'))
    });
  }

  deleteProduct(id: number) {
    if (!confirm('Delete this product?')) return;
    this.productService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => alert('Delete failed: ' + (err?.error?.message ?? 'Unknown'))
    });
  }
}
