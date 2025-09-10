import { Component } from '@angular/core';
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {CreateUser} from "../../models/create-user.model";

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent {
  user: CreateUser = { firstName: '', lastName: '', email: '', permissions: [], password: ''};
  availablePermissions: string[] = [
    'can_create_users', 
    'can_read_users', 
    'can_update_users', 
    'can_delete_users',
    'can_search_order',
    'can_place_order',
    'can_cancel_order',
    'can_track_order',
    'can_schedule_order'
  ];
  errorMessage: string = '';

  constructor(private userService: UserService, private router: Router) {}

  onPermissionChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.checked) {
      this.user.permissions.push(input.value);
    } else {
      const index = this.user.permissions.indexOf(input.value);
      if (index > -1) {
        this.user.permissions.splice(index, 1);
      }
    }
  }

  addUser(): void {
    // Validate required fields
    if (!this.user.firstName?.trim() || !this.user.lastName?.trim() || 
        !this.user.email?.trim() || !this.user.password?.trim()) {
      this.errorMessage = 'All fields are required!';
      return;
    }

    if (this.user.permissions.length === 0) {
      this.errorMessage = 'At least one permission is required!';
      return;
    }

    this.userService.addUser(this.user).subscribe(() => {
      this.router.navigate(['/users']);
    }, error => {
      this.errorMessage = 'Error adding user: ' + error.message;
      console.error('Error adding user:', error);
    });
  }

}
