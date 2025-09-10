import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dish } from '../models/dish.model';

@Injectable({
  providedIn: 'root'
})
export class DishService {
  private apiUrl = 'http://localhost:8080/api/dishes';

  constructor(private http: HttpClient) { }

  getAllDishes(): Observable<Dish[]> {
    return this.http.get<Dish[]>(this.apiUrl);
  }

  getAvailableDishes(): Observable<Dish[]> {
    return this.http.get<Dish[]>(`${this.apiUrl}/available`);
  }

  getDishesByCategory(category: string): Observable<Dish[]> {
    return this.http.get<Dish[]>(`${this.apiUrl}/category/${category}`);
  }
}
