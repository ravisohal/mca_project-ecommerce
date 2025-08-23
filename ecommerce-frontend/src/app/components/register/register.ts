import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.html'
})
export class RegisterComponent {
  registerForm;
  loading = false;
  error = '';
  success = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  submit() {
    if (this.registerForm.invalid) return;
    this.loading = true;
    this.error = '';
    const { username, email, password } = this.registerForm.value;
    this.authService.register({
      username: username ?? '',
      email: email ?? '',
      password: password ?? ''
    }).subscribe({
      next: (res) => {
        this.loading = false;
        this.success = true;
        this.router.navigate(['/login'], { queryParams: { registered: true } });
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Registration failed';
      }
    });
  }
}
