import {Component, OnInit} from '@angular/core';
import {User} from "../../models/user.model";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit{
  user: User = { firstName: '', lastName: '', email: '', id: 0, permissions: [] };
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

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { user: User };
    if (state?.user) {
      this.user = state.user;
    } else {
      const userId = this.route.snapshot.paramMap.get('id');
      if (userId) {
        this.userService.getUser(userId).subscribe(user => {
          this.user = user;
        }, error => {
          this.errorMessage = 'Error fetching user: ' + error.message;
        });
      }
    }
  }

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

  editUser(): void {
    // Validate required fields
    if (!this.user.firstName?.trim() || !this.user.lastName?.trim() || 
        !this.user.email?.trim()) {
      this.errorMessage = 'All fields are required!';
      return;
    }

    if (this.user.permissions.length === 0) {
      this.errorMessage = 'At least one permission is required!';
      return;
    }

    if (this.user.id) {
      this.userService.updateUser(this.user.id, this.user).subscribe(() => {
        this.router.navigate(['/users']);
      }, error => {
        this.errorMessage = 'Error updating user: ' + error.message;
      });
    }
  }
}
