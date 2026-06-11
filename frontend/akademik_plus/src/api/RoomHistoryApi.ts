import { apiFetch, handleResponse } from './client';

export interface RoomHistoryDTO {
  id: number;
  userId: number;
  tenantName: string;
  roomNumber: string;
  floorNumber?: number;
  roomType?: 'DOUBLE' | 'TRIPLE' | 'QUAD';
  rentPrice?: number;
  checkIn: string;
  checkOut?: string | null;
}

export async function fetchMyRoomHistory(): Promise<RoomHistoryDTO[]> {
  const res = await apiFetch('/api/room-history/my');
  return handleResponse<RoomHistoryDTO[]>(res).then(data => data ?? []);
}

export async function fetchAllRoomHistory(): Promise<RoomHistoryDTO[]> {
  const res = await apiFetch('/api/room-history');
  return handleResponse<RoomHistoryDTO[]>(res).then(data => data ?? []);
}

export async function fetchUserRoomHistory(userId: number): Promise<RoomHistoryDTO[]> {
  const res = await apiFetch(`/api/room-history/user/${userId}`);
  return handleResponse<RoomHistoryDTO[]>(res).then(data => data ?? []);
}
