import { apiFetch, handleResponse } from './client';

export interface BillDTO {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  roomNumber: string | null;
  issuedByName: string;
  title: string;
  description: string | null;
  amount: number;
  dueDate: string;
  issuedDate: string;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
  transactionId: string | null;
  paidDate: string | null;
  createdAt: string;
}

export interface BillCreateDTO {
  userId: number;
  title: string;
  description?: string;
  amount: number;
  dueDate: string;
}

export async function fetchBillsByUser(userId: number): Promise<BillDTO[]> {
  const res = await apiFetch(`/api/bills/user/${userId}`);
  return handleResponse<BillDTO[]>(res);
}

export async function createBill(dto: BillCreateDTO): Promise<BillDTO> {
  const res = await apiFetch('/api/bills', {
    method: 'POST',
    body: JSON.stringify(dto),
  });
  return handleResponse<BillDTO>(res);
}

export async function cancelBill(id: number): Promise<BillDTO> {
  const res = await apiFetch(`/api/bills/${id}/cancel`, { method: 'POST' });
  return handleResponse<BillDTO>(res);
}

export async function fetchMyBills(): Promise<BillDTO[]> {
  const res = await apiFetch('/api/bills/my');
  return handleResponse<BillDTO[]>(res);
}

export async function payBill(id: number): Promise<BillDTO> {
  const res = await apiFetch(`/api/bills/${id}/pay`, { method: 'POST' });
  return handleResponse<BillDTO>(res);
}
