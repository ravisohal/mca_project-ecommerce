import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { UserService } from '../../services/user';
import { NotificationService } from '../../services/notification';
import { Address } from '../../models/address';
import { User } from '../../models/user';

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterModule, ReactiveFormsModule],
  templateUrl: './address.html',
  styleUrl: './address.scss'
})
export class AddressComponent implements OnInit {
  protected fb = inject(FormBuilder);
  protected authService = inject(AuthService);
  protected userService = inject(UserService);
  protected notificationService = inject(NotificationService);  
  protected user = this.authService.user();

  protected shippingAddress: Address | null = null;
  protected billingAddress: Address | null = null;

  isFormVisible = signal(false);

  ngOnInit(): void {
    if (this.user) {
      this.shippingAddress = this.user?.shippingAddress || null;
      this.billingAddress = this.user?.billingAddress || null;
      this.shippingAddressForm.patchValue(this.shippingAddress as any);
      this.billingAddressForm.patchValue(this.billingAddress as any);
    }
  }

  shippingAddressForm = this.fb.group({
    street: [this.shippingAddress?.street || '', Validators.required],
    city: [this.shippingAddress?.city || '', Validators.required],
    state: [this.shippingAddress?.state || '', Validators.required],
    postalCode: [this.shippingAddress?.postalCode || '', Validators.required],
    country: [this.shippingAddress?.country || '', Validators.required],
  });

  billingAddressForm = this.fb.group({
    street: [this.billingAddress?.street || '', Validators.required],
    city: [this.billingAddress?.city || '', Validators.required],
    state: [this.billingAddress?.state || '', Validators.required],
    postalCode: [this.billingAddress?.postalCode || '', Validators.required],
    country: [this.billingAddress?.country || '', Validators.required],
  });

  toggleForm(): void {
    this.isFormVisible.update(val => !val);
  }

  onSaveAddress(isShippingAddress: boolean): void {
    this.toggleForm();

    if (isShippingAddress && this.shippingAddressForm.valid && this.user) {

      const filteredFormValue = Object.fromEntries(
        Object.entries(this.shippingAddressForm.value).filter(([, value]) => value !== null)
      ) as Partial<Address>;

      const updatedAddress: Partial<Address> = {
        ...this.shippingAddress,
        ...filteredFormValue
      };

      this.userService.updateUserAddress(this.shippingAddress!.id, updatedAddress).subscribe({
        next: (res: Address) => {
          this.authService.user.update(user => user ? { ...user, shippingAddress: res } : user);
          this.notificationService.success('Shipping address updated successfully.');
        },
        error: (err) => {
          this.notificationService.error('Error updating shipping address: ' + (err?.error?.message ?? 'Unknown error'));
        }
      });

    }

    if (!isShippingAddress && this.billingAddressForm.valid && this.user) {

      const filteredFormValue = Object.fromEntries(
        Object.entries(this.billingAddressForm.value).filter(([, value]) => value !== null)
      ) as Partial<Address>;

      const updatedAddress: Partial<Address> = {
        ...this.billingAddress,
        ...filteredFormValue
      };

      this.userService.updateUserAddress(this.billingAddress!.id, updatedAddress).subscribe({
        next: (res: Address) => {
          this.authService.user.update(user => user ? { ...user, billingAddress: res } : user);
          this.notificationService.success('Billing address updated successfully.');
        },
        error: (err) => {
          this.notificationService.error('Error updating billing address: ' + (err?.error?.message ?? 'Unknown error'));
        }
      });

    }
    this.toggleForm();
  }
}
