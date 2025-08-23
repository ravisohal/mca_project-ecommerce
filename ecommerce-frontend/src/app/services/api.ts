import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}`;

  get<T>(url: string, options?: object) {
    return this.http.get<T>(`${this.baseUrl}${url}`, options).pipe(
      catchError((error) => {
        console.error('API GET error:', error);
        return throwError(() => error);
      })
    );
  }

  post<T>(url: string, body: any, options?: object) {
    return this.http.post<T>(`${this.baseUrl}${url}`, body, options).pipe(
      catchError((error) => {
        console.error('API POST error:', error);
        return throwError(() => error);
      })
    );
  }

  put<T>(url: string, body: any, options?: object) {
    return this.http.put<T>(`${this.baseUrl}${url}`, body, options).pipe(
      catchError((error) => {
        console.error('API PUT error:', error);
        return throwError(() => error);
      })
    );
  }

  delete<T>(url: string, options?: object) {
    return this.http.delete<T>(`${this.baseUrl}${url}`, options).pipe(
      catchError((error) => {
        console.error('API DELETE error:', error);
        return throwError(() => error);
      })
    );
  }
  
}
