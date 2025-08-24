import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { UserService } from '../../services/user';
import { User } from '../../models/user';
import { Address } from '../../models/address';

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterModule, ReactiveFormsModule],
  templateUrl: './address.html',
  styleUrl: './address.scss'
})
export class AddressComponent implements OnInit {
  private fb = inject(FormBuilder);
  public authService = inject(AuthService);
  private userService = inject(UserService);
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

  onSaveAddress(): void {
    if (this.shippingAddressForm.valid) {
      console.log('Saving address:', this.shippingAddressForm.value);
    }
    if (this.billingAddressForm.valid) {
      console.log('Saving address:', this.billingAddressForm.value);
    }
  }
}
