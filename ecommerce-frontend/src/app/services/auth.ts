import { Injectable, inject, signal } from '@angular/core';
import { ApiService } from './api';
import { User } from '../models/user';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiService = inject(ApiService);
  private readonly apiUrl = '/auth';

  // Signals for auth state
  private readonly _user = signal<User | null>(null);
  private readonly _token = signal<string | null>(null);

  user = this._user.asReadonly();
  token = this._token.asReadonly();

  login(username: string, password: string): Observable<any> {
    return this.apiService.post(`${this.apiUrl}/login`, { username, password });
  }

  register(user: { username: string; email: string; password: string }): Observable<any> {
    return this.apiService.post(`${this.apiUrl}/register`, user);
  }

  logout() {
    this._user.set(null);
    this._token.set(null);
    localStorage.removeItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated() {
    return this._token() !== null;
  }

}