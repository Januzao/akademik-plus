import { apiFetch, handleResponse } from './client';

export interface ArrearsEntry {
  userId: number;
  name: string;
  email: string;
  roomNumber: string;
  balance: number;
  monthlyRent: number;
  deficit: number;
}

export interface AdminStatsDTO {
  totalRooms: number;
  vacantRooms: number;
  fullRooms: number;
  totalPlaces: number;
  occupiedPlaces: number;
  freePlaces: number;
  roomsByType: Record<string, number>;
  activeStudents: number;
  studentsWithoutRoom: number;
  studentsInArrears: number;
  totalArrears: number;
  arrearsDetails: ArrearsEntry[];
}

export async function fetchAdminStats(): Promise<AdminStatsDTO> {
  const res = await apiFetch('/api/admin/stats');
  return handleResponse<AdminStatsDTO>(res);
}
