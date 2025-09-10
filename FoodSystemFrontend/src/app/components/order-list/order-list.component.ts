import { Component, OnInit, OnDestroy } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { UserService } from '../../services/user.service';
import { Order, OrderStatus } from '../../models/order.model';
import { OrderSearch } from '../../models/create-order.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  searchCriteria: OrderSearch = {};
  users: any[] = [];
  isAdmin = false;
  orderStatuses = Object.values(OrderStatus);
  private orderUpdateSubscription?: Subscription;

  constructor(
    private orderService: OrderService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.userService.hasPermission('can_read_users');
    
    if (this.isAdmin) {
      this.loadUsers();
    }
    
    this.loadOrders();
    
    // Subscribe to automatic order updates
    this.orderUpdateSubscription = this.orderService.orderUpdates$.subscribe(
      orders => {
        console.log('OrderUpdates subscription - received orders:', orders.length, orders.map(o => ({ id: o.id, status: o.status })));
        this.orders = orders;
      }
    );
  }

  ngOnDestroy(): void {
    if (this.orderUpdateSubscription) {
      this.orderUpdateSubscription.unsubscribe();
    }
  }

  loadOrders(): void {
    this.orderService.searchOrders(this.searchCriteria).subscribe({
      next: (orders) => {
        console.log('LoadOrders - received orders:', orders.length, orders.map(o => ({ id: o.id, status: o.status, createdAt: o.createdAt })));
        this.orders = orders;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        alert('Error loading orders: ' + (error.error || error.message));
      }
    });
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users: any[]) => {
        this.users = users;
      },
      error: (error: any) => {
        console.error('Error loading users:', error);
      }
    });
  }

  onSearch(): void {
    this.loadOrders();
  }

  onCancelOrder(order: Order): void {
    if (order.status !== OrderStatus.ORDERED) {
      alert('Can only cancel orders in ORDERED status');
      return;
    }

    if (confirm('Are you sure you want to cancel this order?')) {
      this.orderService.cancelOrder(order.id).subscribe({
        next: (response) => {
          console.log('Order canceled successfully:', response);
          const message = response?.message || 'Order canceled successfully';
          alert(message);
          this.loadOrders();
        },
        error: (error) => {
          console.error('Error canceling order:', error);
          let errorMessage = 'Unknown error occurred';
          if (error.error) {
            if (typeof error.error === 'string') {
              errorMessage = error.error;
            } else if (error.error.error) {
              errorMessage = error.error.error;
            } else if (error.error.message) {
              errorMessage = error.error.message;
            } else {
              errorMessage = JSON.stringify(error.error);
            }
          } else if (error.message) {
            errorMessage = error.message;
          }
          alert('Error canceling order: ' + errorMessage);
        }
      });
    }
  }

  onTrackOrder(order: Order): void {
    this.orderService.trackOrder(order.id).subscribe({
      next: (updatedOrder) => {
        alert(`Order Status: ${updatedOrder.status}\nLast Updated: ${new Date(updatedOrder.statusUpdatedAt).toLocaleString()}`);
      },
      error: (error) => {
        console.error('Error tracking order:', error);
        alert('Error tracking order: ' + (error.error || error.message));
      }
    });
  }

  clearFilters(): void {
    this.searchCriteria = {};
    this.loadOrders();
  }

  getStatusColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.ORDERED: return 'blue';
      case OrderStatus.PREPARING: return 'orange';
      case OrderStatus.IN_DELIVERY: return 'purple';
      case OrderStatus.DELIVERED: return 'green';
      case OrderStatus.CANCELED: return 'red';
      default: return 'black';
    }
  }

  hasPermission(permission: string): boolean {
    return this.userService.hasPermission(permission);
  }

  getGroupedItems(order: Order): {name: string, price: number, quantity: number}[] {
    const itemMap = new Map<string, {name: string, price: number, quantity: number}>();
    
    order.items.forEach(item => {
      const key = `${item.name}-${item.price}`;
      if (itemMap.has(key)) {
        itemMap.get(key)!.quantity++;
      } else {
        itemMap.set(key, {
          name: item.name,
          price: item.price,
          quantity: 1
        });
      }
    });
    
    return Array.from(itemMap.values());
  }
}
