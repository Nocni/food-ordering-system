import { CanActivateFn } from '@angular/router';
import { inject } from "@angular/core";
import { AuthService } from "../services/auth.service";
import { Router } from "@angular/router";

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
    return false;
  }

  // Check if route has required permissions
  const requiredPermission = route.data['requiredPermission'];
  if (requiredPermission && !authService.hasPermission(requiredPermission)) {
    alert('You do not have permission to access this page');
    router.navigate(['/users']);
    return false;
  }

  return true;
};
