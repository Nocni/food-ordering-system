export enum OrderStatus {
  ORDERED = 'ORDERED',
  PREPARING = 'PREPARING',
  IN_DELIVERY = 'IN_DELIVERY',
  DELIVERED = 'DELIVERED',
  CANCELED = 'CANCELED'
}

export interface Order {
  id: number;
  status: OrderStatus;
  createdById: number;
  createdByName: string;
  active: boolean;
  items: any[]; // Will be populated with dishes
  createdAt: string;
  scheduledFor?: string;
  statusUpdatedAt: string;
}
