import { apiFetch, handleResponse } from './client';

export interface PaymentDTO {
  id: number;
  amount: number;
  paidFor: string;
  paymentDate: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  refundId?: string;
  refundedAt?: string;
  tenantId?: number;
  tenantName?: string;
  roomNumber?: string;
}

export async function fetchMyPayments(): Promise<PaymentDTO[]> {
  const res = await apiFetch('/api/payments/my');
  return handleResponse<PaymentDTO[]>(res).then(data => data ?? []);
}

export async function fetchAllPayments(): Promise<PaymentDTO[]> {
  const res = await apiFetch('/api/payments');
  return handleResponse<PaymentDTO[]>(res).then(data => data ?? []);
}
