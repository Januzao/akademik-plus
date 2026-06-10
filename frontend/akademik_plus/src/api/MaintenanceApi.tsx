import type { MaintenanceRequestReqDTO, MaintenanceRequestRespDTO } from '../dto/MaintenanceRequestDTO';
import { apiFetch, handleResponse } from './client';

const MAINTENANCE_URL = '/api/maintenance-requests';

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

export async function deleteMaintenanceRequest(id: number): Promise<null> {
  const res = await apiFetch(`${MAINTENANCE_URL}/${id}`, { method: 'DELETE' });
  return handleResponse<null>(res);
}
