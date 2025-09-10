export interface CreateOrder {
  dishIds: number[];
  scheduledFor?: string;
}

export interface OrderSearch {
  status?: string[];
  dateFrom?: string;
  dateTo?: string;
  userId?: number;
}
