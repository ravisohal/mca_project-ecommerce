import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Category } from '../../../models/category';
import { CategoryService } from '../../../services/category';
import { NotificationService } from '../../../services/notification';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './categories.html',
  styleUrls: ['./categories.scss'],
})
export class AdminCategoriesComponent implements OnInit {
  private categoryService = inject(CategoryService);
  private formBuilder = inject(FormBuilder);
  private notificationService = inject(NotificationService);

  categories = signal<Category[]>([]);
  showEditor = signal<boolean>(false);
  editId = signal<number | null>(null);


  form = this.formBuilder.group({
    name: ['', [Validators.required]],
    description: ['']
  });

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => this.categories.set(data),
      error: (err) => console.error('Error fetching categories', err),
    });
  }

  openAddCategoryModal() {
    this.editId.set(null);
    this.form.reset({ name: '', description: '' });
    this.showEditor.set(true);
  }

  editCategory(category: Category) {
    this.editId.set(category.id);
    this.form.reset({
      name: category.name,
      description: category.description ?? '',
    });
    this.showEditor.set(true);
  }

  save() {
    if (this.form.invalid) return;
    const payload: any = { ...this.form.value };

    const id = this.editId();
    const req = id == null ? this.categoryService.create(payload) : this.categoryService.update(id, payload);

    req.subscribe({
      next: () => {
        this.showEditor.set(false);
        this.loadCategories();
        this.notificationService.success('Category saved successfully.');
      },
      error: (err) => this.notificationService.error('Save failed: ' + (err?.error?.message ?? 'Unknown'))
    });
  }

  /**
   * Handles the delete category action.
   * @param categoryId The ID of the category to delete.
   */
  deleteCategory(categoryId: number) {
    if (confirm('Are you sure you want to delete this category?')) {
      this.categoryService.delete(categoryId).subscribe({
        next: () => {
          this.loadCategories();
          this.notificationService.success('Category deleted successfully.');
        },
        error: (err) => this.notificationService.error('Delete failed: ' + (err?.error?.message ?? 'Unknown'))
      });
    }
  }
}
