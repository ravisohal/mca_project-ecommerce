import { inject, Injectable, forwardRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}`;

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Generates HttpHeaders, including the Authorization header with a Bearer token if available.
   * @private
   * @returns {HttpHeaders} The HttpHeaders object.
   */
  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const token = this.getToken();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  get<T>(url: string, options?: object) {
    const defaultOptions = { headers: this.getHeaders() };
    const mergedOptions = { ...defaultOptions, ...options };

    return this.http.get<T>(`${this.baseUrl}${url}`, mergedOptions).pipe(
      catchError((error) => {
        console.error('API GET error:', error);
        return throwError(() => error);
      })
    );
  }

  post<T>(url: string, body: any, options?: object) {
    const defaultOptions = { headers: this.getHeaders() };
    const mergedOptions = { ...defaultOptions, ...options };

    return this.http.post<T>(`${this.baseUrl}${url}`, body, mergedOptions).pipe(
      catchError((error) => {
        console.error('API POST error:', error);
        return throwError(() => error);
      })
    );
  }

  put<T>(url: string, body: any, options?: object) {
    const defaultOptions = { headers: this.getHeaders() };
    const mergedOptions = { ...defaultOptions, ...options };

    return this.http.put<T>(`${this.baseUrl}${url}`, body, mergedOptions).pipe(
      catchError((error) => {
        console.error('API PUT error:', error);
        return throwError(() => error);
      })
    );
  }

  delete<T>(url: string, options?: object) {
    const defaultOptions = { headers: this.getHeaders() };
    const mergedOptions = { ...defaultOptions, ...options };

    return this.http.delete<T>(`${this.baseUrl}${url}`, mergedOptions).pipe(
      catchError((error) => {
        console.error('API DELETE error:', error);
        return throwError(() => error);
      })
    );
  }
}