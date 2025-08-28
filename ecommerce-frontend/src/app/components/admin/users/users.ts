import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../../models/user';
import { UserService } from '../../../services/user';
import { NotificationService } from '../../../services/notification';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './users.html',
  styleUrls: ['./users.scss'],
})
export class AdminUsersComponent implements OnInit {
  private userService = inject(UserService);
  private formBuilder = inject(FormBuilder);
  private notificationService = inject(NotificationService);

  users = signal<User[]>([]);
  showEditor = signal<boolean>(false);
  editId = signal<number | null>(null);


  form = this.formBuilder.group({
    username: ['', Validators.required],
    password: [''],
    firstname: ['', Validators.required],
    lastname: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    role: ['customer', Validators.required],

    shippingAddress: this.formBuilder.group ({
      id: [0],
      street: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required]
    }),

    billingAddress: this.formBuilder.group({
      id: [0],
      street: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required]
    })

  });

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => this.users.set(data),
      error: (err) => console.error('Error fetching users', err),
    });
  }

  editUser(user: User) {
    if (user) {
      this.editId.set(user.id);
      this.form.patchValue({
        username: user.username,
        firstname: user.firstname,
        lastname: user.lastname,
        email: user.email,
        phoneNumber: user.phoneNumber,
        role: user.role,
        shippingAddress: {
          id: user.shippingAddress?.id,
          street: user.shippingAddress?.street,
          city: user.shippingAddress?.city,
          state: user.shippingAddress?.state,
          postalCode: user.shippingAddress?.postalCode,
          country: user.shippingAddress?.country,
        },
        billingAddress: {
          id: user.billingAddress?.id,
          street: user.billingAddress?.street,
          city: user.billingAddress?.city,
          state: user.billingAddress?.state,
          postalCode: user.billingAddress?.postalCode,
          country: user.billingAddress?.country,
        }
      });

    } else {
      this.editId.set(null);
      this.form.reset({ role: 'customer' });
    }
    this.showEditor.set(true);
  }

  save() {
    if (this.form.invalid) return;
    const updateUser: any = { ...this.form.value };

    const id = this.editId();
    const req = this.userService.updateUserProfile(id || 0, updateUser);

    req.subscribe({
      next: () => {
        this.showEditor.set(false);
        this.loadUsers();
        this.notificationService.success('User saved successfully.');
      },
      error: (err) => this.notificationService.error('Save failed: ' + (err?.error?.message ?? 'Unknown'))
    });
  }

}
