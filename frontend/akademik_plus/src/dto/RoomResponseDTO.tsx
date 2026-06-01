export interface RoomResponseDTO {
  id?: number;
  roomNumber?: string;
  roomType?: string;
  occupancyStatus?: string;
  occupiedPlaces?: number;
  totalPlaces?: number;
  floorNumber?: number;
}

export interface RoomRequestDTO {
  roomNumber?: string;
  roomType?: string;
  totalPlaces?: number;
  floorNumber?: number;
}