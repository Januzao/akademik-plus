import UsersIcon from "../assets/user.png";
import { getRoomStatus, STATUS_STYLES } from "../components/roomStatus";
import type { RoomResponseDTO } from "../dto/RoomResponseDTO";

interface RoomCardProps {
  room: RoomResponseDTO;
  onClick?: (room: RoomResponseDTO) => void;
}

export default function RoomCard({ room, onClick }: RoomCardProps) {
  const status = getRoomStatus(
    Number(room.occupiedPlaces ?? 0),
    Number(room.totalPlaces ?? 0));
  const s = STATUS_STYLES[status];
  const rawType = String(room.roomType ?? "Normal").trim();
  const key = rawType.toUpperCase();
  const TYPE_LABELS: Record<string, string> = {
    SINGLE: "Single",
    NORMAL: "Normal",
    SPECIAL: "Special",
  };
  const label = TYPE_LABELS[key] ?? (rawType ? rawType[0].toUpperCase() + rawType.slice(1).toLowerCase() : "Unknown");

  return (
    <button
      type="button"
      onClick={() => onClick?.(room)}
      className={`group transform transition-transform duration-200 ease-out hover:-translate-y-1 hover:shadow-lg motion-reduce:transform-none motion-reduce:transition-none flex flex-col justify-between border p-2.5 text-left transition-colors ${s.card}`}
    >
      <div className="flex items-start justify-between gap-1">
        <span className={`text-sm font-semibold leading-tight ${s.text}`}>
          Room {room.roomNumber ?? room.id}
        </span>
        <span className={`shrink-0 rounded-full border px-1.5 py-0.5 text-[10px] font-medium ${s.badge}`}>
          {label}
        </span>
      </div>

      <div className={`mt-2 flex items-center gap-1 text-xs font-medium ${s.sub}`}>
        <img src={UsersIcon} alt="users" className="h-3.5 w-3.5" />
        <span>
          {Number(room.occupiedPlaces ?? 0)}/{Number(room.totalPlaces ?? 0)}
        </span>
      </div>
    </button>
  );
}