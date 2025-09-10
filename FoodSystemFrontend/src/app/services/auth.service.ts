import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { JwtHelperService } from "@auth0/angular-jwt";
import { JwtResponse } from '../models/jwt-response.model';
import { BehaviorSubject, catchError, throwError } from "rxjs";
import { UserService } from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth/';
  private currentUserSubject = new BehaviorSubject<any>(null);

  constructor(private http: HttpClient, private router: Router, private jwtHelper: JwtHelperService, private userService: UserService) {
    const user = localStorage.getItem('user');
    if (user) {
      this.currentUserSubject.next(JSON.parse(user));
    }
    this.validateToken();
  }

  private validateToken(): void {
    const token = localStorage.getItem('token');
    if (token && this.jwtHelper.isTokenExpired(token)) {
      this.logout();
    }
  }

  login(email: string, password: string) {
    return this.http.post<JwtResponse>(`${this.apiUrl}login`, { email, password })
      .pipe(
        catchError(error => {
          if (error.status === 401) {
            console.error('Login error: ', error.message);
            return throwError(() => new Error(error.error));
          }
          return throwError(() => new Error('An error occurred during login'))
        })
      )
      .subscribe({
        next: response => {
          console.log(response);
          localStorage.setItem('token', response.jwt);
          localStorage.setItem('user', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
          
          if (!response.user.permissions || response.user.permissions.length === 0) {
            alert('Warning: You have no permissions assigned.');
          }
          
          this.router.navigate(['/orders']);
        },
        error: error => {
          console.error(error.message);
        }
      });
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    if (!token) {
      return false;
    }
    return token.split('.').length === 3 && !this.jwtHelper.isTokenExpired(token);
  }

  hasPermission(permission: string): boolean {
    return this.userService.hasPermission(permission);
  }
}
