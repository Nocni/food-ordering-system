import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DishService } from '../../services/dish.service';
import { OrderService } from '../../services/order.service';
import { Dish } from '../../models/dish.model';
import { CreateOrder } from '../../models/create-order.model';

@Component({
  selector: 'app-order-create',
  templateUrl: './order-create.component.html',
  styleUrls: ['./order-create.component.css']
})
export class OrderCreateComponent implements OnInit {
  dishes: Dish[] = [];
  selectedDishes: number[] = [];
  categories: string[] = [];
  selectedCategory = '';
  isScheduled = false;
  scheduledDateTime = '';
  dishQuantities: { [dishId: number]: number } = {};

  constructor(
    private dishService: DishService,
    private orderService: OrderService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadDishes();
  }

  loadDishes(): void {
    this.dishService.getAvailableDishes().subscribe({
      next: (dishes: Dish[]) => {
        this.dishes = dishes;
        this.extractCategories();
        // Initialize quantities
        dishes.forEach(dish => {
          this.dishQuantities[dish.id] = 0;
        });
      },
      error: (error: any) => {
        console.error('Error loading dishes:', error);
        alert('Error loading dishes: ' + (error.error || error.message));
      }
    });
  }

  extractCategories(): void {
    const categorySet = new Set(this.dishes.map(dish => dish.category));
    this.categories = Array.from(categorySet);
  }

  getFilteredDishes(): Dish[] {
    if (!this.selectedCategory) {
      return this.dishes;
    }
    return this.dishes.filter(dish => dish.category === this.selectedCategory);
  }

  onDishQuantityChange(dishId: number, quantity: number): void {
    this.dishQuantities[dishId] = quantity;
    
    if (quantity > 0 && !this.selectedDishes.includes(dishId)) {
      this.selectedDishes.push(dishId);
    } else if (quantity === 0 && this.selectedDishes.includes(dishId)) {
      this.selectedDishes = this.selectedDishes.filter(id => id !== dishId);
    }
  }

  getSelectedDishesWithQuantities(): any[] {
    return this.selectedDishes.map(dishId => {
      const dish = this.dishes.find(d => d.id === dishId);
      return {
        dish: dish,
        quantity: this.dishQuantities[dishId]
      };
    });
  }

  getTotalPrice(): number {
    return this.selectedDishes.reduce((total, dishId) => {
      const dish = this.dishes.find(d => d.id === dishId);
      return total + (dish ? dish.price * this.dishQuantities[dishId] : 0);
    }, 0);
  }

  onSubmit(): void {
    if (this.selectedDishes.length === 0) {
      alert('Please select at least one dish');
      return;
    }

    // Create dish IDs array with quantities
    const dishIds: number[] = [];
    this.selectedDishes.forEach(dishId => {
      const quantity = this.dishQuantities[dishId];
      for (let i = 0; i < quantity; i++) {
        dishIds.push(dishId);
      }
    });

    const order: CreateOrder = {
      dishIds: dishIds
    };

    if (this.isScheduled) {
      if (!this.scheduledDateTime) {
        alert('Please select a scheduled date and time');
        return;
      }
      
      const scheduledDate = new Date(this.scheduledDateTime);
      const now = new Date();
      
      if (scheduledDate <= now) {
        alert('Scheduled time must be in the future');
        return;
      }
      
      order.scheduledFor = this.scheduledDateTime;
    }

    const serviceCall = this.isScheduled ? 
      this.orderService.scheduleOrder(order) : 
      this.orderService.placeOrder(order);

    serviceCall.subscribe({
      next: (createdOrder) => {
        console.log('Order created successfully:', createdOrder);
        // Trigger immediate refresh of orders
        this.orderService.refreshOrders();
        alert(`Order ${this.isScheduled ? 'scheduled' : 'placed'} successfully!`);
        this.router.navigate(['/orders']);
      },
      error: (error: any) => {
        console.error('Error creating order:', error);
        alert('Error creating order: ' + (error.error || error.message));
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/orders']);
  }
}
