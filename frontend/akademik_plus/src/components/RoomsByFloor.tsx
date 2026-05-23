import { useEffect, useState } from "react";
import RoomCard from "./RoomCard";
import { fetchRooms } from "../api/RoomsApi";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";

interface RoomsByFloorProps {
  floorNumber: number;
}

export default function RoomsByFloor({ floorNumber }: RoomsByFloorProps) {
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

  const filteredRooms = rooms.filter(
    (room) => Number(room.floorNumber ?? 0) === floorNumber
  );

  return (
    <div className="grid gap-3 grid-cols-10">
      {filteredRooms.map((room) => (
        <RoomCard key={room.id} room={room} />
      ))}
    </div>
  );
}