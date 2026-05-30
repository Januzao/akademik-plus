import { getRoomStatus } from "./roomStatus";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";

export interface RoomFilters {
  roomType: string;
  occupancyStatus: string;
}

export const ROOM_TYPES = ["All Types", "Single", "Normal", "Special"];
export const OCCUPANCY_STATUSES = ["All Rooms", "Empty", "Partial", "Full"];

export function applyRoomFilters(
  rooms: RoomResponseDTO[],
  filters: RoomFilters
): RoomResponseDTO[] {
  return rooms.filter((room) => {
    if (filters.roomType !== "All Types") {
      const type = String(room.roomType ?? "").trim().toUpperCase();
      if (type !== filters.roomType.toUpperCase()) return false;
    }

    if (filters.occupancyStatus !== "All Rooms") {
      const status = getRoomStatus(
        Number(room.occupiedPlaces ?? 0),
        Number(room.totalPlaces ?? 0)
      );
      if (status !== filters.occupancyStatus.toLowerCase()) return false;
    }

    return true;
  });
}
