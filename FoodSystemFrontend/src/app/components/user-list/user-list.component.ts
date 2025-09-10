import {Component, OnInit} from '@angular/core';
import {User} from "../../models/user.model";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit{
  users: User[] = [];
  canCreateUsers: boolean;
  canUpdateUsers: boolean;
  canDeleteUsers: boolean;
  currentUserId: number | undefined;

  constructor(private userService: UserService, private router: Router, private authService: AuthService) {
    this.authService = authService;
    this.canCreateUsers = this.userService.hasPermission('can_create_users');
    this.canUpdateUsers = this.userService.hasPermission('can_update_users');
    this.canDeleteUsers = this.userService.hasPermission('can_delete_users');
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    this.currentUserId = currentUser.id;
  }

  updatePermissions(): void {
    this.canCreateUsers = this.userService.hasPermission('can_create_users');
    this.canUpdateUsers = this.userService.hasPermission('can_update_users');
    this.canDeleteUsers = this.userService.hasPermission('can_delete_users');
  }

  ngOnInit(): void {
    if (!this.userService.hasPermission('can_read_users')) {
      alert('You do not have permission to view users');
      this.authService.logout();
      return;
    }
    this.updatePermissions();
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe(users => {
      this.users = users;
    });
  }

  editUser(user: User): void {
    this.router.navigate(['/users/edit', user.id], { state: { user } });
  }

  deleteUser(id: number | undefined): void {
    if (id === undefined) {
      alert('Invalid user ID');
      return;
    }

    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(id).subscribe(
        () => {
          this.users = this.users.filter(user => user.id !== id);
          if (this.users.length === 0 || id === this.currentUserId) {
            this.authService.logout();
          }
        },
        error => alert('Error deleting user: ' + error)
      );
    }
  }
}
