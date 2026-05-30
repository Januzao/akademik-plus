import type { MaintenanceRequestReqDTO } from "../dto/MaintenanceRequestDTO";

const API_BASE = import.meta.env?.VITE_API_BASE ?? "";
const MAINTENANCE_URL = `${API_BASE}/api/maintenence-requests`;

export async function fetchMaintenanceRequests(): Promise<MaintenanceRequestReqDTO[]> {
  const res = await fetch(MAINTENANCE_URL);
  if (!res.ok) throw new Error(`Failed to fetch maintenance requests: ${res.status}`);
  return res.json();
}

export async function fetchMaintenanceRequestById(id: number): Promise<MaintenanceRequestReqDTO> {
  const res = await fetch(`${MAINTENANCE_URL}/${id}`);
  if (!res.ok) throw new Error(`Failed to fetch request #${id}: ${res.status}`);
  return res.json();
}

export async function createMaintenanceRequest(
  data: MaintenanceRequestReqDTO
): Promise<MaintenanceRequestReqDTO> {
  const res = await fetch(MAINTENANCE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error(`Failed to create request: ${res.status}`);
  return res.json();
}

export async function deleteMaintenanceRequest(id: number): Promise<void> {
  const res = await fetch(`${MAINTENANCE_URL}/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error(`Failed to delete request #${id}: ${res.status}`);
}
