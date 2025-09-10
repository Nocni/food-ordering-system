export class CreateUser {
  firstName: string;
  lastName: string;
  email: string;
  permissions: string[];
  password: string;

  constructor(firstName: string, lastName: string, email: string, permissions: string[], password: string) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.permissions = permissions;
    this.password = password;
  }
}
