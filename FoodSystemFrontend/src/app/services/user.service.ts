import { Injectable } from '@angular/core';
import {HttpClient, HttpStatusCode} from "@angular/common/http";
import {catchError, Observable, tap, throwError} from "rxjs";
import { User} from "../models/user.model";
import {CreateUser} from "../models/create-user.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';
  private userPermissions: string[] = [];

  constructor(private http: HttpClient) {
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    this.userPermissions = currentUser.permissions || [];
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUser(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  addUser(user: CreateUser): Observable<User> {
    return this.http.post<User>(this.apiUrl, user).pipe(
      catchError(error => {
        if (error.status === HttpStatusCode.Conflict) {
          return throwError(() => new Error('Email already exists'));
        }
        return throwError(() => new Error('Error adding user'));
      })
    );
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user).pipe(
      tap(updatedUser => {
        const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
        if (currentUser.id === updatedUser.id) {
          localStorage.setItem('user', JSON.stringify(updatedUser));
          this.userPermissions = updatedUser.permissions || [];
        }
      })
    );
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  hasPermission(permission: string): boolean {
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    this.userPermissions = currentUser.permissions || [];
    return this.userPermissions.includes(permission);
  }
}
