import type { RoomResponseDTO } from '../dto/RoomResponseDTO';
import { apiFetch, handleResponse } from './client';

export async function fetchRooms(): Promise<RoomResponseDTO[]> {
  const res = await apiFetch('/api/rooms');
  return handleResponse<RoomResponseDTO[]>(res).then(data => data ?? []);
}

export async function fetchRoom(id: number | string): Promise<RoomResponseDTO> {
  const res = await apiFetch(`/api/rooms/${id}`);
  return handleResponse<RoomResponseDTO>(res);
}

export async function createRoom(body: Partial<RoomResponseDTO>): Promise<RoomResponseDTO> {
  const res = await apiFetch('/api/rooms', {
    method: 'POST',
    body: JSON.stringify(body),
  });
  return handleResponse<RoomResponseDTO>(res);
}

export async function updateRoom(id: number | string, body: Partial<RoomResponseDTO>): Promise<RoomResponseDTO> {
  const res = await apiFetch(`/api/rooms/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
  return handleResponse<RoomResponseDTO>(res);
}

export async function deleteRoom(id: number | string): Promise<null> {
  const res = await apiFetch(`/api/rooms/${id}`, { method: 'DELETE' });
  return handleResponse<null>(res);
}
