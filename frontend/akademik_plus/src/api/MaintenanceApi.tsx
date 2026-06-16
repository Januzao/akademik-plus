import type { MaintenanceRequestReqDTO, MaintenanceRequestRespDTO } from '../dto/MaintenanceRequestDTO';
import { apiFetch, handleResponse } from './client';

export interface MaintenanceMessageDTO {
  id: number;
  senderName: string;
  senderRole: 'STUDENT' | 'ADMIN';
  text: string;
  createdAt: string;
}

const MAINTENANCE_URL = '/api/maintenance-requests';

export async function fetchMyMaintenanceRequests(): Promise<MaintenanceRequestRespDTO[]> {
  const res = await apiFetch(`${MAINTENANCE_URL}/my`);
  return handleResponse<MaintenanceRequestRespDTO[]>(res).then(data => data ?? []);
}

export async function fetchMaintenanceRequests(): Promise<MaintenanceRequestReqDTO[]> {
  const res = await apiFetch(MAINTENANCE_URL);
  return handleResponse<MaintenanceRequestReqDTO[]>(res).then(data => data ?? []);
}

export async function fetchMaintenanceRequestById(id: number): Promise<MaintenanceRequestRespDTO> {
  const res = await apiFetch(`${MAINTENANCE_URL}/${id}`);
  return handleResponse<MaintenanceRequestRespDTO>(res);
}

export async function createMaintenanceRequest(
  data: MaintenanceRequestReqDTO,
  userId: number
): Promise<MaintenanceRequestRespDTO> {
  const res = await apiFetch(`${MAINTENANCE_URL}?userId=${userId}`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  return handleResponse<MaintenanceRequestRespDTO>(res);
}

export async function updateMaintenanceStatus(id: number, status: string): Promise<MaintenanceRequestRespDTO> {
  const res = await apiFetch(`${MAINTENANCE_URL}/${id}/status?status=${encodeURIComponent(status)}`, {
    method: 'PATCH',
  });
  return handleResponse<MaintenanceRequestRespDTO>(res);
}

export async function uploadMaintenancePhoto(id: number, file: File): Promise<MaintenanceRequestRespDTO> {
  const form = new FormData();
  form.append('file', file);
  const token = localStorage.getItem('token');
  const res = await fetch(`${MAINTENANCE_URL}/${id}/photo`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: form,
  });
  return handleResponse<MaintenanceRequestRespDTO>(res);
}

export async function deleteMaintenanceRequest(id: number): Promise<null> {
  const res = await apiFetch(`${MAINTENANCE_URL}/${id}`, { method: 'DELETE' });
  return handleResponse<null>(res);
}

export async function fetchMessages(requestId: number): Promise<MaintenanceMessageDTO[]> {
  const res = await apiFetch(`${MAINTENANCE_URL}/${requestId}/messages`);
  return handleResponse<MaintenanceMessageDTO[]>(res).then(data => data ?? []);
}

export async function postMessage(requestId: number, text: string): Promise<MaintenanceMessageDTO> {
  const res = await apiFetch(`${MAINTENANCE_URL}/${requestId}/messages`, {
    method: 'POST',
    body: JSON.stringify({ text }),
  });
  return handleResponse<MaintenanceMessageDTO>(res);
}
