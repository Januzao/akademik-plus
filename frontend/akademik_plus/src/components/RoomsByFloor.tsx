import { useEffect, useState } from "react";
import RoomCard from "./RoomCard";
import { fetchRooms } from "../api/RoomsApi";
import { applyRoomFilters } from "./roomFiltersUtils";
import type { RoomFilters } from "./roomFiltersUtils";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";

interface RoomsByFloorProps {
  floorNumber: number;
  filters: RoomFilters;
}

export default function RoomsByFloor({ floorNumber, filters }: RoomsByFloorProps) {
  const [rooms, setRooms] = useState<RoomResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;

    fetchRooms()
      .then((data) => {
        if (active) setRooms(data);
      })
      .catch((err) => {
        if (active) setError(err instanceof Error ? err.message : String(err));
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  if (loading) return <div>Loading rooms...</div>;
  if (error) return <div>Error: {error}</div>;

  const filteredRooms = applyRoomFilters(
    rooms.filter((room) => Number(room.floorNumber ?? 0) === floorNumber),
    filters
  );

  if (filteredRooms.length === 0) {
    return (
      <p className="py-4 text-sm text-gray-500">
        No rooms match the selected filters on this floor.
      </p>
    );
  }

  return (
    <div className="grid gap-3 grid-cols-1 sm:grid-cols-4 lg:grid-cols-8">
      {filteredRooms.map((room) => (
        <RoomCard key={room.id} room={room} />
      ))}
    </div>
  );
}