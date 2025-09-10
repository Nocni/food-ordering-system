export interface ErrorMessage {
  id: number;
  orderId?: number;
  operation: string;
  errorMessage: string;
  timestamp: string;
  userId: number;
  userName: string;
}
