import { Component, OnInit } from '@angular/core';
import { ErrorMessageService } from '../../services/error-message.service';
import { UserService } from '../../services/user.service';
import { ErrorMessage } from '../../models/error-message.model';

@Component({
  selector: 'app-error-list',
  templateUrl: './error-list.component.html',
  styleUrls: ['./error-list.component.css']
})
export class ErrorListComponent implements OnInit {
  errors: ErrorMessage[] = [];
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  isAdmin = false;

  constructor(
    private errorMessageService: ErrorMessageService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.userService.hasPermission('can_read_users');
    this.loadErrors();
  }

  loadErrors(): void {
    if (this.isAdmin) {
      // Load paginated errors for admin
      this.errorMessageService.getErrors(this.currentPage, this.pageSize).subscribe({
        next: (response: any) => {
          this.errors = response.content || response;
          this.totalPages = response.totalPages || 1;
          this.totalElements = response.totalElements || this.errors.length;
        },
        error: (error: any) => {
          console.error('Error loading errors:', error);
          // Fallback to getAllErrors if pagination endpoint doesn't exist
          this.loadAllErrors();
        }
      });
    } else {
      // Load all errors for regular users (backend should filter by user)
      this.loadAllErrors();
    }
  }

  loadAllErrors(): void {
    this.errorMessageService.getAllErrors().subscribe({
      next: (errors: ErrorMessage[]) => {
        this.errors = errors;
        this.totalElements = errors.length;
        this.totalPages = Math.ceil(errors.length / this.pageSize);
      },
      error: (error: any) => {
        console.error('Error loading errors:', error);
        alert('Error loading error messages: ' + (error.error || error.message));
      }
    });
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadErrors();
    }
  }

  getPageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }
}
