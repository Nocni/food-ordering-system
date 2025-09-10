import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from "./components/login/login.component";
import { UserListComponent } from "./components/user-list/user-list.component";
import { UserAddComponent } from "./components/user-add/user-add.component";
import { UserEditComponent } from "./components/user-edit/user-edit.component";
import { OrderListComponent } from "./components/order-list/order-list.component";
import { OrderCreateComponent } from "./components/order-create/order-create.component";
import { ErrorListComponent } from "./components/error-list/error-list.component";
import { authGuard } from "./security/auth.guard";

const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { 
    path: 'users', 
    component: UserListComponent, 
    canActivate: [authGuard],
    data: { requiredPermission: 'can_read_users' }
  },
  { 
    path: 'users/add', 
    component: UserAddComponent, 
    canActivate: [authGuard],
    data: { requiredPermission: 'can_create_users' }
  },
  { 
    path: 'users/edit/:id', 
    component: UserEditComponent, 
    canActivate: [authGuard],
    data: { requiredPermission: 'can_update_users' }
  },
  { 
    path: 'orders', 
    component: OrderListComponent, 
    canActivate: [authGuard],
    data: { requiredPermission: 'can_search_order' }
  },
  { 
    path: 'orders/create', 
    component: OrderCreateComponent, 
    canActivate: [authGuard],
    data: { requiredPermission: 'can_place_order' }
  },
  { 
    path: 'errors', 
    component: ErrorListComponent, 
    canActivate: [authGuard],
    data: { requiredPermission: 'can_search_order' }
  },
  { path: '', redirectTo: '/orders', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
