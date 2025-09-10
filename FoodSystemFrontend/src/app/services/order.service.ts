import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval, Subject } from 'rxjs';
import { Order } from '../models/order.model';
import { CreateOrder, OrderSearch } from '../models/create-order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';
  private orderUpdateSubject = new Subject<Order[]>();
  public orderUpdates$ = this.orderUpdateSubject.asObservable();

  constructor(private http: HttpClient) {
    // Start periodic polling for order status updates
    this.startPolling();
  }

  searchOrders(searchCriteria: OrderSearch = {}): Observable<Order[]> {
    return this.http.post<Order[]>(`${this.apiUrl}/search`, searchCriteria);
  }

  placeOrder(order: CreateOrder): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, order);
  }

  scheduleOrder(order: CreateOrder): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/schedule`, order);
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${orderId}/cancel`, {});
  }

  trackOrder(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}/track`);
  }

  private startPolling(): void {
    // Poll every 3 seconds for order updates
    interval(3000).subscribe(() => {
      this.searchOrders().subscribe({
        next: (orders) => {
          console.log('Polling update - found orders:', orders.length, orders.map(o => ({ id: o.id, status: o.status })));
          this.orderUpdateSubject.next(orders);
        },
        error: (error) => {
          console.error('Error polling orders:', error);
        }
      });
    });
  }

  // Method to manually trigger order refresh
  public refreshOrders(): void {
    this.searchOrders().subscribe({
      next: (orders) => {
        console.log('Manual refresh - found orders:', orders.length, orders.map(o => ({ id: o.id, status: o.status })));
        this.orderUpdateSubject.next(orders);
      },
      error: (error) => {
        console.error('Error refreshing orders:', error);
      }
    });
  }
}
