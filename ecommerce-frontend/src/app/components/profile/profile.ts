import { NotificationService } from './../../services/notification';
import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { UserService } from '../../services/user';
import { User } from '../../models/user';
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent implements OnInit {
  protected fb = inject(FormBuilder);
  protected authService = inject(AuthService);
  protected userService = inject(UserService);
  protected user = this.authService.user();
  protected notificationService = inject(NotificationService);

  isFormVisible = signal(false);

  profileForm = this.fb.group({
    username: [{ value: '', disabled: true }, Validators.required],
    firstname: ['', Validators.required],
    lastname: ['', Validators.required],
    email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
    phoneNumber: ['']
  });

  ngOnInit(): void {
    if (this.user) {
      this.profileForm.patchValue(this.user as any);
    }
  }

  toggleForm(): void {
    this.isFormVisible.update(val => !val);
  }

  onSaveProfile(): void {
    this.toggleForm();
    if (this.profileForm.valid && this.user) {

      const filteredFormValue = Object.fromEntries(
        Object.entries(this.profileForm.value).filter(([, value]) => value !== null)
      ) as Partial<User>;

      const updatedUser: Partial<User> = {
        ...this.user,
        ...filteredFormValue
      }

      this.userService.updateUserProfile(this.user!.id, updatedUser).subscribe({
        next: (res: User) => {
              this.authService.user.set(res);
              this.notificationService.success('Profile updated successfully.');
        },
        error: (err) => {
          this.notificationService.error('Error updating profile: ' + (err?.error?.message ?? 'Unknown error'));
        }
      });
    }
    this.toggleForm();
  }

}
