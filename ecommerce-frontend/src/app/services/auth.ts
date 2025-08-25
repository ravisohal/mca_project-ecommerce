import { Injectable, inject, signal, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Observable, of, switchMap } from 'rxjs';
import { ApiService } from './api';
import { User } from '../models/user';
import { UserService } from './user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiService = inject(ApiService);
  private readonly userService = inject(UserService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly authUrl = '/auth';
  private readonly usersUrl = '/users';

  user = signal<User | null>(null);
  token = signal<string | null>(null);

  isAuthenticated() {
    return this.token() !== null;
  }

  isAdmin() {
    return this.user()?.role === 'admin';
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    }
    return null;
  }

  private setUserAndToken(token: string) {
    localStorage.setItem('token', token);
    this.token.set(token);

    // Decode token to get user info. This is a simplified example.
    const decodedPayload = JSON.parse(atob(token.split('.')[1]));
    
    // Create a partial user object with only the fields available from the token payload.
    const partialUser: Partial<User> = {
      id: decodedPayload.sub,
      username: decodedPayload.username,
      role: decodedPayload.role
    };
    
    // You'll need to fetch the full user details from a backend endpoint
    // once authenticated to populate the rest of the fields (email, address, etc.).
    this.user.set(partialUser as User);
  }

  login(username: string, password: string): Observable<any> {
    return this.apiService.post<any>(`${this.authUrl}/login`, { username, password }).pipe(
      switchMap((response) => {
        this.setUserAndToken(response.token);
        this.userService.getUserProfile(username).subscribe({
          next: (fullUser) => { this.user.set(fullUser); },
        });
        return of(response);
      })
    );
  }

  register(user: User): Observable<any> {
    return this.apiService.post(`${this.usersUrl}/register`, user);
  }

  logout() {
    this.user.set(null);
    this.token.set(null);
    localStorage.removeItem('token');
  }

}