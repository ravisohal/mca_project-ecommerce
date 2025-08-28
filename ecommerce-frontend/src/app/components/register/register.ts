import { Address } from './../../models/address';
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html'
})
export class RegisterComponent {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  registerForm = this.fb.group({
      username: ['', Validators.required],
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phoneNumber: ['', Validators.required],
      shippingAddress: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        postalCode: ['', Validators.required],
        country: ['', Validators.required]
      }),
      billingAddress: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        postalCode: ['', Validators.required],
        country: ['', Validators.required]
      })
    });

  loading = false;
  error = '';
  success = false;

  constructor() { }

  submit() {
    if (this.registerForm.invalid) return;
    this.loading = true;
    this.error = '';

    const { username, firstname, lastname, email, password, phoneNumber, shippingAddress, billingAddress } = this.registerForm.value;
    this.authService.register({
      username: username ?? '',
      firstname: firstname ?? '',
      lastname: lastname ?? '',
      email: email ?? '',
      password: password ?? '',
      id: 0,
      phoneNumber: phoneNumber ?? '',
      shippingAddress: shippingAddress as Address,
      billingAddress: billingAddress as Address,
      role: 'customer'
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.success = true;
        setTimeout(() => {
          this.router.navigate(['/auth/login'], { queryParams: { registered: true } });
        }, 1500)
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Registration failed';
      }
    });
  }
}
