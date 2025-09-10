import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import {UserService} from "../services/user.service";

@Directive({
  selector: '[hasPermission]'
})
export class HasPermissionDirective {
  private permission: string = '';

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService
  ) {}

  @Input()
  set hasPermission(permission: string) {
    this.permission = permission;
    this.updateView();
  }

  private updateView(): void {
    if (this.userService.hasPermission(this.permission)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
