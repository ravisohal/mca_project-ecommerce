import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CartService } from '../../services/cart';
import { InteractionService } from '../../services/interaction';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.scss']
})
export class CheckoutComponent {
  private router = inject(Router);
  private authService = inject(AuthService);
  protected cartService = inject(CartService);
  private interactionsService = inject(InteractionService);

  ngOnInit() {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/auth/login'], { queryParams: { returnUrl: '/orders/checkout' } });
      return;
    }
    this.interactionsService.log({ type: 'CHECKOUT_START' });
  }

  placeOrder() {
    this.cartService.placeOrder().subscribe({
      next: () => {
        this.interactionsService.log({ type: 'CHECKOUT_COMPLETE' });
        this.router.navigate(['/user/dashboard']);
      }
    });
  }
}
