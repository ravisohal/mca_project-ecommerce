import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../services/cart';

@Component({
  selector: 'app-layout',
  imports: [RouterModule, NgOptimizedImage],
  templateUrl: './layout.html',
  styleUrl: './layout.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'layout-container'
  }
})
export class LayoutComponent {
  public cartService = inject(CartService);
}

