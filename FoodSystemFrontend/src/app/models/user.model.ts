export class User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  permissions: string[];

  constructor(id: number, firstName: string, lastName: string, email: string, permissions: string[]) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.permissions = permissions;
    this.id = id;
  }
}
