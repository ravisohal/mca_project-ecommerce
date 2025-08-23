import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="container"><h2 class="text-2xl font-semibold mb-4 capitalize">categories</h2><p>Coming soon: CRUD, filters, pagination.</p></div>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminCategoriesComponent { }
