// src/app/models/jwt-response.model.ts
import { User } from './user.model';

export interface JwtResponse {
  jwt: string;
  user: User;
}
