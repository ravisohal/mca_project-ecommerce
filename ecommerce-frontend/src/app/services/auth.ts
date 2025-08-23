import { Injectable, inject, signal } from '@angular/core';
import { ApiService } from './api';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly loginUrl = '/auth/login';

  // Signals for auth state
  private readonly _user = signal<User | null>(null);
  private readonly _token = signal<string | null>(null);

  user = this._user.asReadonly();
  token = this._token.asReadonly();

  login(email: string, password: string) {
    return this.api.post<{ user: User; token: string }>(this.loginUrl, { email, password })
      .subscribe(({ user, token }) => {
        this._user.set(user);
        this._token.set(token);
        localStorage.setItem('token', token);
      });
  }

  logout() {
    this._user.set(null);
    this._token.set(null);
    localStorage.removeItem('token');
  }

  register(arg0: { username: string | null | undefined; email: string | null | undefined; password: string | null | undefined; }) {
    throw new Error('Method not implemented.');
  }
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated() {
    return this._token() !== null;
  }
}
