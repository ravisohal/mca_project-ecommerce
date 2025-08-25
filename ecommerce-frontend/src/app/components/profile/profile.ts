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
  imports: [CommonModule, RouterModule, RouterLink, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  protected authService = inject(AuthService);
  protected userService = inject(UserService);
  protected user = this.authService.user();

  profileForm = this.fb.group({
    username: [{ value: '', disabled: true }, Validators.required],
    email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    phoneNumber: [''],
    bio: ['']
  });

  ngOnInit(): void {
    if (this.user) {
      this.profileForm.patchValue(this.user as any);
    }
  }

  onSaveProfile(): void {
    if (this.profileForm.valid && this.user) {
      // Create a filtered version of the form value to remove nulls
      const filteredFormValue = Object.fromEntries(
        Object.entries(this.profileForm.value).filter(([, value]) => value !== null)
      ) as Partial<User>;
      
      const updatedUser: Partial<User> = {
        ...this.user,
        ...filteredFormValue
      };

      console.log('Saving profile:', updatedUser);

      this.userService.updateUserProfile(this.user!.id, updatedUser).subscribe({
        next: (res) => console.log('Profile updated', res),
        error: (err) => console.error('Error updating profile:', err)
      });
    }
  }
  
}
