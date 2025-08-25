import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api';
import { map, switchMap } from 'rxjs/operators';
import { User } from '../models/user';
import { Address } from '../models/address';

@Injectable({ providedIn: 'root' })
export class UserService {
    private apiService = inject(ApiService);
    private usersUrl = '/users';
    
    /**
     * Fetches the user profile by username.
     * @param username The username of the user.
     * @returns An Observable of the User object.
     */
    getUserProfile(username: string): Observable<User> {
        return this.apiService.get<User>(`${this.usersUrl}/username/${username}`).pipe(
            map((response: User) => response)
        );
    }

    /**
     * Updates the user's profile information.
     * @param userId The ID of the user to update.
     * @param userData The partial User object with updated fields.
     * @returns An Observable of the updated User object.
     */
    updateUserProfile(userId: number, userData: Partial<User>): Observable<User> {
        return this.apiService.put<User>(`${this.usersUrl}/${userId}`, userData).pipe(
            map((response: User) => response)
        );
    }

    /**
     * Fetches the user's address by user ID.
     * @param userId The ID of the user.
     * @returns An Observable of the Address object.
     */
    getUserAddress(userId: string): Observable<Address> {
        return this.apiService.get<Address>(`${this.usersUrl}/${userId}/address`).pipe(
            map((response: Address) => response)
        );
    }

    /**
     * Updates the user's address information.
     * @param userId The ID of the user.
     * @param addressData The Address object with updated fields.
     * @returns An Observable of the updated Address object.
     */
    updateUserAddress(userId: string, addressData: Address): Observable<Address> {
        return this.apiService.put<Address>(`${this.usersUrl}/${userId}/address`, addressData).pipe(
            map((response: Address) => response)
        );
    }

    /**
      * Updates the user's profile information.
      * @param userData The User object with updated fields.
      * @returns An Observable of the updated User object.
      */
    updateProfile(userData: User): Observable<User> {
        return this.apiService.put<User>(`${this.usersUrl}/${userData.id}`, userData).pipe(
            map((response: User) => response)
        );
    }

}