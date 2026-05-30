export interface RoomResponseDTO {
  id?: number;
  roomNumber?: string;
  roomType?: string;
  occupancyStatus?: string;
  occupiedPlaces?: number;
  totalPlaces?: number;
  floorNumber?: number;
}